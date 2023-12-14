package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.MessageSource
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object MessageSourceOuterLayout :
    AbstractOuterLayoutSerializer<MessageSourceOuterLayout.Delegate, MessageSource>() {
    @Serializable
    @SerialName("reply")
    class Delegate(
        @Serializable(ByteArrayHexStringSerializer::class)
        @SerialName("internal")
        override val buf: ByteArray,
        val messageId: String,
    ) : AbstractDelegate()

    override val serializer: KSerializer<Delegate>
        get() = Delegate.serializer()

    override fun newDelegate(value: ByteArray, message: MessageSource): Delegate {
        return Delegate(value, message.messageId)
    }
}