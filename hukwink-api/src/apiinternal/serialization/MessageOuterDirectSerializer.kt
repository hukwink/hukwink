package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Message
import com.hukwink.hukwink.util.HukwinkInternalApi
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@HukwinkInternalApi
public interface MessageOuterDirectSerializer<T : Message> {
    public fun deserializeDirectly(decoder: Decoder): T
    public fun serializeDirectly(encoder: Encoder, value: T)
}
