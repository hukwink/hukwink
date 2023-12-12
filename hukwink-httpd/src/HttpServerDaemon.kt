package com.hukwink.hukwink.httpd

import com.hukwink.hukwink.util.childScope
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.net.SocketAddress
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.coroutineRouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public class HttpServerDaemon(
    public val vertx: Vertx,
    context: CoroutineContext = EmptyCoroutineContext,
) {
    public val router: Router = Router.router(vertx)
    public val scope: CoroutineScope = context.childScope("Hukwink Httpd")
    private lateinit var httpServer0: HttpServer

    public val httpServer: HttpServer get() = httpServer0

    public fun setup(): Unit = with(scope) {
        coroutineRouter {
            router.get("/").handler { ctx ->
                ctx.response().putHeader("Context-Type", "application/plain-text")

                ctx.response().end("Project HukWink. From ${ctx.request().remoteAddress()}")
            }
        }
    }

    public fun start(
        address: SocketAddress
    ) {
        val server = vertx.createHttpServer()
            .requestHandler(router)
            .listen(address)
            .toCompletionStage()
            .toCompletableFuture()
            .join()

        this.httpServer0 = server

        scope.coroutineContext.job.invokeOnCompletion { server.close() }
    }

    public fun close() {
        scope.cancel()
    }
}