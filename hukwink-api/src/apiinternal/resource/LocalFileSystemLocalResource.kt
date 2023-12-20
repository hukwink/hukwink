package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.AbstractLocalResource
import com.hukwink.hukwink.resource.LocalResourceOrigin
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.fileSize

internal class LocalFileSystemLocalResource(
    private val path: Path,
    private val deleteOnClose: Boolean = false,
) : AbstractLocalResource() {
    override val fileName: String by lazy {
        path.fileName.toString()
    }
    override val size: Long by lazy {
        path.fileSize()
    }

    override val origin: LocalResourceOrigin = LocalResourceOrigin.FromFile(path)

    override fun openStream(): InputStream = Files.newInputStream(path)
    override fun close0() {
        if (deleteOnClose) {
            path.deleteIfExists()
        }
    }

    override fun toString(): String {
        return "LocalFileResource[$path]"
    }
}