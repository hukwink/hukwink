package com.hukwink.hukwink.message.serialization

import com.hukwink.hukwink.apiinternal.misc.decodeCharSequence
import com.hukwink.hukwink.apiinternal.misc.encodeCharSequence
import com.hukwink.hukwink.message.*
import com.hukwink.hukwink.message.MessageUtil.messageChainOf
import com.hukwink.hukwink.util.hukwinkBuildJsonObject
import io.netty.buffer.ByteBuf
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MessageSerializationTest {
    private val serialization = HukwinkMessageSerialization().apply {
        registerDefaults()
        register(TestMessageElm::class.java, TestMessageSerializer)
    }
    private val messageSerializer = PolymorphicSerializer(Message::class)
    val json = Json {
        prettyPrint = true
        serializersModule = serialization.serializersModule
    }

    @Test
    fun runTest() {

        assertEquals(
            hukwinkBuildJsonObject {
                "type"("messageChain")
                "chain" arr {
                    obj {
                        "type"("reply")
                        "internal"("0000001868756b77696e6b2e746573742e546573744d657373616765000000056d73676964")
                        "replyMessageId"("msgid")
                    }
                    obj {
                        "type"("text")
                        "content"("Hello World")
                    }
                    obj {
                        "type"("text")
                        "content"("Omg")
                        "style" arr {
                            this += "BOLD"
                        }
                    }
                    obj {
                        "type"("hyperlink")
                        "content"("Test")
                        "hyperlink"("https://github.com/hukwink/hukwink")
                    }
                    obj {
                        "type"("mention")
                        "target"("6")
                    }
                }
            },
            json.encodeToJsonElement(
                messageSerializer, messageChainOf(
                    PlainText("Hello World"),
                    PlainText("Omg", setOf(PlainText.Style.BOLD)),
                    Hyperlink("Test", "https://github.com/hukwink/hukwink"),
                    Mention("6"),
                    TestMessageElm("msgid")
                )
            ).also { println(json.encodeToString(JsonElement.serializer(), it)) }
        )
    }


    private class TestMessageElm(
        val value: String,
    ) : MessageQuoteReply {
        override val replyMessageId: String get() = value
        override val key: MessageMetadataKey<*> get() = MessageQuoteReply.Key

        override fun contentToString(): String = "Test Elm $value"
        override fun toString(): String = "Test Elm $value"
    }

    private object TestMessageSerializer : MessageInternalDataSerializer<TestMessageElm> {
        override val outerLayout: MessageOuterLayoutSerializer<TestMessageElm>
            get() = MessageQuoteReply.outerLayout()
        override val serialName: String
            get() = "hukwink.test.TestMessage"

        override fun decode(buffer: ByteBuf): TestMessageElm {
            return TestMessageElm(
                value = buffer.decodeCharSequence().toString(),
            )
        }

        override fun encode(message: TestMessageElm, buffer: ByteBuf) {
            buffer.encodeCharSequence(message.value)
        }

    }
}