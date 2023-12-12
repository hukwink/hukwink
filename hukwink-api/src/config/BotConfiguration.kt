package com.hukwink.hukwink.config

import com.hukwink.hukwink.event.engine.EventEngine
import kotlin.coroutines.CoroutineContext

public interface BotConfiguration {
    public val eventEngine: EventEngine
    public val parentContext: CoroutineContext
}
