package com.hukwink.hukwink.config

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.event.engine.EventEngine
import com.hukwink.hukwink.event.engine.EventEngineBuilder
import com.hukwink.hukwink.httpd.HttpServerDaemon
import com.hukwink.hukwink.util.lazyMutable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("MemberVisibilityCanBePrivate")
public open class BotConfiguration {
    public var logger: Logger by lazyMutable { LoggerFactory.getLogger(Bot::class.java) }

    public var eventEngine: EventEngine by lazyMutable {
        EventEngineBuilder.newBuilder().withLogger(logger).build()
    }

    public var parentContext: CoroutineContext = EmptyCoroutineContext

    public var httpServerDaemon: HttpServerDaemon by lazyMutable { error("Http Server Daemon not setup") }
}
