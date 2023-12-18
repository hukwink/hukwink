package com.hukwink.hukwink.apiinternal.resource

import java.lang.ref.Cleaner

internal object GlobalCleaner {
    val cleaner = Cleaner.create()
}