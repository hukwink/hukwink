package com.hukwink.hukwink.apiinternal.eventengine

import com.hukwink.hukwink.event.engine.EventContext
import com.hukwink.hukwink.event.engine.EventHandler
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Predicate
import kotlin.coroutines.CoroutineContext

internal class HandlerImpl(
    private val action: suspend EventContext.(evt: Any) -> Unit,
    private val queue: ConcurrentLinkedQueue<HandlerImpl>,
    private val context: CoroutineContext,
    private val engineImpl: EventEngineImpl,
    private val filters: List<Predicate<Any>>,
) : EventHandler {
    override fun dispose() {
        queue.remove(this)
    }

    suspend fun process(context: EventContext, evt: Any) {
        try {
            for (f in filters) {
                if (!f.test(evt)) return
            }

            withContext(this.context) { action.invoke(context, evt) }
        } catch (e: Throwable) {
            val errorHandler = this.context[CoroutineExceptionHandler]
            if (errorHandler != null) {
                try {
                    errorHandler.handleException(this.context, e)
                    return
                } catch (errorInHandler: Throwable) {
                    engineImpl.logger.warn("Exception in error handler {}", errorHandler, errorInHandler)
                }
            }
            engineImpl.logger.warn("Event handler error", e)
        }
    }
}