package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.apiinternal.misc.encodeCharSequence
import com.hukwink.hukwink.message.Hyperlink
import com.hukwink.hukwink.message.PlainText
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import io.netty.buffer.ByteBuf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object HyperlinkSerializer :
    MessageOuterDirectSerializer<Hyperlink>,
    MessageOuterLayoutSerializer<Hyperlink>,
    MessageInternalDataSerializer<Hyperlink> {
    override val serialName: String get() = "hukwink.Hyperlink"

    @Serializable
    @SerialName("hyperlink")
    private class Delegate(
        val content: String,
        val hyperlink: String,
        val style: Set<PlainText.Style> = emptySet(),
    )

    override fun deserializeDirectly(decoder: Decoder): Hyperlink {
        val delegate = Delegate.serializer().deserialize(decoder)
        return Hyperlink(
            content = delegate.content,
            hyperlink = delegate.hyperlink,
            styles = delegate.style
        )
    }

    override fun serializeDirectly(encoder: Encoder, value: Hyperlink) {
        val delegate = Delegate(
            content = value.content.toString(),
            hyperlink = value.hyperlink.toString(),
            style = value.styles.takeIf { it.isNotEmpty() } ?: emptySet(),
        )
        Delegate.serializer().serialize(encoder, delegate)
    }

    override val descriptor: SerialDescriptor get() = Delegate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ByteBuf = error("Unsupported")
    override fun serialize(encoder: Encoder, byteBuf: ByteBuf, message: Hyperlink) = error("Unsupported")
    override val outerLayout: HyperlinkSerializer get() = this

    override fun decode(buffer: ByteBuf): Hyperlink {
        val style = PlainTextSerializer.decodeStyles(buffer)
        val hyperlink = buffer.readCharSequence(buffer.readInt(), Charsets.UTF_8)
        val content = buffer.readCharSequence(buffer.readableBytes(), Charsets.UTF_8)
        return Hyperlink(
            content = content,
            hyperlink = hyperlink,
            styles = style,
        )
    }

    override fun encode(message: Hyperlink, buffer: ByteBuf) {
        PlainTextSerializer.encodeStyles(message.styles, buffer)
        buffer.encodeCharSequence(message.hyperlink)

        buffer.writeCharSequence(message.content, Charsets.UTF_8)
    }
}