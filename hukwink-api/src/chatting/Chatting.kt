package com.hukwink.hukwink.chatting

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.contact.ChatInfo
import com.hukwink.hukwink.message.Message

public interface Chatting {
    public val chatInfo: ChatInfo
    public val chatType: ChatType
    public val bot: Bot

    public suspend fun sendMessage(message: Message): MessageReceipt
}
