package com.hukwink.hukwink.adapter.larksuite.message.file

import com.hukwink.hukwink.adapter.larksuite.resource.FromMessageResource
import com.hukwink.hukwink.message.File
import com.hukwink.hukwink.message.MessageElement

internal class LarksuiteFileFromChat(
    override val fileKey: String,
    override val fileName: String,
    public val isFolder: Boolean,
    override val messageId: String,
) : FromMessageResource(), MessageElement, File {
    override fun contentToString(): String = "[file:$fileName]"
    override fun toString(): String {
        return contentToString()
    }
}