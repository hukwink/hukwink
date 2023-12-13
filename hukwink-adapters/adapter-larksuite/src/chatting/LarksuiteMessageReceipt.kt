package com.hukwink.hukwink.adapter.larksuite.chatting

import com.hukwink.hukwink.chatting.MessageReceipt

public class LarksuiteMessageReceipt(
    override val messageId: String,
    override val sentTime: Long,
) : MessageReceipt {
}