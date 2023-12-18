package com.hukwink.hukwink.resource

import com.hukwink.hukwink.apiinternal.resource.*
import com.hukwink.hukwink.apiinternal.resource.LocalResourceGlobalValues.builtinLeakObservable
import io.netty.buffer.ByteBuf
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface LocalResource : Closeable {
    public val fileName: String
    public val sha1: ByteArray
    public val md5: ByteArray

    public val size: Long
    public val isAutoClosable: Boolean get() = false
    public val isLeakObservable: Boolean get() = false


    @Throws(IOException::class)
    public fun openStream(): InputStream

    public fun toAutoClosable(): LocalResource {
        return AutoClosableLocalResource.of(this)
    }

    public fun withLeakObserver(): LocalResource {
        return LeakSafeLocalResource.of(this)
    }

    public companion object {
        @JvmStatic
        public fun LocalResource.withFileName(name: String): LocalResource {
            if (fileName == name) return this
            return NameChangedLocalResource(this, name)
        }

        @JvmStatic
        @JvmOverloads
        public fun wrap(
            fileName: String,
            content: ByteArray,
            offset: Int = 0,
            length: Int = content.size,
        ): LocalResource {
            return ByteArrayLocalResource(content = content, offset = offset, length = length, fileName = fileName)
        }

        @JvmStatic
        public fun wrap(fileName: String, buffer: ByteBuf): LocalResource {
            return ByteBufLocalResource(fileName = fileName, byteBuf = buffer)
        }


        @JvmStatic
        public fun open(fileName: String, buffer: ByteBuf): LocalResource {
            return ByteBufLocalResource(fileName = fileName, byteBuf = buffer).builtinLeakObservable()
        }

        @JvmStatic
        public fun open(path: Path): LocalResource = LocalFileSystemLocalResource(path).builtinLeakObservable()

        @JvmStatic
        public fun open(file: File): LocalResource = open(file.toPath())
    }
}

@OptIn(ExperimentalContracts::class)
@JvmSynthetic
public inline fun <T> LocalResource.withAutoUse(block: (LocalResource) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.autoCloseFinally(exception)
    }
}

@SinceKotlin("1.2")
@PublishedApi
internal fun LocalResource?.autoCloseFinally(cause: Throwable?) {
    if (this == null) return
    if (!isAutoClosable) return

    when {
        cause == null -> close()
        else ->
            try {
                close()
            } catch (closeException: Throwable) {
                cause.addSuppressed(closeException)
            }
    }
}
