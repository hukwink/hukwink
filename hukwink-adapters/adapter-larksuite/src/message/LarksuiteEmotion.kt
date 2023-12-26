package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.Emotion
import com.hukwink.hukwink.message.MessageElement
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.util.decodeCharSequence
import com.hukwink.hukwink.util.encodeCharSequence
import io.netty.buffer.ByteBuf

public class LarksuiteEmotion(
    public val emojiType: String,
) : MessageElement, Emotion {
    override val emotionType: String
        get() = emojiType

    override fun toString(): String {
        return "[emotion:$emojiType]"
    }

    override fun contentToString(): String {
        return toString()
    }

    internal object Serializer : MessageInternalDataSerializer<LarksuiteEmotion> {
        override val serialName: String get() = "larksuite.Emotion"
        override val outerLayout: MessageOuterLayoutSerializer<LarksuiteEmotion>
            get() = Emotion.outerLayout()

        override fun decode(buffer: ByteBuf): LarksuiteEmotion {
            return LarksuiteEmotion(buffer.decodeCharSequence().toString())
        }

        override fun encode(message: LarksuiteEmotion, buffer: ByteBuf) {
            buffer.encodeCharSequence(message.emojiType)
        }

    }
}