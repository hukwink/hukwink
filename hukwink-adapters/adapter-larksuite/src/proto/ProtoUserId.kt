package com.hukwink.hukwink.adapter.larksuite.proto

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.adapter.larksuite.LarksuiteConfiguration
import kotlinx.serialization.Serializable

@Suppress("PropertyName")
@Serializable
internal class ProtoUserId(
    val open_id: String,
    val union_id: String,
    val user_id: String = "",
) {
    fun resolveId(bot: LarksuiteBot): String {
        return when (bot.configuration.preferUserId) {
            LarksuiteConfiguration.PreferUserId.OPEN_ID -> open_id
            LarksuiteConfiguration.PreferUserId.UNION_ID -> union_id
            LarksuiteConfiguration.PreferUserId.USER_ID -> user_id
        }
    }
}