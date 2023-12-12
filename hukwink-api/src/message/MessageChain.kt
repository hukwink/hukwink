package com.hukwink.hukwink.message

public interface MessageChain : Message {
    public val metadata: List<MessageMetadata>
    public val content: List<MessageElement>
}