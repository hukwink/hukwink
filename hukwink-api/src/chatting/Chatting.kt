package com.hukwink.hukwink.chatting

import com.hukwink.hukwink.contact.ChatInfo
import com.hukwink.hukwink.message.Message

public interface Chatting {
    public val chatInfo: ChatInfo
    public val chatType: ChatType

    public suspend fun sendMessage(message: Message): MessageReceipt
}
