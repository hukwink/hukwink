package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Emotion
import com.hukwink.hukwink.util.ByteArrayHexStringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object EmotionOuterLayout :
    AbstractOuterLayoutSerializer<EmotionOuterLayout.Delegate, Emotion>() {
    @Serializable
    @SerialName("emotion")
    class Delegate(
        @Serializable(ByteArrayHexStringSerializer::class)
        @SerialName("internal")
        override val buf: ByteArray,
        val emotionType: String,
    ) : AbstractDelegate()

    override val serializer: KSerializer<Delegate>
        get() = Delegate.serializer()

    override fun newDelegate(value: ByteArray, message: Emotion): Delegate {
        return Delegate(value, message.emotionType)
    }
}