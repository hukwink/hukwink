package com.hukwink.hukwink.adapter.larksuite.proto

import kotlinx.serialization.Serializable

@Suppress("RemoveRedundantQualifierName", "PropertyName")
@Serializable
internal class ProtoSendMessageReply(
    val data: ProtoSendMessageReply.RepliedMessage,
) {
    @Serializable
    internal class RepliedMessage(
        val message_id: String,
        val create_time: String,
    )
}