package com.hukwink.hukwink

import com.hukwink.hukwink.chatting.ChatId
import com.hukwink.hukwink.chatting.Chatting
import com.hukwink.hukwink.config.BotConfiguration
import com.hukwink.hukwink.event.engine.EventEngine
import kotlinx.coroutines.CoroutineScope

public interface Bot {
    public val isActive: Boolean
    public val botName: String
    public val botId: String

    public val configuration: BotConfiguration
    public val coroutineScope: CoroutineScope
    public val eventEngine: EventEngine

    public suspend fun login()

    public fun close()
    public fun close(reason: Throwable)


    public fun openChat(chatId: ChatId): Chatting
}
