package com.hukwink.hukwink.event.engine

import java.util.function.Consumer

public interface EventEngine {
    public fun fire(evt: Any)
    public suspend fun <T : Any> fireAndWait(evt: T): T

    public fun <T : Any> fireAndProcess(evt: T, callback: Consumer<T>?)


    public val eventChannel: EventChannel<Any>
}
