package com.hukwink.hukwink.resource

import java.io.Closeable
import java.io.IOException
import java.io.InputStream

public interface LocalResource : Closeable {
    public val fileName: String
    public val sha1: ByteArray
    public val md5: ByteArray

    public val size: Long


    @Throws(IOException::class)
    public fun openStream(): InputStream

    public fun toAutoClosable(): LocalResource
}
