package com.hukwink.hukwink.adapter.larksuite

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.BotFactory
import com.hukwink.hukwink.adapter.larksuite.netprocess.DebugPrintProcessor
import com.hukwink.hukwink.adapter.larksuite.netprocess.LarksuiteWebhookProcessor
import com.hukwink.hukwink.config.BotConfiguration
import com.hukwink.hukwink.util.childScope
import io.vertx.core.http.HttpClientOptions
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job

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
            )
            botScope.coroutineContext.job.invokeOnCompletion { httpClient.close() }

            val botImpl = LarksuiteBot(
                configuration,
                botScope,
                httpClient
            )
            LarksuiteWebhookProcessor(botImpl).register()

            return botImpl
        } catch (e: Throwable) {
            botScope.cancel()
            throw e
        }
    }
}