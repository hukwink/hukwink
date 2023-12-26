@file:JvmMultifileClass
@file:JvmName("HukwinkUtilKt")

package com.hukwink.hukwink.util

import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest

public fun InputStream.messageDigest(type: String): ByteArray {
    val md = MessageDigest.getInstance(type)
    transferTo(object : OutputStream() {
        override fun write(b: Int) {
            md.update(b.toByte())
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            md.update(b, off, len)
        }
    })
    return md.digest()
}

public fun InputStream.sha1(): ByteArray = messageDigest("SHA-1")
public fun InputStream.sha256(): ByteArray = messageDigest("SHA-256")
public fun InputStream.md5(): ByteArray = messageDigest("MD5")
