package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.LocalResource

internal class AutoClosableLocalResource(
    delegate: LocalResource
) : LocalResource by delegate {
    override val isAutoClosable: Boolean get() = true

    companion object {
        fun of(resource: LocalResource): LocalResource {
            if (resource.isAutoClosable) return resource
            return AutoClosableLocalResource(resource)
        }
    }
}