package com.hukwink.hukwink.resource

import com.hukwink.hukwink.resource.LocalResource.Companion.withFileName
import org.junit.jupiter.api.io.TempDir
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AbstractLocalResourceTest {
    @field:TempDir
    lateinit var temporaryDirectory: Path

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


    @Test
    fun testFileOpenNotDeleted() {
        val randomFile = Files.createTempFile(temporaryDirectory, "tmptest", ".txt")
        randomFile.writeText("Hello World")

        LocalResource.open(randomFile).use { res ->
            assertEquals("Hello World", res.openStream().reader().use { it.readText() })
            assertEquals(randomFile.fileName.toString(), res.fileName)
            assertEquals(LocalResourceOrigin.FromFile(randomFile), res.origin)
        }
        assertTrue(randomFile.exists())
    }

    @Test
    fun testFileNameOverride() {
        val res = LocalResource.wrap("test.txt", ByteArray(0)).withFileName("omg.txt")
        assertEquals("omg.txt", res.fileName)
    }
}