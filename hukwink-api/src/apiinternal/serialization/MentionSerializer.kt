package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Mention
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import io.netty.buffer.ByteBuf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object MentionSerializer :
    MessageOuterDirectSerializer<Mention>,
    MessageOuterLayoutSerializer<Mention>,
    MessageInternalDataSerializer<Mention> {
    override val serialName: String get() = "hukwink.Mention"

    @Serializable
    @SerialName("mention")
    private class Delegate(
        val target: String,
    )

    override fun deserializeDirectly(decoder: Decoder): Mention {
        val delegate = Delegate.serializer().deserialize(decoder)
        return Mention(target = delegate.target)
    }

    override fun serializeDirectly(encoder: Encoder, value: Mention) {
        Delegate.serializer().serialize(encoder, Delegate(value.target))
    }

    override val descriptor: SerialDescriptor get() = Delegate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ByteBuf = error("Unsupported")
    override fun serialize(encoder: Encoder, byteBuf: ByteBuf, message: Mention) = error("Unsupported")
    override val outerLayout: MentionSerializer get() = this

    override fun decode(buffer: ByteBuf): Mention {
        return Mention(buffer.readCharSequence(buffer.readableBytes(), Charsets.UTF_8).toString())
    }

    override fun encode(message: Mention, buffer: ByteBuf) {
        buffer.writeCharSequence(message.target, Charsets.UTF_8)
    }
}