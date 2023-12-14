package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Message
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal interface MessageOuterDirectSerializer<T : Message> {
    fun deserializeDirectly(decoder: Decoder): T
    fun serializeDirectly(encoder: Encoder, value: T)
}
