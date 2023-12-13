package com.hukwink.hukwink.adapter.larksuite

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess.ensureOk
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess.parseToJsonAndVerify
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteResourceExposeAdapter
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteTokenKeepHolder
import com.hukwink.hukwink.adapter.larksuite.http.larksuiteAuthorization
import com.hukwink.hukwink.adapter.larksuite.netprocess.EventProcessorRegistry
import com.hukwink.hukwink.adapter.larksuite.proto.ProtoBotInfo
import com.hukwink.hukwink.chatting.ChatId
import com.hukwink.hukwink.chatting.ChatType
import com.hukwink.hukwink.chatting.Chatting
import com.hukwink.hukwink.event.engine.EventEngine
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpMethod
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

public class LarksuiteBot(
    override val configuration: LarksuiteConfiguration,
    override val coroutineScope: CoroutineScope,

    internal val httpClient: HttpClient,
) : Bot {
    override val eventEngine: EventEngine get() = configuration.eventEngine

    public val eventProcessorRegistry: EventProcessorRegistry = EventProcessorRegistry()
    internal val tokenKeepHolder = LarksuiteTokenKeepHolder(this)
    internal val larksuiteResourceExposeAdapter = LarksuiteResourceExposeAdapter(this)

    internal val logger get() = configuration.logger

    override val isActive: Boolean get() = true
    override lateinit var botName: String private set
    override lateinit var botId: String private set

    override suspend fun login() {
        tokenKeepHolder.refreshToken()
        coroutineScope.launch(CoroutineName("Token Refresher")) {
            while (isActive) {
                delay(TimeUnit.MINUTES.toMillis(10))
                try {
                    tokenKeepHolder.refreshToken()
                } catch (e: Throwable) {
                    logger.warn("Exception when refreshing token", e)
                }
            }
        }
        val reply = httpClient.request(HttpMethod.GET, "https://open.feishu.cn/open-apis/bot/v3/info").coAwait()
            .larksuiteAuthorization(this)
            .send().coAwait().ensureOk()
            .body().coAwait()
            .parseToJsonAndVerify()

        val protoBotInfo = LarksuiteHttpResponseProcess.jsonHttpProcess.decodeFromJsonElement(
            ProtoBotInfo.serializer(), reply["bot"] ?: error("Failed to fetch bot info")
        )

        this.botId = protoBotInfo.open_id
        this.botName = protoBotInfo.app_name

    }

    override fun close() {
        coroutineScope.coroutineContext.job.cancel()
    }

    override fun close(reason: Throwable) {
    }

    override fun openChat(type: ChatType, chatId: ChatId): Chatting {
        TODO("Not yet implemented")
    }
}