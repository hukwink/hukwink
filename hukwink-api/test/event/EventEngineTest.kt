package com.hukwink.hukwink.event

import com.hukwink.hukwink.event.engine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Timeout
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@Timeout(5, unit = TimeUnit.SECONDS)
class EventEngineTest {
    private lateinit var eventEngine: EventEngine
    private lateinit var scope: CoroutineScope

    @BeforeEach
    fun setup() {
        scope = CoroutineScope(EmptyCoroutineContext)
        eventEngine = EventEngineBuilder()
            .withScope(scope)
            .withLogger(LoggerFactory.getLogger(EventEngineTest::class.java))
            .build()
    }

    @AfterEach
    fun release() {
        scope.coroutineContext.job.cancel()
    }


    @Test
    fun baseTest() = runTest {
        lateinit var evt: Any
        eventEngine.eventChannel.listen {
            evt = it
        }

        eventEngine.fireAndWait("Hello Event Line")
        assertEquals("Hello Event Line", evt)

        eventEngine.fireAndWait("Omg")
        assertEquals("Omg", evt)
    }

    @Test
    fun testFilter() = runTest {
        lateinit var evt: Any
        eventEngine.eventChannel.filter { it != System.out }.listen {
            evt = it
        }

        eventEngine.fireAndWait(System.err)
        assertSame(System.err, evt)

        eventEngine.fireAndWait(System.out)
        assertSame(System.err, evt)
    }

    @Test
    fun testExceptionNoAffect() = runTest {
        eventEngine.eventChannel.listen { error("Should not happened") }

        eventEngine.fireAndWait("")
    }

    @Test
    fun testDispose() = runTest {
        lateinit var envValue: Any
        val handler = eventEngine.eventChannel.listen { envValue = it }

        eventEngine.fireAndWait("Test 1")
        handler.dispose()
        eventEngine.fireAndWait("Test 2")

        assertEquals("Test 1", envValue)
    }

    @Test
    fun testChannelScope() = runTest {
        var envValue: Any = "Val"

        val newScope = CoroutineScope(EmptyCoroutineContext)
        eventEngine.eventChannel.withContext(newScope.coroutineContext).listen { envValue = it }

        eventEngine.fireAndWait("Test")
        assertEquals("Test", envValue)

        newScope.coroutineContext.job.cancelAndJoin()
        eventEngine.fireAndWait("Test5")
        assertEquals("Test", envValue)
    }

    @Nested
    inner class EventHostTest {
        open inner class TestHost : EventHost() {
            private lateinit var testScope: TestScope

            fun reg0(testScope: TestScope, target: EventChannel<*>) {
                this.testScope = testScope
                registerAll(target)
            }


            override fun exceptionCaught(context: CoroutineContext, error: Throwable) {
                if (!::testScope.isInitialized) return

                testScope.cancel(
                    CancellationException(
                        "Exception in test host",
                        error
                    )
                )
            }
        }

        @Nested
        inner class NonSuspend {
            @Test
            fun simpleListen() = runTest {
                lateinit var evt: Any
                object : TestHost() {
                    @Listen
                    fun String.listen() {
                        evt = this
                    }
                }.reg0(this, eventEngine.eventChannel)

                eventEngine.fireAndWait("A")
                eventEngine.fireAndWait(System.out)

                assertEquals("A", evt)
            }

            @Test
            fun withContext() = runTest {
                lateinit var evt1: Any
                lateinit var evt2: Any
                object : TestHost() {
                    @Listen
                    fun String.listen(ctx: EventContext) {
                        evt1 = this
                    }

                    @Listen
                    fun EventContext.listen(evt: String) {
                        evt2 = evt
                    }

                }.reg0(this, eventEngine.eventChannel)

                eventEngine.fireAndWait("A")
                eventEngine.fireAndWait(System.out)

                assertEquals("A", evt1)
                assertEquals("A", evt2)
            }

            @Test
            @Timeout(10, unit = TimeUnit.SECONDS)
            fun completableFutureReturnValue() = runTest {
                object : TestHost() {
                    @Listen
                    fun handler1(evt: String): CompletableFuture<Void> {
                        return CompletableFuture<Void>().also { future ->
                            ForkJoinPool.commonPool().submit {
                                Thread.dumpStack()
                                Thread.sleep(1000L)
                                future.completeExceptionally(Throwable("Stack trace"))
                                Thread.dumpStack()
                            }
                        }
                    }

                    override fun exceptionCaught(context: CoroutineContext, error: Throwable) {
                        LoggerFactory.getLogger(EventEngineTest::class.java).info("completableFutureReturnValue", error)
                    }
                }.reg0(this, eventEngine.eventChannel)

                eventEngine.fireAndWait("")
            }

            @Test
            @Timeout(10, unit = TimeUnit.SECONDS)
            fun completableFutureParameterValue() = runTest {
                object : TestHost() {
                    @Listen
                    fun handler2(evt: String, callback: CompletableFuture<Void>) {
                        ForkJoinPool.commonPool().submit {
                            Thread.dumpStack()
                            Thread.sleep(1000L)
                            callback.completeExceptionally(Throwable("Stack trace"))
                            Thread.dumpStack()
                        }
                    }

                    override fun exceptionCaught(context: CoroutineContext, error: Throwable) {
                        LoggerFactory.getLogger(EventEngineTest::class.java)
                            .info("completableFutureParameterValue", error)
                    }
                }.reg0(this, eventEngine.eventChannel)

                eventEngine.fireAndWait("")
            }
        }

        @Nested
        inner class Suspended {
            @Test
            fun simpleListen() = runTest {
                lateinit var evt: Any
                object : TestHost() {
                    @Listen
                    suspend fun String.listen() {
                        evt = this
                    }
                }.reg0(this, eventEngine.eventChannel)

                eventEngine.fireAndWait("A")
                eventEngine.fireAndWait(System.out)

                assertEquals("A", evt)
            }

            @Test
            fun withContext() = runTest {
                lateinit var evt1: Any
                lateinit var evt2: Any
                object : TestHost() {
                    @Listen
                    suspend fun String.listen(ctx: EventContext) {
                        evt1 = this
                    }

                    @Listen
                    suspend fun EventContext.listen(evt: String) {
                        evt2 = evt
                    }

                }.reg0(this, eventEngine.eventChannel)

                eventEngine.fireAndWait("A")
                eventEngine.fireAndWait(System.out)

                assertEquals("A", evt1)
                assertEquals("A", evt2)
            }
        }
    }
}