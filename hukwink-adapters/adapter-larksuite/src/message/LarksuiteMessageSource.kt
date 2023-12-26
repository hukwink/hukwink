package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageMetadataKey
import com.hukwink.hukwink.message.MessageQuoteReply
import com.hukwink.hukwink.message.MessageSource
import com.hukwink.hukwink.message.serialization.MessageInternalDataSerializer
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.util.decodeCharSequence
import com.hukwink.hukwink.util.encodeCharSequence
import io.netty.buffer.ByteBuf

public class LarksuiteMessageSource(
    override val messageId: String,
    public val parentId: String = "",
    public val rootId: String = "",
) : MessageSource {
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

    public companion object Key : MessageMetadataKey<LarksuiteMessageSource>(MessageSource)

    internal object Serializer : MessageInternalDataSerializer<LarksuiteMessageSource> {
        override val serialName: String get() = "larksuite.MessageSource"
        override val outerLayout: MessageOuterLayoutSerializer<LarksuiteMessageSource>
            get() = MessageSource.outerLayout()

        override fun decode(buffer: ByteBuf): LarksuiteMessageSource {
            val messageId = buffer.decodeCharSequence().toString()
            val rootId = buffer.decodeCharSequence().toString()
            val parentId = buffer.decodeCharSequence().toString()

            return LarksuiteMessageSource(messageId, parentId, rootId)
        }

        override fun encode(message: LarksuiteMessageSource, buffer: ByteBuf) {
            buffer.encodeCharSequence(message.messageId)
            buffer.encodeCharSequence(message.rootId)
            buffer.encodeCharSequence(message.parentId)
        }

    }
}