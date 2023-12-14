package com.hukwink.hukwink.apiinternal.serialization

import io.netty.buffer.ByteBufUtil
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object ByteArrayHexStringSerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): ByteArray {
        return ByteBufUtil.decodeHexDump(String.serializer().deserialize(decoder))
    }

    override fun serialize(encoder: Encoder, value: ByteArray) {
        String.serializer().serialize(encoder, ByteBufUtil.hexDump(value))
    }
}