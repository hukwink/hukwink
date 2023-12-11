package com.hukwink.hukwink.util

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