package com.hukwink.hukwink.message

import com.hukwink.hukwink.apiinternal.serialization.QuoteReplyOuterLayout
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer

public interface MessageQuoteReply : MessageMetadata {
    public companion object Key : MessageMetadataKey<MessageQuoteReply>() {
        @JvmStatic
        public fun outerLayout(): MessageOuterLayoutSerializer<MessageQuoteReply> = QuoteReplyOuterLayout
    }

    public val replyMessageId: String
}
