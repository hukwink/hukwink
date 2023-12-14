package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.MessageChain
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import io.netty.buffer.ByteBuf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object MessageChainAsSingleMessageSerializer :
    MessageOuterDirectSerializer<MessageChain>,
    MessageOuterLayoutSerializer<MessageChain>,
    MessageInternalDataSerializer<MessageChain> {
    override val serialName: String get() = "hukwink.MessageChain"

    @Serializable
    @SerialName("messageChain")
    private class Delegate(
        val chain: MessageChain,
    )

    override fun deserializeDirectly(decoder: Decoder): MessageChain {
        val delegate = Delegate.serializer().deserialize(decoder)
        return delegate.chain
    }

    override fun serializeDirectly(encoder: Encoder, value: MessageChain) {
        Delegate.serializer().serialize(encoder, Delegate(value))
    }

    override val descriptor: SerialDescriptor get() = Delegate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ByteBuf = error("Unsupported")
    override fun serialize(encoder: Encoder, byteBuf: ByteBuf, message: MessageChain) = error("Unsupported")
    override val outerLayout: MessageChainAsSingleMessageSerializer get() = this

    override fun decode(buffer: ByteBuf): MessageChain = error("Unsupported")
    override fun encode(message: MessageChain, buffer: ByteBuf) = error("Unsupported")
}