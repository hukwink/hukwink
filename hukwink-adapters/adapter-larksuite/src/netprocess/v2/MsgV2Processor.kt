package com.hukwink.hukwink.adapter.larksuite.netprocess.v2

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.adapter.larksuite.netprocess.EventProcessor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonObject

public abstract class MsgV2Processor<T>() : EventProcessor() {
    protected abstract val eventSerializer: KSerializer<T>

    override suspend fun process(evt: JsonObject, bot: LarksuiteBot) {
        val json = bot.configuration.sharableJson
        val header = json.decodeFromJsonElement(MsgV2Header.serializer(), evt["header"] ?: error("Missing header"))
        val event = json.decodeFromJsonElement(eventSerializer, evt["event"] ?: error("No event payload found"))

        process0(evt, bot, header, event)
    }

    protected abstract fun process0(evt: JsonObject, bot: LarksuiteBot, header: MsgV2Header, event: T)
}
