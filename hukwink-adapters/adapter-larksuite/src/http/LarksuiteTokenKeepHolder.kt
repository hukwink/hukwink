package com.hukwink.hukwink.adapter.larksuite.http

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.client.HttpRequest
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class LarksuiteTokenKeepHolder(
    private val bot: LarksuiteBot,
) {
    private val appid: String get() = bot.configuration.appId
    private val appsec: String get() = bot.configuration.appSecret

    var accessToken: String = ""
        private set

    suspend fun refreshToken() {
        val request = bot.webClient.request(
            HttpMethod.POST, "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal"
        )
        request.putHeader("Content-Type", "application/json; charset=utf-8")
        val response = request.sendBuffer(Buffer.buffer("{\"app_id\":\"$appid\",\"app_secret\":\"$appsec\"}")).coAwait()
        if (response.statusCode() != 200) {
            throw IllegalStateException("Bad code status: ${response.statusCode()}")
        }
        val responseBody = response.body()
        val responseObject = kotlin.runCatching {
            Json.Default.decodeFromString(
                JsonObject.serializer(), responseBody.toString(Charsets.UTF_8)
            )
        }.getOrElse {
            throw IllegalStateException(
                "Exception while parsing " + responseBody.toString(Charsets.UTF_8),
                it
            )
        }

        responseObject["tenant_access_token"]?.jsonPrimitive?.content
            ?.takeIf { it.isNotBlank() }
            ?.let { token ->
                accessToken = token
            }
            ?: error(responseBody.toString(Charsets.UTF_8))

        bot.logger.info("Refreshed tenant access token.")
    }

    fun makeRequest() {}
}

internal fun HttpClientRequest.larksuiteAuthorization(bot: LarksuiteBot) = apply {
    putHeader("Authorization", "Bearer ${bot.tokenKeepHolder.accessToken}")
}

internal fun <ST : Any?, T : HttpRequest<ST>> T.larksuiteAuthorization(bot: LarksuiteBot) = apply {
    putHeader("Authorization", "Bearer ${bot.tokenKeepHolder.accessToken}")
}