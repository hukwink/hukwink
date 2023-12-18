package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.AbstractLocalResource
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.fileSize

internal class LocalFileSystemLocalResource(
    private val path: Path,
) : AbstractLocalResource() {
    override val fileName: String by lazy {
        path.fileName.toString()
    }
    override val size: Long by lazy {
        path.fileSize()
    }

    override fun openStream(): InputStream = Files.newInputStream(path)
    override fun close0() = Unit
}