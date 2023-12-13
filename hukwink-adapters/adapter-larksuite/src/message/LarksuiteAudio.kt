package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.adapter.larksuite.resource.FromMessageResource
import com.hukwink.hukwink.message.MessageElement

internal class LarksuiteAudio(
    override val fileKey: String,
    public val duration: Long,
    override val messageId: String,
) : FromMessageResource(), MessageElement {
}