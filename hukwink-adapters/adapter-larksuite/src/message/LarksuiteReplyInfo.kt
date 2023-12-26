package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageMetadataKey
import com.hukwink.hukwink.message.MessageQuoteReply
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.util.decodeCharSequence
import com.hukwink.hukwink.util.encodeCharSequence
import io.netty.buffer.ByteBuf

public class LarksuiteReplyInfo(
    public val parentId: String,
    public val rootId: String,
) : MessageQuoteReply {

    override val key: MessageMetadataKey<LarksuiteReplyInfo> get() = Key


    override fun toString(): String {
        return "[reply:$parentId, root=$rootId]"
    }

    override fun contentToString(): String = ""

    override val replyMessageId: String
        get() = parentId

    public companion object Key : MessageMetadataKey<LarksuiteReplyInfo>(MessageQuoteReply)

    internal object Serializer : MessageInternalDataSerializer<LarksuiteReplyInfo> {
        override val serialName: String get() = "larksuite.Reply"
        override val outerLayout: MessageOuterLayoutSerializer<LarksuiteReplyInfo>
            get() = MessageQuoteReply.outerLayout()

        override fun decode(buffer: ByteBuf): LarksuiteReplyInfo {
            val parentId = buffer.decodeCharSequence().toString()
            val rootId = buffer.decodeCharSequence().toString()

            return LarksuiteReplyInfo(parentId, rootId)
        }

        override fun encode(message: LarksuiteReplyInfo, buffer: ByteBuf) {
            buffer.encodeCharSequence(message.parentId)
            buffer.encodeCharSequence(message.rootId)
        }

    }
}