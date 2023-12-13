package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.message.MessageMetadata
import com.hukwink.hukwink.message.MessageMetadataKey

public class LarksuiteMessageTitle(
    public val title: String,
) : MessageMetadata {

    override val key: MessageMetadataKey<LarksuiteMessageTitle> get() = Key

    public companion object Key : MessageMetadataKey<LarksuiteMessageTitle>()
}