package com.hukwink.hukwink.message.serialization

import com.hukwink.hukwink.apiinternal.serialization.*
import com.hukwink.hukwink.message.Hyperlink
import com.hukwink.hukwink.message.Mention
import com.hukwink.hukwink.message.Message
import com.hukwink.hukwink.message.PlainText
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule

public class HukwinkMessageSerialization
private constructor(
    private val registeredSerializers: MutableMap<Class<*>, MessageInternalDataSerializer<*>>,
    private val reversedSerializers: MutableMap<String, MessageInternalDataSerializer<*>>,
    private val outerSerialNameSerializers: MutableMap<String, MessageOuterLayoutSerializer<*>>,
) {
    public constructor() : this(mutableMapOf(), mutableMapOf(), mutableMapOf())

    public val serializersModule: SerializersModule = SerializersModule {
        polymorphicDefaultSerializer(Message::class, SerializerProvider())
        polymorphicDefaultDeserializer(Message::class, DeserializerProvider())
    }

    @OptIn(ExperimentalSerializationApi::class)
    public fun <T : Message> register(type: Class<T>, serializer: MessageInternalDataSerializer<T>) {
        if (registeredSerializers.putIfAbsent(type, serializer) != null) {
            error("Type $type was already registered")
        }

        if (reversedSerializers.putIfAbsent(serializer.serialName, serializer) != null) {
            registeredSerializers.remove(type, serializer)
            error("Serial name ${serializer.serialName} was already registered")
        }

        val outerLayout = serializer.outerLayout
        val registeredOuterLayout =
            outerSerialNameSerializers.putIfAbsent(outerLayout.descriptor.serialName, outerLayout)
        if (registeredOuterLayout != null && registeredOuterLayout !== outerLayout) {
            registeredSerializers.remove(type, serializer)
            reversedSerializers.remove(serializer.serialName, serializer)


            error("Conflict outer layout ${outerLayout.descriptor.serialName}")
        }

    }

    public fun registerDefaults() {
        register(PlainText::class.java, PlainTextSerializer)
        register(Hyperlink::class.java, HyperlinkSerializer)
        register(Mention::class.java, MentionSerializer)
    }

    public fun mirror(): HukwinkMessageSerialization {
        return HukwinkMessageSerialization(
            registeredSerializers = registeredSerializers,
            reversedSerializers = reversedSerializers,
            outerSerialNameSerializers = outerSerialNameSerializers,
        )
    }

    public fun copy(): HukwinkMessageSerialization {
        return HukwinkMessageSerialization(
            registeredSerializers = registeredSerializers.toMutableMap(),
            reversedSerializers = reversedSerializers.toMutableMap(),
            outerSerialNameSerializers = outerSerialNameSerializers.toMutableMap(),
        )
    }

    private fun getSerializer(type: Class<*>): MessageInternalDataSerializer<*>? =
        generateSequence(type) { type.superclass }
            .map { registeredSerializers[it] }
            .first()

    private inner class SerializerProvider : (Message) -> SerializationStrategy<Message>? {
        override fun invoke(p1: Message): SerializationStrategy<Message>? {
            val innerSerializer = getSerializer(p1.javaClass) ?: return null

            return MessageSerializationStrategyWithLayout(innerSerializer)
        }
    }


    private inner class DeserializerProvider : (String?) -> DeserializationStrategy<Message>? {
        override fun invoke(p1: String?): DeserializationStrategy<Message>? {
            val outerLayoutSerializer = outerSerialNameSerializers[p1 ?: return null] ?: return null

            return MessageDeserializationStrategyWithLayout(outerLayoutSerializer, reversedSerializers)
        }
    }
}
