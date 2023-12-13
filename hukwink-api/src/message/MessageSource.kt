package com.hukwink.hukwink.message

public interface MessageSource : MessageMetadata {
    public companion object Key : MessageMetadataKey<MessageSource>()


    public val messageId: String
    public fun reply(): MessageQuoteReply
}
