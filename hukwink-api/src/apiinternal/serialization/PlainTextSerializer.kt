package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.PlainText
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import io.netty.buffer.ByteBuf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

internal object PlainTextSerializer :
    MessageOuterDirectSerializer<PlainText>,
    MessageOuterLayoutSerializer<PlainText>,
    MessageInternalDataSerializer<PlainText> {
    override val serialName: String get() = "hukwink.PlainText"

    @Serializable
    @SerialName("text")
    private class Delegate(
        val content: String,
        val style: Set<PlainText.Style> = emptySet(),
    )

    override fun deserializeDirectly(decoder: Decoder): PlainText {
        val delegate = Delegate.serializer().deserialize(decoder)
        return PlainText(content = delegate.content, styles = delegate.style)
    }

    override fun serializeDirectly(encoder: Encoder, value: PlainText) {
        val delegate = Delegate(
            content = value.content.toString(),
            style = value.styles.takeIf { it.isNotEmpty() } ?: emptySet(),
        )
        Delegate.serializer().serialize(encoder, delegate)
    }

    override val descriptor: SerialDescriptor get() = Delegate.serializer().descriptor

    override fun deserialize(decoder: Decoder): ByteBuf = error("Unsupported")
    override fun serialize(encoder: Encoder, byteBuf: ByteBuf, message: PlainText) = error("Unsupported")
    override val outerLayout: PlainTextSerializer get() = this

    override fun decode(buffer: ByteBuf): PlainText {
        val style = decodeStyles(buffer)
        return PlainText(
            buffer.readCharSequence(buffer.readableBytes(), Charsets.UTF_8),
            style
        )
    }

    fun decodeStyles(buffer: ByteBuf): Set<PlainText.Style> {
        val size = buffer.readInt()
        if (size == 0) return emptySet()
        val newSet = EnumSet.noneOf(PlainText.Style::class.java)
        repeat(size) {
            newSet.add(PlainText.Style.entries[buffer.readInt()])
        }
        return newSet
    }

    fun encodeStyles(style: Set<PlainText.Style>, buffer: ByteBuf) {
        buffer.writeInt(style.size)
        style.forEach { buffer.writeInt(it.ordinal) }
    }

    override fun encode(message: PlainText, buffer: ByteBuf) {
        encodeStyles(message.styles, buffer)
        buffer.writeCharSequence(message.content, Charsets.UTF_8)
    }
}