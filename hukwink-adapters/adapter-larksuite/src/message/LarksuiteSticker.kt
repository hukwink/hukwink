package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageElement

public class LarksuiteSticker(
    public val fileKey: String,
    public val messageId: String,
) : MessageElement {
}