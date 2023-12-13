package com.hukwink.hukwink.adapter.larksuite

import com.hukwink.hukwink.config.BotConfiguration
import com.hukwink.hukwink.util.lazyMutable
import kotlinx.serialization.json.Json

public class LarksuiteConfiguration : BotConfiguration() {
    public enum class PreferUserId {
        OPEN_ID, UNION_ID, USER_ID;
    }

    public var appId: String by lazyMutable { error("Appid not setup") }
    public var appSecret: String by lazyMutable { error("Application secret not setup") }
    public var encryptKey: String = ""
    public var verificationToken: String = ""
    public var preferUserId: PreferUserId = PreferUserId.UNION_ID

    public var webhookPath: String = "/adapter-webhook-larksuite"
    public var webhookDebugPrint: Boolean = false

    public var sharableJson: Json by lazyMutable {
        Json {
            this.ignoreUnknownKeys = true
        }
    }
}