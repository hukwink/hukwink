package com.hukwink.hukwink.event.events

import com.hukwink.hukwink.chatting.Chatting
import com.hukwink.hukwink.contact.UserInfo
import com.hukwink.hukwink.message.MessageChain

public interface MessageEvent : BotRelatedEvent {
    public val messages: MessageChain

    public val chatting: Chatting
    public val sender: UserInfo
}

public interface IncomingMessageEvent : MessageEvent

