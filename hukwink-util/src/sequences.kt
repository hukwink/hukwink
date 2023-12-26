@file:JvmMultifileClass
@file:JvmName("HukwinkUtilKt")

package com.hukwink.hukwink.util

public fun <K, V, V2> Sequence<Pair<K, V>>.mapValue(
    mapper: (Pair<K, V>) -> V2,
): Sequence<Pair<K, V2>> = map { pair -> pair.first to mapper(pair) }


public fun Sequence<*>.justProcess() {
    @Suppress("ControlFlowWithEmptyBody")
    for (unused in this) {
    }
}

