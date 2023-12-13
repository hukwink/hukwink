package com.hukwink.hukwink.message

public interface MessageQuoteReply : MessageMetadata {
    public companion object Key : MessageMetadataKey<MessageQuoteReply>()

    public val replyMessageId: String
}
