package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageElement

internal class InternalPlainText(
    val content: String,
) : MessageElement {
    override fun contentToString(): String = content
    override fun toString(): String = content
}