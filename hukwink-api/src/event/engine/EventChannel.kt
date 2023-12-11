package com.hukwink.hukwink.event.engine

import java.util.function.Predicate
import kotlin.coroutines.CoroutineContext

public interface EventChannel<T : Any> {
    public val engine: EventEngine

    public fun withContext(context: CoroutineContext): EventChannel<T>

    public fun filter(filter: Predicate<T>): EventChannel<T>

    public fun <SubT : T> filterIsInstance(type: Class<SubT>): EventChannel<SubT>


    public fun listen(
        priority: EventPriority = EventPriority.NORMAL,
        handler: suspend EventContext.(evt: T) -> Unit,
    ): EventHandler

    public suspend fun next(
        priority: EventPriority = EventPriority.H3,
        timeout: Long = 0L,
        intercept: Boolean = false,
        isInstance: Predicate<T>,
    ): T
}

public inline fun <reified SubT : Any> EventChannel<*>.filterIsInstance(): EventChannel<SubT> {
    @Suppress("UNCHECKED_CAST")
    return (this as EventChannel<Any>).filterIsInstance(SubT::class.java)
}
