package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageElement

public class LarksuiteSticker(
    public val fileKey: String,
    public val messageId: String,
) : MessageElement {
    override fun contentToString(): String {
        return "[sticker:$fileKey]"
    }

    override fun toString(): String {
        return "[sticker:$fileKey,$messageId]"
    }
}