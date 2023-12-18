package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.adapter.larksuite.message.image.LarksuiteImageFromChat
import com.hukwink.hukwink.adapter.larksuite.resource.FromMessageResource
import com.hukwink.hukwink.message.Media
import com.hukwink.hukwink.message.MessageElement
import com.hukwink.hukwink.resource.ExternalResource

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
}