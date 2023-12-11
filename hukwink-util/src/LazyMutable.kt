package com.hukwink.hukwink.util

import kotlin.concurrent.Volatile


public class LazyMutable<T>(
    @field:Volatile
    private var initializer: (() -> T)?
) {
    private var value: T? = null

    public fun getValue(): T {
        if (initializer != null) {
            synchronized(this) {
                val initializer0 = initializer
                if (initializer0 != null) {
                    value = initializer0()
                    initializer = null
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    public fun setValue(value: T) {
        if (initializer != null) {
            synchronized(this) {
                initializer = null
                this.value = value
            }
        } else {
            this.value = value
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun getValue(a: Any?, b: Any?): T {
        return getValue()
    }

    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun setValue(a: Any?, b: Any?, value: T) {
        setValue(value)
    }
}

public fun <T> lazyMutable(initializer: () -> T): LazyMutable<T> = LazyMutable(initializer)

