package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.adapter.larksuite.resource.FromMessageResource
import com.hukwink.hukwink.message.Audio
import com.hukwink.hukwink.message.MessageElement

internal class LarksuiteAudio(
    override val fileKey: String,
    public val duration: Long,
    override val messageId: String,
) : FromMessageResource(), MessageElement, Audio {
    override fun contentToString(): String = "[audio:$fileKey]"
    override fun toString(): String = "[audio:$fileKey, $messageId]"
}