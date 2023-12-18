package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.LocalResource
import com.hukwink.hukwink.util.systemProp
import java.lang.ref.Cleaner

internal object LocalResourceGlobalValues {
    val cleaner = Cleaner.create()


    val builtInResourceLeakObservable: Boolean by lazy { systemProp("hukwink.resource.builtin-leak-observable", true) }

    fun LocalResource.builtinLeakObservable(): LocalResource {
        if (builtInResourceLeakObservable) {
            return withLeakObserver()
        }
        return this
    }
}