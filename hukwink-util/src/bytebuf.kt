@file:JvmMultifileClass
@file:JvmName("HukwinkUtilKt")

package com.hukwink.hukwink.util

import io.netty.buffer.ByteBuf
import java.nio.charset.Charset

public fun ByteBuf.encodeCharSequence(content: CharSequence, charset: Charset = Charsets.UTF_8) {
    val lenPos = writerIndex()
    writeInt(0)
    val contentLength = writeCharSequence(content, charset)
    val endPos = writerIndex()

    writerIndex(lenPos).writeInt(contentLength).writerIndex(endPos)
}

public fun ByteBuf.decodeCharSequence(charset: Charset = Charsets.UTF_8): CharSequence {
    return readCharSequence(readInt(), charset)
}