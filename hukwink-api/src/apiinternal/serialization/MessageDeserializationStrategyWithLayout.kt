package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Message
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder

internal class MessageDeserializationStrategyWithLayout(
    private val outerLayoutSerializer: MessageOuterLayoutSerializer<*>,
    private val serializers: Map<String, MessageInternalDataSerializer<*>>,
) : DeserializationStrategy<Message> {
    override val descriptor: SerialDescriptor
        get() = outerLayoutSerializer.descriptor

    override fun deserialize(decoder: Decoder): Message {
        if (outerLayoutSerializer is MessageOuterDirectSerializer<*>) {
            return outerLayoutSerializer.deserializeDirectly(decoder)
        }

        val internalMsg = outerLayoutSerializer.deserialize(decoder)
        if (!internalMsg.isReadable(4)) {
            throw SerializationException("Failed to parse internal serializer from internal message")
        }
        val serialName = internalMsg.readCharSequence(internalMsg.readInt(), Charsets.UTF_8)

        val specialSerializer =
            serializers[serialName] ?: throw SerializationException("Unknown serializer for $serialName")
        return specialSerializer.decode(internalMsg)
    }
}