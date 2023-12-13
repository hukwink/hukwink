package com.hukwink.hukwink.message

public interface MessageChain : Message {
    public val metadata: Collection<MessageMetadata>
    public val content: List<MessageElement>

    public operator fun <T> get(key: MessageMetadataKey<T>): T?
}