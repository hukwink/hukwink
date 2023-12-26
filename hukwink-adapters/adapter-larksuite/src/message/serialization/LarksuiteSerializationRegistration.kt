package com.hukwink.hukwink.adapter.larksuite.message.serialization

import com.hukwink.hukwink.adapter.larksuite.message.*
import com.hukwink.hukwink.message.serialization.HukwinkMessageSerialization

internal object LarksuiteSerializationRegistration {
    internal fun register(serialization: HukwinkMessageSerialization) {
        serialization.register(LarksuiteAudio::class.java, LarksuiteAudio.Serializer)
        serialization.register(LarksuiteEmotion::class.java, LarksuiteEmotion.Serializer)
        serialization.register(LarksuiteMedia::class.java, LarksuiteMedia.Serializer)
        serialization.register(LarksuiteMessageSource::class.java, LarksuiteMessageSource.Serializer)
        serialization.register(LarksuiteMessageTitle::class.java, LarksuiteMessageTitleSerializer)
        serialization.register(LarksuiteReplyInfo::class.java, LarksuiteReplyInfo.Serializer)
        serialization.register(LarksuiteSticker::class.java, LarksuiteSticker.Serializer)
    }
}