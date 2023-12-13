package com.hukwink.hukwink.adapter.larksuite.netprocess

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import kotlinx.serialization.json.JsonObject

public abstract class EventProcessor {
    public abstract suspend fun process(evt: JsonObject, bot: LarksuiteBot)

}