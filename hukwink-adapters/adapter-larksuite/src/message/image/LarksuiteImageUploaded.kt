package com.hukwink.hukwink.adapter.larksuite.message.image

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.message.Image
import com.hukwink.hukwink.message.MessageElement
import com.hukwink.hukwink.resource.ExternalResource

internal class LarksuiteImageUploaded(
    val imageId: String,
) : MessageElement, Image {
    override fun contentToString(): String {
        return "[image:$imageId]"
    }

    override fun openResource(bot: Bot): ExternalResource.ResourceHandle {
        TODO()
    }

    override fun toString(): String {
        return "[image:$imageId]"
    }
}