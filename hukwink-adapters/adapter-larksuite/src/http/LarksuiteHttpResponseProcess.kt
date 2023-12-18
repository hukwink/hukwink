package com.hukwink.hukwink.adapter.larksuite.http

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClientResponse
import io.vertx.ext.web.client.HttpResponse
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

internal object LarksuiteHttpResponseProcess {
    val jsonHttpProcess = Json {
        ignoreUnknownKeys = true
    }
    val jsonPrettyPrint = Json {
        prettyPrint = true
    }

    suspend fun HttpClientResponse.ensureOk(): HttpClientResponse {
        if (statusCode() != 200) {
            error("Bad status code: ${statusCode()}, ${body().coAwait().toString(Charsets.UTF_8)}")
        }
        return this
    }

    fun <T : HttpResponse<*>> T.ensureOk(): T {
        if (statusCode() != 200) {
            error("Bad status code: ${statusCode()}, ${bodyAsString()}")
        }
        return this
    }

    fun Buffer.parseToJson(): JsonObject {
        return Json.Default.decodeFromString(JsonObject.serializer(), toString(Charsets.UTF_8))
    }

    fun Buffer.parseToJsonAndVerify(): JsonObject {
        return parseToJson().also { obj ->
            if (obj["code"]?.jsonPrimitive?.int != 0) {
                error(obj.toString())
            }
        }
    }
}