package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.adapter.larksuite.resource.FromMessageResource
import com.hukwink.hukwink.message.Audio
import com.hukwink.hukwink.message.MessageElement
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.util.decodeCharSequence
import com.hukwink.hukwink.util.encodeCharSequence
import io.netty.buffer.ByteBuf

internal class LarksuiteAudio(
    override val fileKey: String,
    public val duration: Long,
    override val messageId: String,
) : FromMessageResource(), MessageElement, Audio {
    override fun contentToString(): String = "[audio:$fileKey]"
    override fun toString(): String = "[audio:$fileKey, $messageId]"


    internal object Serializer : MessageInternalDataSerializer<LarksuiteAudio> {
        override val serialName: String get() = "larksuite.Audio"
        override val outerLayout: MessageOuterLayoutSerializer<LarksuiteAudio> get() = Audio.outerLayout()

        override fun decode(buffer: ByteBuf): LarksuiteAudio {
            val fileKey = buffer.decodeCharSequence()
            val messageId = buffer.decodeCharSequence()
            val duration = buffer.readLong()

            return LarksuiteAudio(
                fileKey = fileKey.toString(),
                messageId = messageId.toString(),
                duration = duration,
            )
        }

        override fun encode(message: LarksuiteAudio, buffer: ByteBuf) {
            buffer.encodeCharSequence(message.fileKey)
            buffer.encodeCharSequence(message.messageId)
            buffer.writeLong(message.duration)
        }
    }
}