package com.hukwink.hukwink.resource

import com.hukwink.hukwink.apiinternal.resource.AutoClosableLocalResource
import com.hukwink.hukwink.apiinternal.resource.LeakSafeLocalResource
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface LocalResource : Closeable {
    public val fileName: String
    public val sha1: ByteArray
    public val md5: ByteArray

    public val size: Long
    public val isAutoClosable: Boolean get() = false
    public val isLeakObservable: Boolean get() = false


    @Throws(IOException::class)
    public fun openStream(): InputStream

    public fun toAutoClosable(): LocalResource {
        return AutoClosableLocalResource.of(this)
    }

    public fun withLeakObserver(): LocalResource {
        return LeakSafeLocalResource.of(this)
    }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> LocalResource.withAutoUse(block: (LocalResource) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.autoCloseFinally(exception)
    }
}

@SinceKotlin("1.2")
@PublishedApi
internal fun LocalResource?.autoCloseFinally(cause: Throwable?) {
    if (this == null) return
    if (!isAutoClosable) return

    when {
        cause == null -> close()
        else ->
            try {
                close()
            } catch (closeException: Throwable) {
                cause.addSuppressed(closeException)
            }
    }
}
