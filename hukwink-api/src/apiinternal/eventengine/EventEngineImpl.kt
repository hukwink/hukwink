package com.hukwink.hukwink.apiinternal.eventengine

import com.hukwink.hukwink.event.engine.EventChannel
import com.hukwink.hukwink.event.engine.EventContext
import com.hukwink.hukwink.event.engine.EventEngine
import com.hukwink.hukwink.event.engine.EventPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer

internal class EventEngineImpl(
    private val coroutineScope: CoroutineScope,
    internal val logger: Logger,
) : EventEngine {
    override val eventChannel: EventChannel<Any> = EventChannelImpl(this)

    private companion object {
        val eventPriorities = EventPriority.entries
        val eventPrioritiesNoMonitor = EventPriority.entries
            .filterNot { it == EventPriority.MONITOR }
            .toList()
    }


    internal val eventLines = EnumMap<EventPriority, ConcurrentLinkedQueue<HandlerImpl>>(
        EventPriority::class.java
    ).also { map ->
        eventPriorities.forEach { map[it] = ConcurrentLinkedQueue() }
    }

    override fun fire(evt: Any) {
        coroutineScope.launch { invoke0(evt) }
    }


    override suspend fun <T : Any> fireAndWait(evt: T): T {
        withContext(coroutineScope.coroutineContext) { invoke0(evt) }
        return evt
    }

    private suspend fun invoke0(evt: Any) {

        val context = object : EventContext {
            var intercepted = false

            override fun intercept() {
                intercepted = true
            }
        }

        try {

            eventPrioritiesNoMonitor.asSequence()
                .flatMap { eventLines[it]!!.asSequence() }
                .forEach { crtHandler ->
                    crtHandler.process(context, evt)

                    if (context.intercepted) {
                        return@invoke0
                    }
                }

            supervisorScope {
                eventLines[EventPriority.MONITOR]!!.forEach { crtHandler ->
                    launch { crtHandler.process(context, evt) }
                }
            }


        } catch (e: Throwable) {
            logger.error("Exception while broadcasting event {}", evt, e)
        }
    }

    override fun <T : Any> fireAndProcess(evt: T, callback: Consumer<T>?) {
        val job = coroutineScope.launch {
            invoke0(evt)
        }
        if (callback != null) {
            job.invokeOnCompletion { callback.accept(evt) }
        }
    }
}