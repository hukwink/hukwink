package com.hukwink.hukwink.adapter.larksuite.proto

import kotlinx.serialization.Serializable

@Suppress("RemoveRedundantQualifierName", "unused", "PropertyName")
@Serializable
internal class ProtoEventReceiveMessage(
    val message: ProtoEventReceiveMessage.Message,
    val sender: ProtoEventReceiveMessage.Sender,
) {
    @Serializable
    internal class Message(
        val chat_id: String,
        val chat_type: String,
        val message_id: String,
        val message_type: String,
        val content: String = "",
        val mentions: List<ProtoEventReceiveMessage.Mention> = emptyList(),
        val parent_id: String = "",
        val root_id: String = "",
        val create_time: String = "",
    )

    @Serializable
    internal class Mention(
        val id: ProtoUserId,
        val key: String,
        val name: String,
        val tenant_key: String,
    )

    @Serializable
    internal class Sender(
        val sender_type: String,
        val sender_id: ProtoUserId,
    )
}