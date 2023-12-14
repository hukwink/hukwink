package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.MessageQuoteReply
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object QuoteReplyOuterLayout :
    AbstractOuterLayoutSerializer<QuoteReplyOuterLayout.Delegate, MessageQuoteReply>() {
    @Serializable
    @SerialName("reply")
    class Delegate(
        @Serializable(ByteArrayHexStringSerializer::class)
        @SerialName("internal")
        override val buf: ByteArray,
        val replyMessageId: String,
    ) : AbstractDelegate()

    override val serializer: KSerializer<Delegate>
        get() = Delegate.serializer()

    override fun newDelegate(value: ByteArray, message: MessageQuoteReply): Delegate {
        return Delegate(value, message.replyMessageId)
    }
}