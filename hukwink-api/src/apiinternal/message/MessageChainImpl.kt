package com.hukwink.hukwink.apiinternal.message

import com.hukwink.hukwink.message.MessageChain
import com.hukwink.hukwink.message.MessageElement
import com.hukwink.hukwink.message.MessageMetadata
import com.hukwink.hukwink.message.MessageMetadataKey
import com.hukwink.hukwink.message.MessageUtil.asSequence

internal class MessageChainImpl(
    private val metadataMap: Map<MessageMetadataKey<*>, MessageMetadata>,
    override val content: List<MessageElement>,
) : MessageChain {
    override val metadata: List<MessageMetadata> by lazy {
        metadataMap.values.asSequence().distinct().toList()
    }

    override fun <T> get(key: MessageMetadataKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return metadataMap[key] as T?
    }


    private val toStringCache by lazy(LazyThreadSafetyMode.PUBLICATION) {
        return@lazy asSequence().joinToString { it.toString() }
    }
    private val contentToStringCache by lazy(LazyThreadSafetyMode.PUBLICATION) {
        return@lazy content.joinToString { it.contentToString() }
    }

    override fun contentToString(): String = contentToStringCache
    override fun toString(): String = toStringCache
}
