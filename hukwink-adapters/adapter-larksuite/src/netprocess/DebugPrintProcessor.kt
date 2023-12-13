package com.hukwink.hukwink.adapter.larksuite.netprocess

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal class DebugPrintProcessor : EventProcessor() {
    private val json0 = Json { prettyPrint = true }
    override suspend fun process(evt: JsonObject, bot: LarksuiteBot) {
        bot.configuration.logger.info("Message in: {}", json0.encodeToString(JsonObject.serializer(), evt))
    }
}