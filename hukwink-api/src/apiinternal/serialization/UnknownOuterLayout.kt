package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Message
import com.hukwink.hukwink.util.ByteArrayHexStringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object UnknownOuterLayout :
    AbstractOuterLayoutSerializer<UnknownOuterLayout.Delegate, Message>() {
    @Serializable
    @SerialName("emotion")
    class Delegate(
        @Serializable(ByteArrayHexStringSerializer::class)
        @SerialName("internal")
        override val buf: ByteArray,
    ) : AbstractDelegate()

    override val serializer: KSerializer<Delegate>
        get() = Delegate.serializer()

    override fun newDelegate(value: ByteArray, message: Message): Delegate {
        return Delegate(value)
    }
}