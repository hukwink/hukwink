package com.hukwink.hukwink.message

import com.hukwink.hukwink.apiinternal.serialization.MessageSourceOuterLayout
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer

public interface MessageSource : MessageMetadata {
    public companion object Key : MessageMetadataKey<MessageSource>() {
        @JvmStatic
        public fun outerLayout(): MessageOuterLayoutSerializer<MessageSource> = MessageSourceOuterLayout
    }


    public val messageId: String
    public fun reply(): MessageQuoteReply
}
