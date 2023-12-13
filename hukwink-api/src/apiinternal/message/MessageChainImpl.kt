package com.hukwink.hukwink.apiinternal.message

import com.hukwink.hukwink.message.MessageChain
import com.hukwink.hukwink.message.MessageElement
import com.hukwink.hukwink.message.MessageMetadata
import com.hukwink.hukwink.message.MessageMetadataKey

internal class MessageChainImpl(
    private val metadataMap: Map<MessageMetadataKey<*>, MessageMetadata>,
    override val content: List<MessageElement>,
) : MessageChain {
    override val metadata: Collection<MessageMetadata> by lazy {
        metadataMap.values.toSet()
    }

    override fun <T> get(key: MessageMetadataKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return metadataMap[key] as T?
    }
}
