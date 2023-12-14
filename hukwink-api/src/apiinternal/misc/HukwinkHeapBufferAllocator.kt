package com.hukwink.hukwink.apiinternal.misc

import io.netty.buffer.AbstractByteBufAllocator
import io.netty.buffer.ByteBuf
import io.netty.buffer.UnpooledHeapByteBuf

internal object HukwinkHeapBufferAllocator : AbstractByteBufAllocator(false) {
    override fun isDirectBufferPooled(): Boolean = false
    override fun newHeapBuffer(initialCapacity: Int, maxCapacity: Int): ByteBuf {
        return UnpooledHeapByteBuf(this, initialCapacity, maxCapacity)
    }

    override fun newDirectBuffer(initialCapacity: Int, maxCapacity: Int): ByteBuf {
        return UnpooledHeapByteBuf(this, initialCapacity, maxCapacity)
    }
}