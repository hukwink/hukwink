package com.hukwink.hukwink.resource

import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertTrue

class AbstractLocalResourceTest {

    @Test
    fun testClose() { // test VarHandle in kotlin work correctly
        val res = object : AbstractLocalResource() {
            var closed = false
            override fun close0() {
                closed = true
            }

            override val fileName: String get() = ""
            override val size: Long get() = 0

            override fun openStream(): InputStream = InputStream.nullInputStream()
        }
        res.close()
        assertTrue(res.closed)
    }
}