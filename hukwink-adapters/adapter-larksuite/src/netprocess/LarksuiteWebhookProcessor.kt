package com.hukwink.hukwink.adapter.larksuite.netprocess

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.adapter.larksuite.util.LarksuiteWebhookDecrypt
import com.hukwink.hukwink.util.encodeHex
import com.hukwink.hukwink.util.sha256
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.coroutines.coroutineRouter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import java.io.SequenceInputStream


internal class LarksuiteWebhookProcessor(
    private val botImpl: LarksuiteBot
) {
    private val verificationToken = botImpl.configuration.verificationToken
    private val encryptKey = botImpl.configuration.encryptKey
    private val encryptKeySha256 = encryptKey.toByteArray(Charsets.UTF_8).inputStream().sha256()
    private val debugPrinter = DebugPrintProcessor()

    private object RejectedException : Throwable() {
        private fun readResolve(): Any = RejectedException
    }

    fun register() {
        val botConf = botImpl.configuration


        botImpl.coroutineScope.coroutineRouter {
            val httpd = botConf.httpServerDaemon
            httpd.router.post(botConf.webhookPath)
                .coHandler(requestHandler = this@LarksuiteWebhookProcessor::handle)
        }
    }


    private suspend fun handle(ctx: RoutingContext) {
        try {
            handle0(ctx)
        } catch (_: RejectedException) {
            ctx.response().setStatusCode(403).end()
        } catch (e: Throwable) {
            botImpl.configuration.logger.warn("Exception when processing webhook", e)
            ctx.response().setStatusCode(503).end()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun handle0(ctx: RoutingContext) {
        fun reject(reason: String): Nothing {
            ctx.request().headers().forEach { t, u ->
                println("$t --> $u")
            }
            botImpl.configuration.logger.debug("Webhook request was ignored because {}", reason)
            throw RejectedException
        }

        val originBodyBuffer = ctx.request().body().coAwait()

        var originMessage = originBodyBuffer.bytes

        if (encryptKey.isNotBlank()) {
            // verify
            val timestamp = ctx.request().getHeader("X-Lark-Request-Timestamp")
            val nonce = ctx.request().getHeader("X-Lark-Request-Nonce")

            if (timestamp != null && nonce != null) {
                val bytesB1 = (timestamp + nonce + encryptKey).toByteArray(Charsets.UTF_8)

                val sha256 = SequenceInputStream(
                    bytesB1.inputStream(), originMessage.inputStream()
                ).sha256().encodeHex()

                if (sha256 != ctx.request().getHeader("X-Lark-Signature").orEmpty()) {
                    botImpl.configuration.logger.warn(
                        "Received signature incorrect webhook event: Excepted {} but got {}",
                        sha256,
                        ctx.request().getHeader("X-Lark-Signature").orEmpty()
                    )
                    reject("sign not match")
                }
            }

            val jobj = originBodyBuffer.toJsonObject()
            originMessage = LarksuiteWebhookDecrypt.decrypt(
                encryptKeySha256,
                jobj.getString("encrypt") ?: error("No `encrypt` field found in webhook content")
            )
        }


        val evtMsg = kotlin.runCatching {
            originMessage.inputStream().use { stream ->
                Json.decodeFromStream(JsonObject.serializer(), stream)
            }
        }.getOrElse { throw IllegalStateException("Exception while parsing webhook content", it) }

        processEvent(evtMsg, ctx)

    }

    private suspend fun processEvent(evtMsg: JsonObject, ctx: RoutingContext) {
        val evtType = kotlin.runCatching {
            evtMsg["header"]?.jsonObject?.get("event_type")?.jsonPrimitive?.content
        }.getOrNull()
            ?: kotlin.runCatching {
                evtMsg["type"]?.jsonPrimitive?.content
            }.getOrNull()
            ?: ""

        if (evtType == "url_verification") {
            ctx.response().setStatusCode(200).end(evtMsg.toString()).coAwait()
            return
        }



        val evtProcessor = botImpl.eventProcessorRegistry.getProcessor(evtType)
            ?: botImpl.eventProcessorRegistry.defaultProcessor

        if (botImpl.configuration.webhookDebugPrint) {
            debugPrinter.process(evtMsg, botImpl)
        }

        if (evtProcessor == null) {
            botImpl.configuration.logger.debug("Unhandled webhook request: {}", evtMsg)
            ctx.response().setStatusCode(404).end()
        } else {
            evtProcessor.process(evtMsg, botImpl)
            ctx.response().setStatusCode(200).end()
        }

    }

}