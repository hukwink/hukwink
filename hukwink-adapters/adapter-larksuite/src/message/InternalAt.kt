package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageElement

internal class InternalAt(
    val user_id: String,
) : MessageElement {
    override fun contentToString(): String = ""

    override fun toString(): String = "[@$user_id]"
}