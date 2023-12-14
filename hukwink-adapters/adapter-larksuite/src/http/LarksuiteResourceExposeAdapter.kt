package com.hukwink.hukwink.adapter.larksuite.http

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.util.childScope
import io.vertx.core.http.HttpMethod
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

internal class LarksuiteResourceExposeAdapter(
    val bot: LarksuiteBot
) {
    private val adapterScope = bot.coroutineScope.coroutineContext.childScope("LarksuiteResourceExposeAdapter")

    val folder = "/larksuite-res-expose-"// + UUID.randomUUID()

    init {
        val router = bot.configuration.httpServerDaemon.router
        println(folder)
        val theRoute = router.get("$folder/*")
        adapterScope.coroutineContext.job.invokeOnCompletion { theRoute.remove() }

        theRoute.handler { ctx ->
            // /open-apis/im/v1/messages/:message_id/resources/:file_key
            val ptx = ctx.normalizedPath().substring(folder.length + 1)

            // type/msgid/filekey
            val pathSplit = ptx.split('/')

            if (pathSplit.size != 3) {
                ctx.response()
                    .setStatusCode(403)
                    .end("Bad request path: $ptx")
                return@handler
            }

            val (resType, messageId, fileKey) = pathSplit

            adapterScope.launch(CoroutineName("adapter request# $messageId - $fileKey") + ctx.vertx().dispatcher()) {
                val replied = bot.httpClient.request(
                    HttpMethod.GET,
                    "/open-apis/im/v1/messages/$messageId/resources/$fileKey?type=$resType"
                ).coAwait()
                    .setFollowRedirects(true)
                    .larksuiteAuthorization(bot).send().coAwait()

                ctx.response().setStatusCode(replied.statusCode())

                replied.getHeader("Content-Type")?.let {
                    ctx.response().putHeader("Content-Type", it)
                }

                ctx.response().send(replied).coAwait()
            }
        }
    }
}