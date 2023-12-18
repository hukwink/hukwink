package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.LocalResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.ref.Cleaner
import java.util.concurrent.atomic.AtomicBoolean

internal class LeakSafeLocalResource(
    private val delegate: InnerActionWrap
) : LocalResource by delegate {
    override val isLeakObservable: Boolean get() = true
    lateinit var cleanerHandle: Cleaner.Cleanable

    override fun close() {
        delegate.close()
        cleanerHandle.clean() // dispose cleaner handle
    }

    companion object {
        val logger: Logger by lazy { LoggerFactory.getLogger(LeakSafeLocalResource::class.java) }
        val tracing: Boolean by lazy { System.getProperty("hukwink.resource.leak-create-point").toBoolean() }

        fun of(resource: LocalResource): LocalResource {
            if (resource.isLeakObservable) return resource

            val innerAction = InnerActionWrap(resource)
            val outerResource = LeakSafeLocalResource(innerAction)

            outerResource.cleanerHandle = GlobalCleaner.cleaner.register(
                outerResource, innerAction
            )
            if (tracing) {
                innerAction.trace = Throwable("Create point trace")
            }

            return outerResource
        }
    }
}

internal class InnerActionWrap(
    private val delegate: LocalResource
) : LocalResource by delegate, Runnable {
    val closed = AtomicBoolean(false)
    var trace: Throwable? = null

    override val isLeakObservable: Boolean get() = true

    override fun close() {
        if (closed.compareAndSet(false, true)) {
            delegate.close()
        }
    }

    override fun run() { // cleaner action
        if (!closed.get()) {
            if (trace == null) {
                LeakSafeLocalResource.logger.warn(
                    "Leaked resource {} detected. Add -Dhukwink.resource.leak-create-point=true to enable leak tracing",
                    this
                )
            } else {
                LeakSafeLocalResource.logger.warn("Leaked resource {} detected.", this, trace)
            }

            kotlin.runCatching {
                close()
            }.onSuccess {
                LeakSafeLocalResource.logger.warn("Leaked resource {} closed.", delegate)
            }.onFailure { err ->
                LeakSafeLocalResource.logger.warn("Leaked resource {} closed with exception: ", delegate, err)
            }
        }
    }
}
