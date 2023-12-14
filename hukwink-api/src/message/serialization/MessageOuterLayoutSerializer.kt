package com.hukwink.hukwink.message.serialization

import com.hukwink.hukwink.message.Message
import io.netty.buffer.ByteBuf
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public interface MessageOuterLayoutSerializer<in T : Message> {
    public val descriptor: SerialDescriptor
    public fun serialize(encoder: Encoder, byteBuf: ByteBuf, message: T)
    public fun deserialize(decoder: Decoder): ByteBuf
}
