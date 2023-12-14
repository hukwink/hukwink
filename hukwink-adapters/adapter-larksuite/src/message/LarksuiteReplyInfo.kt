package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageMetadataKey
import com.hukwink.hukwink.message.MessageQuoteReply

public class LarksuiteReplyInfo(
    public val parentId: String,
    public val rootId: String,
) : MessageQuoteReply {

    override val key: MessageMetadataKey<LarksuiteReplyInfo> get() = Key

    public companion object Key : MessageMetadataKey<LarksuiteReplyInfo>(MessageQuoteReply)


    override fun toString(): String {
        return "[reply:$parentId, root=$rootId]"
    }

    override fun contentToString(): String = ""

    override val replyMessageId: String
        get() = parentId
}