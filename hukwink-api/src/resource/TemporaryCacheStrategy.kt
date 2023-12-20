package com.hukwink.hukwink.resource

import com.hukwink.hukwink.apiinternal.misc.AccessibleByteArrayOutputStream
import com.hukwink.hukwink.apiinternal.resource.LocalFileSystemLocalResource
import com.hukwink.hukwink.apiinternal.resource.LocalResourceGlobalValues.builtinLeakObservable
import com.hukwink.hukwink.resource.LocalResource.Companion.withFileName
import java.io.*
import java.nio.file.Path
import java.util.*
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

public abstract class TemporaryCacheStrategy {
    public companion object {

    }

    @Throws(IOException::class)
    public abstract fun cache(fileName: String, source: InputStream): LocalResource


    /// builtin implementations

    public class InMemory : TemporaryCacheStrategy() {
        override fun cache(fileName: String, source: InputStream): LocalResource {
            val holder = AccessibleByteArrayOutputStream()
            source.copyTo(holder)

            return LocalResource.wrap(fileName, content = holder.content, length = holder.len)
        }
    }

    public class InTemporaryDirectory(
        private val directory: Path,
    ) : TemporaryCacheStrategy() {
        internal fun nextTemporaryFile() = generateSequence { UUID.randomUUID().toString() }
            .map { directory.resolve(it) }
            .filter { it.notExists() }
            .first()

        override fun cache(fileName: String, source: InputStream): LocalResource {
            val nextTemporaryFile = nextTemporaryFile()

            if (!directory.exists()) {
                directory.createDirectories()
            }

            nextTemporaryFile.outputStream().use { source.copyTo(it) }

            return LocalFileSystemLocalResource(
                path = nextTemporaryFile,
                deleteOnClose = true,
            ).withFileName(fileName).builtinLeakObservable()
        }
    }

    public class BalancedBetweenMemoryAndLocal(
        private val memoryMax: Int = 512 * 1024 * 1024,
        private val directory: Path,
    ) : TemporaryCacheStrategy() {
        private val helper = InTemporaryDirectory(directory)

        override fun cache(fileName: String, source: InputStream): LocalResource {
            class BalancedOutputStream(
                delegate: OutputStream
            ) : FilterOutputStream(delegate) {
                var out0 by this::out
            }

            val wrapper = BalancedOutputStream(OutputStream.nullOutputStream())
            val inMemory = object : AccessibleByteArrayOutputStream() {
                val targetFile by lazy { helper.nextTemporaryFile() }

                override fun write(b: Int) {
                    super.write(b)
                    convert()
                }

                override fun write(b: ByteArray, off: Int, len: Int) {
                    super.write(b, off, len)
                    convert()
                }

                private fun convert() {
                    if (count < memoryMax) return

                    if (!directory.exists()) {
                        directory.createDirectories()
                    }
                    wrapper.out0 = targetFile.outputStream()

                    writeTo(wrapper)

                    buf = ByteArray(0)
                    count = 0
                }
            }
            wrapper.out0 = inMemory

            wrapper.use { source.copyTo(wrapper) }

            return if (wrapper.out0 === inMemory) {
                LocalResource.wrap(fileName, content = inMemory.content, length = inMemory.len)
            } else {
                LocalFileSystemLocalResource(
                    path = inMemory.targetFile,
                    deleteOnClose = true,
                ).withFileName(fileName).builtinLeakObservable()
            }
        }
    }
}