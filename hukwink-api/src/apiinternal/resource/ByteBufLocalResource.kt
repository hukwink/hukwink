package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.AbstractLocalResource
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import java.io.InputStream

internal class ByteBufLocalResource(
    private val byteBuf: ByteBuf,
    override val fileName: String,
) : AbstractLocalResource() {
    override val size: Long
        get() = byteBuf.readableBytes().toLong()

    override fun openStream(): InputStream {
        return ByteBufInputStream(byteBuf)
    }

    override fun close0() {
        byteBuf.release()
    }
}