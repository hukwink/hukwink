package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.Message
import com.hukwink.hukwink.message.MessageElement
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.util.decodeCharSequence
import com.hukwink.hukwink.util.encodeCharSequence
import io.netty.buffer.ByteBuf

public class LarksuiteSticker(
    public val fileKey: String,
) : MessageElement {
    override fun contentToString(): String {
        return "[sticker:$fileKey]"
    }

    override fun toString(): String {
        return "[sticker:$fileKey]"
    }


    internal object Serializer : MessageInternalDataSerializer<LarksuiteSticker> {
        override val serialName: String get() = "larksuite.Sticker"
        override val outerLayout: MessageOuterLayoutSerializer<LarksuiteSticker>
            get() = Message.unknownOuterLayout()

        override fun decode(buffer: ByteBuf): LarksuiteSticker {
            val fileKey = buffer.decodeCharSequence().toString()

            return LarksuiteSticker(fileKey)
        }

        override fun encode(message: LarksuiteSticker, buffer: ByteBuf) {
            buffer.encodeCharSequence(message.fileKey)
        }

    }
}