package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.adapter.larksuite.resource.FromMessageResource
import com.hukwink.hukwink.message.Image
import com.hukwink.hukwink.message.MessageElement

internal class LarksuiteImage(
    public val imageId: String,
    public override val messageId: String,
) : FromMessageResource(), MessageElement, Image {
    override val resourceType: String
        get() = "image"

    override val fileKey: String
        get() = imageId

    override fun contentToString(): String {
        return "[image:$imageId]"
    }

    override fun toString(): String {
        return "[image:$imageId,$messageId]"
    }
}