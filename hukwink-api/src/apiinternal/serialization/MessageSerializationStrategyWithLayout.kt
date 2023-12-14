package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.apiinternal.misc.HukwinkHeapBufferAllocator
import com.hukwink.hukwink.message.Message
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import io.netty.buffer.Unpooled
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder

internal class MessageSerializationStrategyWithLayout(
    private val serializer: MessageInternalDataSerializer<*>
) : SerializationStrategy<Message> {
    override val descriptor: SerialDescriptor
        get() = serializer.outerLayout.descriptor

    override fun serialize(encoder: Encoder, value: Message) {
        if (serializer is MessageOuterDirectSerializer<*>) {
            @Suppress("UNCHECKED_CAST")
            (serializer as MessageOuterDirectSerializer<Message>).serializeDirectly(encoder, value)
            return
        }

        val buffer = HukwinkHeapBufferAllocator.buffer()

        @Suppress("UNCHECKED_CAST")
        (serializer as MessageInternalDataSerializer<Message>)
            .encode(message = value, buffer = buffer)

        val messageHeader = HukwinkHeapBufferAllocator.buffer()
        messageHeader.writeInt(0) // length placeholder
        val written = messageHeader.writeCharSequence(serializer.serialName, Charsets.UTF_8)

        messageHeader
            .markWriterIndex()
            .writerIndex(0).writeInt(written)
            .resetWriterIndex()

        val mergedData = Unpooled.wrappedBuffer(messageHeader, buffer)

        serializer.outerLayout.serialize(encoder, mergedData, value)
    }
}