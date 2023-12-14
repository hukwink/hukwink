package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Image
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object ImageOuterLayout :
    AbstractOuterLayoutSerializer<ImageOuterLayout.Delegate, Image>() {
    @Serializable
    @SerialName("image")
    class Delegate(
        @Serializable(ByteArrayHexStringSerializer::class)
        @SerialName("internal")
        override val buf: ByteArray,
    ) : AbstractDelegate()

    override val serializer: KSerializer<Delegate>
        get() = Delegate.serializer()

    override fun newDelegate(value: ByteArray, message: Image): Delegate {
        return Delegate(value)
    }
}