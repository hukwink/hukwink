package com.hukwink.hukwink.message

import com.hukwink.hukwink.message.MessageUtil.messageChainOf
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MessageChainTest {
    @Test
    fun assertConflictMessageMetadataNotExists() {
        val messageChain = messageChainOf(TestMetadata("A"), TestMetadata("B"), TestMetadata("C"))
        assertEquals(1, messageChain.metadata.size)
        assertEquals(TestMetadata("C"), messageChain[TestMetadata])
    }

    @Test
    fun assertNotSameKeyMetadataCanExists() {
        val newKey = MessageMetadataKey<TestMetadata>()
        val messageChain = messageChainOf(TestMetadata("A", newKey), TestMetadata("B", newKey), TestMetadata("C"))
        assertEquals(2, messageChain.metadata.size)
        assertEquals(TestMetadata("C"), messageChain[TestMetadata])
        assertEquals(TestMetadata("B", newKey), messageChain[newKey])
    }

    @Test
    fun assertMetadataOrderNotChanged() {
        val k1 = MessageMetadataKey<TestMetadata>()
        val k2 = MessageMetadataKey<TestMetadata>()
        val k3 = MessageMetadataKey<TestMetadata>()

        assertEquals(
            listOf(
                TestMetadata("A", k1),
                TestMetadata("B", k2),
                TestMetadata("C", k3),
            ), messageChainOf(
                TestMetadata("A", k1),
                TestMetadata("B", k2),
                TestMetadata("C", k3),
            ).metadata
        )


        assertEquals(
            listOf(
                TestMetadata("D", k1),
                TestMetadata("B", k2),
                TestMetadata("C", k3),
            ), messageChainOf(
                TestMetadata("A", k1),
                TestMetadata("B", k2),
                TestMetadata("C", k3),
                TestMetadata("D", k1),
            ).metadata
        )
    }

    @TestFactory
    fun assertMetadataKeyTreeOverride(): List<DynamicNode> {
        val k1 = MessageMetadataKey<TestMetadata>(TestMetadata)
        val k2 = MessageMetadataKey<TestMetadata>(TestMetadata)

        return listOf(
            DynamicTest.dynamicTest("Tree root override") {
                val messageChain = messageChainOf(
                    TestMetadata("A", TestMetadata), TestMetadata("B", k1), TestMetadata("C", k2)
                )
                assertEquals(1, messageChain.metadata.size)
                assertEquals(TestMetadata("C", k2), messageChain[k2])
                assertEquals(TestMetadata("C", k2), messageChain[TestMetadata])
                assertNull(messageChain[k1])
            },
            DynamicTest.dynamicTest("sub node override") {
                val messageChain = messageChainOf(
                    TestMetadata("B", k1), TestMetadata("C", k2), TestMetadata("D", k1)
                )
                assertEquals(1, messageChain.metadata.size)
                assertEquals(TestMetadata("D", k1), messageChain[k1])
                assertEquals(TestMetadata("D", k1), messageChain[TestMetadata])
                assertNull(messageChain[k2])
            }
        )
    }

    private data class TestMetadata(
        val content: String,
        override val key: MessageMetadataKey<*> = TestMetadata,
    ) : MessageMetadata {

        override fun contentToString(): String = ""
        override fun toString(): String = "TestMetadata[$content]"

        companion object Key : MessageMetadataKey<TestMetadata>() {}
    }
}