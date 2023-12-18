package com.hukwink.hukwink.resource

import com.hukwink.hukwink.util.md5
import com.hukwink.hukwink.util.sha1
import java.lang.invoke.MethodHandles

public abstract class AbstractLocalResource : LocalResource {
    @field:Volatile
    private var closed: Boolean = false

    override val sha1: ByteArray by lazy {
        openStream().use { it.sha1() }
    }

    override val md5: ByteArray by lazy {
        openStream().use { it.md5() }
    }


    public companion object {
        private var vh_closed = MethodHandles.lookup().findVarHandle(
            AbstractLocalResource::class.java, "closed", java.lang.Boolean.TYPE
        )
    }

    protected abstract fun close0()

    final override fun close() {
        if (!vh_closed.compareAndSet(this, false, true)) return
        close0()
    }
}