package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageElement

public class LarksuiteEmotion(
    public val emojiType: String,
) : MessageElement {
    override fun toString(): String {
        return "[emotion:$emojiType]"
    }

    override fun contentToString(): String {
        return toString()
    }
}