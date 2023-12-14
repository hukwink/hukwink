package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageMetadataKey
import com.hukwink.hukwink.message.MessageQuoteReply
import com.hukwink.hukwink.message.MessageSource

public class LarksuiteMessageSource(
    override val messageId: String,
    public val parentId: String = "",
    public val rootId: String = "",
) : MessageSource {
    public companion object Key : MessageMetadataKey<LarksuiteMessageSource>(MessageSource)

    override val key: MessageMetadataKey<*> get() = Key

    override fun reply(): MessageQuoteReply {
        return LarksuiteReplyInfo(
            parentId = messageId,
            rootId = rootId.ifEmpty { messageId }
        )
    }

    override fun toString(): String {
        return "[source:$messageId, parent=$parentId, root=$rootId]"
    }

    override fun contentToString(): String = ""
}