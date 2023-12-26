package com.hukwink.hukwink.adapter.larksuite.message.serialization

import com.hukwink.hukwink.adapter.larksuite.message.LarksuiteMessageTitle
import com.hukwink.hukwink.apiinternal.serialization.MessageOuterDirectSerializer
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.util.decodeCharSequence
import com.hukwink.hukwink.util.encodeCharSequence
import io.netty.buffer.ByteBuf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object LarksuiteMessageTitleSerializer :
    MessageOuterDirectSerializer<LarksuiteMessageTitle>,
    MessageOuterLayoutSerializer<LarksuiteMessageTitle>,
    MessageInternalDataSerializer<LarksuiteMessageTitle> {
    override val serialName: String get() = "hukwink.MessageTitle"

    @Serializable
    @SerialName("larksuiteMessageTitle")
    private class Delegate(
        val title: String,
    )

    override fun deserializeDirectly(decoder: Decoder): LarksuiteMessageTitle {
        val delegate = Delegate.serializer().deserialize(decoder)
        return LarksuiteMessageTitle(title = delegate.title)
    }

    override fun serializeDirectly(encoder: Encoder, value: LarksuiteMessageTitle) {
        val delegate = Delegate(value.title)
        Delegate.serializer().serialize(encoder, delegate)
    }

    override val descriptor: SerialDescriptor get() = Delegate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ByteBuf = error("Unsupported")
    override fun serialize(encoder: Encoder, byteBuf: ByteBuf, message: LarksuiteMessageTitle) = error("Unsupported")
    override val outerLayout: LarksuiteMessageTitleSerializer get() = this

    override fun decode(buffer: ByteBuf): LarksuiteMessageTitle {
        return LarksuiteMessageTitle(buffer.decodeCharSequence().toString())
    }

    override fun encode(message: LarksuiteMessageTitle, buffer: ByteBuf) {
        buffer.encodeCharSequence(message.title)
    }
}