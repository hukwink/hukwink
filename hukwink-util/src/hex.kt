@file:JvmMultifileClass
@file:JvmName("HukwinkUtilKt")

package com.hukwink.hukwink.util

import java.util.*

public fun ByteArray.encodeHex(format: HexFormat = HexFormat.of()): String {
    return format.formatHex(this)
}

public fun CharSequence.decodeHex(format: HexFormat = HexFormat.of()): ByteArray {
    return format.parseHex(this)
}
