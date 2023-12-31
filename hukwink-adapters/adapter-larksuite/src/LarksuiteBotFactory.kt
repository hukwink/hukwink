package com.hukwink.hukwink.adapter.larksuite

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.BotFactory
import com.hukwink.hukwink.adapter.larksuite.message.serialization.LarksuiteSerializationRegistration
import com.hukwink.hukwink.adapter.larksuite.netprocess.LarksuiteWebhookProcessor
import com.hukwink.hukwink.config.BotConfiguration
import com.hukwink.hukwink.message.serialization.HukwinkMessageSerialization
import com.hukwink.hukwink.util.childScope
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpVersion
import io.vertx.ext.web.client.WebClient
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import java.util.concurrent.TimeUnit

public class LarksuiteBotFactory : BotFactory {
    override fun createBot(configuration: BotConfiguration): Bot {
        if (configuration !is LarksuiteConfiguration) {
            error("Require a larksuite configuration")
        }
        val botScope = configuration.parentContext.childScope("Larksuite Bot")

        try {
            val httpClient = configuration.httpServerDaemon.vertx.createHttpClient(
                HttpClientOptions()
                    .setName("Larksuite Bot Http Client")
                    .setShared(false)
                    .setSsl(true)
                    .setDefaultHost("open.feishu.cn")
                    .setDefaultPort(443)
                    .setProtocolVersion(HttpVersion.HTTP_1_1)
                    .setIdleTimeout(10)
                    .setIdleTimeoutUnit(TimeUnit.SECONDS)
            )
            botScope.coroutineContext.job.invokeOnCompletion { httpClient.close() }

            val botImpl = LarksuiteBot(
                configuration,
                botScope,
                httpClient,
                webClient = WebClient.wrap(httpClient),
            )
            LarksuiteWebhookProcessor(botImpl).register()

            return botImpl
        } catch (e: Throwable) {
            botScope.cancel()
            throw e
        }
    }


    override fun registerMessageSerializers(serialization: HukwinkMessageSerialization) {
        LarksuiteSerializationRegistration.register(serialization)
    }
}