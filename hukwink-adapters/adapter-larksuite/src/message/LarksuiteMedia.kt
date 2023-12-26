package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.adapter.larksuite.message.image.LarksuiteImageFromChat
import com.hukwink.hukwink.adapter.larksuite.resource.FromMessageResource
import com.hukwink.hukwink.message.Media
import com.hukwink.hukwink.message.MessageElement
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.resource.ExternalResource
import com.hukwink.hukwink.util.decodeCharSequence
import com.hukwink.hukwink.util.encodeCharSequence
import io.netty.buffer.ByteBuf

internal class LarksuiteMedia(
    override val fileKey: String,
    val imageKey: String,
    override val fileName: String,
    val duration: Long,
    override val messageId: String,
) : FromMessageResource(), MessageElement, Media {

    override fun openCoverImage(bot: Bot): ExternalResource.ResourceHandle {
        return LarksuiteImageFromChat(imageKey, messageId).openResource(bot)
    }

    override fun contentToString(): String = "[media:$fileKey]"
    override fun toString(): String = "[media:$fileKey, $messageId]"

    internal object Serializer : MessageInternalDataSerializer<LarksuiteMedia> {
        override val serialName: String get() = "larksuite.Media"
        override val outerLayout: MessageOuterLayoutSerializer<LarksuiteMedia>
            get() = Media.outerLayout()

        override fun decode(buffer: ByteBuf): LarksuiteMedia {
            val fileKey = buffer.decodeCharSequence().toString()
            val imageKey = buffer.decodeCharSequence().toString()
            val fileName = buffer.decodeCharSequence().toString()
            val messageId = buffer.decodeCharSequence().toString()
            val duration = buffer.readLong()

            return LarksuiteMedia(
                fileKey, imageKey, fileName, duration, messageId
            )
        }

        override fun encode(message: LarksuiteMedia, buffer: ByteBuf) {
            buffer.encodeCharSequence(message.fileKey)
            buffer.encodeCharSequence(message.imageKey)
            buffer.encodeCharSequence(message.fileName)
            buffer.encodeCharSequence(message.messageId)
            buffer.writeLong(message.duration)
        }

    }
}