package com.hukwink.hukwink

import com.hukwink.hukwink.chatting.ChatType
import com.hukwink.hukwink.chatting.Chatting
import com.hukwink.hukwink.config.BotConfiguration

public interface Bot {
    public val isActive: Boolean
    public val botName: String
    public val botId: String

    public val configuration: BotConfiguration

    public fun close()
    public fun close(reason: Throwable)


    public fun openChat(type: ChatType, chatId: String): Chatting
}
