package com.hukwink.hukwink.message.serialization

import com.hukwink.hukwink.message.Message
import io.netty.buffer.ByteBuf

public interface MessageInternalDataSerializer<T : Message> {
    public val serialName: String get() = javaClass.name

    public val outerLayout: MessageOuterLayoutSerializer<T>

    public fun encode(message: T, buffer: ByteBuf)
    public fun decode(buffer: ByteBuf): T
}
