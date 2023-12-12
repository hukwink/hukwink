package com.hukwink.hukwink.util

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public fun CoroutineContext.childScope(
    name: String,
    context: CoroutineContext = EmptyCoroutineContext,
): CoroutineScope {
    val newJob = Job(parent = this[Job])
    return CoroutineScope(this + context + CoroutineName(name) + newJob)
}

public fun CoroutineContext.childSupervisorScope(
    name: String,
    context: CoroutineContext = EmptyCoroutineContext,
): CoroutineScope {
    val newJob = SupervisorJob(parent = this[Job])
    return CoroutineScope(this + context + CoroutineName(name) + newJob)
}

