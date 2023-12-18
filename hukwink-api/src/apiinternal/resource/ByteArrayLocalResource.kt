package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.AbstractLocalResource
import java.io.ByteArrayInputStream
import java.io.InputStream

internal class ByteArrayLocalResource(
    override val fileName: String,

    val content: ByteArray,
    val offset: Int = 0,
    val length: Int = content.size,
) : AbstractLocalResource() {
    override val size: Long get() = length.toLong()

    override fun openStream(): InputStream = ByteArrayInputStream(content, offset, length)
    override fun close0() = Unit
}