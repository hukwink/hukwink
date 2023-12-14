package com.hukwink.hukwink.message

import com.hukwink.hukwink.apiinternal.serialization.AudioOuterLayout
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.resource.ExternalResource

public interface Audio : ExternalResource, MessageElement {
    public companion object Key {
        @JvmStatic
        public fun outerLayout(): MessageOuterLayoutSerializer<Audio> = AudioOuterLayout
    }
}
