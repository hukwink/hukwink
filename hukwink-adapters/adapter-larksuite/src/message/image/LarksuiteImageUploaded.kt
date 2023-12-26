package com.hukwink.hukwink.adapter.larksuite.message.image

import com.hukwink.hukwink.adapter.larksuite.resource.UploadedResource
import com.hukwink.hukwink.message.Image
import com.hukwink.hukwink.message.MessageElement

internal class LarksuiteImageUploaded(
    val imageId: String,
) : MessageElement, Image, UploadedResource() {
    override fun contentToString(): String {
        return "[image:$imageId]"
    }

    override val fileKey: String get() = imageId
    override val resourceType: String get() = "uploadedImage"

    override fun toString(): String {
        return "[image:$imageId]"
    }
}