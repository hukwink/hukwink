package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageElement

internal class LarksuiteFile(
    public val fileKey: String,
    public val fileName: String,
    public val isFolder: Boolean,
    public val messageId: String,
) : MessageElement {
    override fun contentToString(): String = "[file:$fileName]"
    override fun toString(): String {
        return contentToString()
    }
}