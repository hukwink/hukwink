package com.hukwink.hukwink.message

import com.hukwink.hukwink.apiinternal.serialization.UnknownOuterLayout
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer

public interface Message {
    public fun contentToString(): String
    public override fun toString(): String

    public companion object Key {
        @JvmStatic
        public fun unknownOuterLayout(): MessageOuterLayoutSerializer<Message> = UnknownOuterLayout
    }
}