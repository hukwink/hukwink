package com.hukwink.hukwink.adapter.larksuite.message

import com.hukwink.hukwink.adapter.larksuite.resource.FromMessageResource
import com.hukwink.hukwink.message.MessageElement

internal class LarksuiteMedia(
    override val fileKey: String,
    val imageKey: String,
    val fileName: String,
    val duration: Long,
    override val messageId: String,
) : FromMessageResource(), MessageElement {
}