@file:JvmMultifileClass
@file:JvmName("HukwinkUtilKt")

package com.hukwink.hukwink.util

import java.util.*

public inline fun <T> MutableList<T>.removeFirst(filter: (T) -> Boolean): T? {
    val iter = iterator()
    for (item in iter) {
        if (filter(item)) {
            iter.remove()
            return item
        }
    }
    return null
}

public inline fun <T> MutableList<T>.removeAllAndPick(crossinline filter: (T) -> Boolean): MutableList<T> {
    val resp = mutableListOf<T>()
    removeIf { elm ->
        if (filter(elm)) {
            resp.add(elm)
            return@removeIf true
        }
        return@removeIf false
    }
    return resp
}

@Suppress("NOTHING_TO_INLINE")
public inline fun <T> List<T>.immutable(): List<T> {
    return Collections.unmodifiableList(this)
}
