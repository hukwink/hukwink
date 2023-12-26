package com.hukwink.hukwink.message

import com.hukwink.hukwink.apiinternal.serialization.EmotionOuterLayout
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer

public interface Emotion : MessageElement {
    public val emotionType: String

    public companion object Key {
        @JvmStatic
        public fun outerLayout(): MessageOuterLayoutSerializer<Emotion> = EmotionOuterLayout
    }
}