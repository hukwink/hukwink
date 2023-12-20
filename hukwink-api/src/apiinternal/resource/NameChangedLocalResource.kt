package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.LocalResource

internal class NameChangedLocalResource(
    private val delegate: LocalResource,
    override val fileName: String,
) : LocalResource by delegate {

    override fun toAutoClosable(): LocalResource {
        return AutoClosableLocalResource.of(this)
    }

    override fun withLeakObserver(): LocalResource {
        return LeakSafeLocalResource.of(this)
    }

    override fun toString(): String {
        return "NameChanged<$fileName>[$delegate]"
    }
}
