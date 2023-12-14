package com.hukwink.hukwink.message

import com.hukwink.hukwink.message.MessageUtil.asSequence
import com.hukwink.hukwink.message.MessageUtil.toMessageChain
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(MessageChain.Serializer::class)
public interface MessageChain : Message {
    public val metadata: Collection<MessageMetadata>
    public val content: List<MessageElement>

    public operator fun <T> get(key: MessageMetadataKey<T>): T?

    public object Serializer : KSerializer<MessageChain> {
        private val polymorphicSerializer = PolymorphicSerializer(Message::class)
        private val delegateSerializer = ListSerializer(polymorphicSerializer)
        override val descriptor: SerialDescriptor = delegateSerializer.descriptor

        override fun deserialize(decoder: Decoder): MessageChain {
            return delegateSerializer.deserialize(decoder).toMessageChain()
        }

        override fun serialize(encoder: Encoder, value: MessageChain) {
            delegateSerializer.serialize(encoder, value.asSequence().toList())
        }
    }
}
