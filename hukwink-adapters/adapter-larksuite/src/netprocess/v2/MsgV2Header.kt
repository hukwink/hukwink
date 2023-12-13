package com.hukwink.hukwink.adapter.larksuite.netprocess.v2

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public class MsgV2Header(
    @SerialName("event_id")
    public val eventId: String,

    @SerialName("event_type")
    public val eventType: String,

    @SerialName("create_time")
    public val createTime: String,


    @SerialName("token")
    public val token: String,


    @SerialName("app_id")
    public val appId: String,

    @SerialName("tenant_key")
    public val tenantKey: String,
) {
}