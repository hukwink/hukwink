package com.hukwink.hukwink.adapter.larksuite.message.file

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.message.File
import com.hukwink.hukwink.resource.ExternalResource

internal class LarksuiteFileUploaded(
    val fileId: String,
    override val fileName: String,
) : File {
    override fun contentToString(): String = "[file:$fileName]"

    override fun toString(): String = contentToString()

    override fun openResource(bot: Bot): ExternalResource.ResourceHandle {
        TODO("Not yet implemented")
    }
}