package com.hukwink.hukwink.adapter.larksuite.proto

import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
internal class ProtoBotInfo(
    val activate_status: Int,
    val app_name: String,
    val avatar_url: String,
    val open_id: String,
) {
}