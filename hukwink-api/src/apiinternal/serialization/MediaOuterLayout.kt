package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Media
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object MediaOuterLayout :
    AbstractOuterLayoutSerializer<MediaOuterLayout.Delegate, Media>() {
    @Serializable
    @SerialName("media")
    class Delegate(
        @Serializable(ByteArrayHexStringSerializer::class)
        @SerialName("internal")
        override val buf: ByteArray,
        val fileName: String,
    ) : AbstractDelegate()

    override val serializer: KSerializer<Delegate>
        get() = Delegate.serializer()

    override fun newDelegate(value: ByteArray, message: Media): Delegate {
        return Delegate(value, message.fileName)
    }
}