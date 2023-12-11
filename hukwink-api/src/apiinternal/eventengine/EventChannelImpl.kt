package com.hukwink.hukwink.apiinternal.eventengine

import com.hukwink.hukwink.event.engine.EventChannel
import com.hukwink.hukwink.event.engine.EventContext
import com.hukwink.hukwink.event.engine.EventPriority
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeout
import java.util.function.Predicate
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal class EventChannelImpl<T : Any>(
    override val engine: EventEngineImpl,
    private val mergedCoroutineContext: CoroutineContext = EmptyCoroutineContext,
    private val viewedJobs: List<Job> = emptyList(),
    private val filters: List<Predicate<*>> = emptyList(),
) : EventChannel<T> {

    override fun withContext(context: CoroutineContext): EventChannel<T> {
        val theJob = context[Job]

        return if (theJob == null) {
            EventChannelImpl(
                engine = engine,
                mergedCoroutineContext = mergedCoroutineContext + context,
                viewedJobs = viewedJobs,
                filters = filters,
            )
        } else {
            EventChannelImpl(
                engine = engine,
                mergedCoroutineContext = mergedCoroutineContext + context,
                viewedJobs = viewedJobs + theJob,
                filters = filters,
            )
        }
    }

    override suspend fun next(priority: EventPriority, timeout: Long, intercept: Boolean, isInstance: Predicate<T>): T {
        val cp = CompletableDeferred<T>()

        val listener = listen(priority = priority) { evt ->
            if (isInstance.test(evt)) {
                if (cp.complete(evt)) {
                    if (intercept) {
                        intercept()
                    }
                }
            }
        }

        try {
            return withTimeout(timeout) { cp.await() }
        } catch (e: Throwable) {
            cp.completeExceptionally(e)

            throw e
        } finally {
            listener.dispose()
        }
    }

    override fun listen(priority: EventPriority, handler: suspend EventContext.(evt: T) -> Unit): HandlerImpl {
        val targetLine = engine.eventLines[priority]!!

        @Suppress("UNCHECKED_CAST")
        val handlerImpl = HandlerImpl(
            handler as (suspend EventContext.(Any) -> Unit),
            targetLine,
            mergedCoroutineContext,
            engine,
            filters as List<Predicate<Any>>
        )

        targetLine.add(handlerImpl)

        val jobCompletionHandler: CompletionHandler = { handlerImpl.dispose() }
        viewedJobs.forEach { job -> job.invokeOnCompletion(jobCompletionHandler) }

        return handlerImpl
    }

    override fun <SubT : T> filterIsInstance(type: Class<SubT>): EventChannel<SubT> {
        @Suppress("UNCHECKED_CAST")
        return filter(type::isInstance) as EventChannel<SubT>
    }

    override fun filter(filter: Predicate<T>): EventChannel<T> {
        return EventChannelImpl(
            engine = engine,
            mergedCoroutineContext = mergedCoroutineContext,
            viewedJobs = viewedJobs,
            filters = filters + filter,
        )
    }
}