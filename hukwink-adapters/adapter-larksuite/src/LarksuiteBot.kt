package com.hukwink.hukwink.adapter.larksuite

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.adapter.larksuite.chatting.LarksuiteChat
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess.ensureOk
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess.parseToJsonAndVerify
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteResourceExposeAdapter
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteTokenKeepHolder
import com.hukwink.hukwink.adapter.larksuite.http.larksuiteAuthorization
import com.hukwink.hukwink.adapter.larksuite.message.image.LarksuiteImageUploaded
import com.hukwink.hukwink.adapter.larksuite.netprocess.EventProcessorRegistry
import com.hukwink.hukwink.adapter.larksuite.proto.ProtoBotInfo
import com.hukwink.hukwink.adapter.larksuite.resource.ResourcesCacheManager
import com.hukwink.hukwink.chatting.ChatId
import com.hukwink.hukwink.chatting.ChatType
import com.hukwink.hukwink.chatting.Chatting
import com.hukwink.hukwink.contact.ChatInfo
import com.hukwink.hukwink.event.engine.EventEngine
import com.hukwink.hukwink.message.Image
import com.hukwink.hukwink.resource.LocalResource
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

public class LarksuiteBot(
    override val configuration: LarksuiteConfiguration,
    override val coroutineScope: CoroutineScope,

    internal val httpClient: HttpClient,
    internal val webClient: WebClient,
) : Bot {
    override val eventEngine: EventEngine get() = configuration.eventEngine

    public val eventProcessorRegistry: EventProcessorRegistry = EventProcessorRegistry()
    internal val tokenKeepHolder = LarksuiteTokenKeepHolder(this)
    internal val larksuiteResourceExposeAdapter = LarksuiteResourceExposeAdapter(this)
    internal val resourcesCacheManager = ResourcesCacheManager(this)

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

        resourcesCacheManager.initialize()
    }

    override fun close() {
        coroutineScope.coroutineContext.job.cancel()
    }

    override fun close(reason: Throwable) {
    }

    override fun openChat(chatId: ChatId): Chatting {
        return LarksuiteChat(
            bot = this,
            chatType = ChatType.UNKNOWN_CHAT_TYPE,
            chatInfo = object : ChatInfo {
                override val chatId: ChatId get() = chatId
                override val chatName: String get() = "<Unknown Outgoing Chat>"
            }
        )
    }


    internal suspend fun uploadImageImpl(resource: LocalResource): String {
        return resourcesCacheManager.upload(resource, ResourcesCacheManager.SubType.IMAGE)
    }

    internal suspend fun uploadFileImpl(resource: LocalResource): String {
        return resourcesCacheManager.upload(resource, ResourcesCacheManager.SubType.FILE)
    }

    public suspend fun uploadImage(resource: LocalResource): Image {
        return LarksuiteImageUploaded(uploadImageImpl(resource))
    }
}