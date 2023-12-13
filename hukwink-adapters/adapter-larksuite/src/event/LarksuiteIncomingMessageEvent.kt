package com.hukwink.hukwink.adapter.larksuite.event

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.chatting.Chatting
import com.hukwink.hukwink.contact.UserInfo
import com.hukwink.hukwink.event.events.IncomingMessageEvent
import com.hukwink.hukwink.message.MessageChain

public class LarksuiteIncomingMessageEvent(
    override val bot: Bot,
    override val messages: MessageChain,
    override val chatting: Chatting,
    override val sender: UserInfo,
) : IncomingMessageEvent {
    override fun toString(): String {
        return "LarksuiteIncomingMessageEvent($sender in $chatting, $messages)"
    }
}