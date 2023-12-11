package com.hukwink.hukwink.event.engine

import com.hukwink.hukwink.apiinternal.eventengine.EventEngineImpl
import com.hukwink.hukwink.util.lazyMutable
import kotlinx.coroutines.CoroutineScope
import org.slf4j.Logger
import kotlin.coroutines.EmptyCoroutineContext

public class EventEngineBuilder {
    public companion object {
        @JvmStatic
        public fun newBuilder(): EventEngineBuilder = EventEngineBuilder()
    }

    private var coroutineScope: CoroutineScope by lazyMutable { CoroutineScope(EmptyCoroutineContext) }
    private var logger: Logger by lazyMutable { error("Logger not setup") }

    public fun withScope(scope: CoroutineScope): EventEngineBuilder = apply {
        coroutineScope = scope
    }

    public fun withLogger(logger: Logger): EventEngineBuilder = apply {
        this.logger = logger
    }


    public fun build(): EventEngine {
        return EventEngineImpl(
            coroutineScope = coroutineScope,
            logger = logger,
        )
    }

}