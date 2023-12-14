package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Audio
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object AudioOuterLayout :
    AbstractOuterLayoutSerializer<AudioOuterLayout.Delegate, Audio>() {
    @Serializable
    @SerialName("audio")
    class Delegate(
        @Serializable(ByteArrayHexStringSerializer::class)
        @SerialName("internal")
        override val buf: ByteArray,
    ) : AbstractDelegate()

    override val serializer: KSerializer<Delegate>
        get() = Delegate.serializer()

    override fun newDelegate(value: ByteArray, message: Audio): Delegate {
        return Delegate(value)
    }
}