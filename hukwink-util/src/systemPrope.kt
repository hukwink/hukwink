@file:JvmMultifileClass
@file:JvmName("HukwinkUtilKt")

package com.hukwink.hukwink.util

public fun systemProp(prop: String, def: Boolean): Boolean = when (val value = System.getProperty(prop)) {
    null -> def
    "" -> true
    else -> value.toBooleanStrictOrNull() ?: error("Bad property $prop boolean value: $value")
}

public fun systemProp(prop: String, def: Int): Int = when (val value = System.getProperty(prop)) {
    null -> def
    "" -> error("Bad property $prop int value: <empty>")
    else -> value.toIntOrNull() ?: error("Bad property $prop int value: $value")
}

public fun systemProp(prop: String, def: Long): Long = when (val value = System.getProperty(prop)) {
    null -> def
    "" -> error("Bad property $prop long value: <empty>")
    else -> value.toLongOrNull() ?: error("Bad property $prop long value: $value")
}
