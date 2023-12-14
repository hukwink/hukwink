package com.hukwink.hukwink.contact

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.chatting.ChatId
import com.hukwink.hukwink.chatting.Chatting

public interface UserInfo {
    public val userId: String
    public val username: String

    /**
     * 获取开启 [Chatting] 的 ChatId.
     *
     * 此 id 仅能用于开启 [Chatting], 不能用于确认俩个 [Chatting] 是否是同一个 [Chatting]
     *
     * @see Bot.openChat
     */
    public val chatId: ChatId
}