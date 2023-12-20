package com.hukwink.hukwink.resource

import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.test.*

class TemporaryCacheStrategyTest {
    @field:TempDir
    lateinit var temporaryDirectory: Path

    @Test
    fun testBalanced() {
        val strategy = TemporaryCacheStrategy.BalancedBetweenMemoryAndLocal(
            memoryMax = 10,
            directory = temporaryDirectory
        )

        strategy.cache("", "Hello".byteInputStream()).use {
            assertIsNot<LocalResourceOrigin.FromFile>(it.origin)
        }


        val forigin: LocalResourceOrigin.FromFile
        strategy.cache("", "Hello World~".byteInputStream()).use { res ->
            assertIs<LocalResourceOrigin.FromFile>(res.origin)
            assertEquals("Hello World~", res.openStream().bufferedReader().use { it.readText() })
            forigin = res.origin as LocalResourceOrigin.FromFile
            println(forigin)
            println(res)

            assertEquals("", res.fileName)
        }
        assertFalse { forigin.path.exists() }
    }
}