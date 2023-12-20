package com.hukwink.hukwink.apiinternal.misc

import java.io.ByteArrayOutputStream

internal open class AccessibleByteArrayOutputStream : ByteArrayOutputStream {
    constructor() : super()
    constructor(size: Int) : super(size)

    var content by this::buf
    var len by this::count
}