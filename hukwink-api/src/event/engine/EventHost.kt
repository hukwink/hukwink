package com.hukwink.hukwink.event.engine

import com.hukwink.hukwink.util.mapValue
import com.hukwink.hukwink.util.removeFirst
import kotlinx.coroutines.CoroutineExceptionHandler
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction

public open class EventHost {
    protected open val context: CoroutineContext by lazy {
        return@lazy CoroutineExceptionHandler(this::exceptionCaught)
    }

    @Target(AnnotationTarget.FUNCTION)
    public annotation class Listen(
        val value: EventPriority = EventPriority.NORMAL,
    )

    public fun registerAll(target: EventChannel<*>) {
        fun processJavaFunction(met: Method): suspend EventContext.(evt: Any) -> Unit {
            val paramList = met.parameterTypes.asSequence()
                .mapIndexed { index, parameter -> index to parameter }
                .toMutableList()

            val evtContextParam = paramList.removeFirst { (_, type) -> type == EventContext::class.java }
            val futureParam = paramList.removeFirst { (_, type) -> type == CompletableFuture::class.java }
            val isFutureResult = met.returnType == CompletableFuture::class.java
            if (futureParam != null && isFutureResult) {
                error("Could not use future param async and future return async in same time.")
            }
            if (paramList.size != 1) {
                error("Could not detect event type from $met")
            }
            val evtType = paramList[0]

            var asMethodHandle = lookup().unreflect(met)
            if (!Modifier.isStatic(met.modifiers)) {
                asMethodHandle = asMethodHandle.bindTo(this@EventHost)
            }

            // step: convert to (EventContext, Object, CompletableFuture?)
            kotlin.run {
                val oldType = asMethodHandle.type()
                val newOrder = IntArray(oldType.parameterCount())
                val newType = when {
                    futureParam != null -> MethodType.methodType(
                        oldType.returnType(),
                        EventContext::class.java,
                        evtType.second,
                        CompletableFuture::class.java,
                    )

                    else -> MethodType.methodType(
                        oldType.returnType(),
                        EventContext::class.java,
                        evtType.second,
                    )
                }
                if (newType != oldType) {
                    evtContextParam?.let { newOrder[it.first] = 0 }
                    futureParam?.let { newOrder[it.first] = 2 }
                    newOrder[evtType.first] = 1

                    asMethodHandle = MethodHandles.permuteArguments(asMethodHandle, newType, *newOrder)
                }
            }

            if (futureParam != null) {
                asMethodHandle = MethodHandles.filterArguments(asMethodHandle, 2, implCAsCF)
                asMethodHandle = MethodHandles.dropReturn(asMethodHandle)
                asMethodHandle = MethodHandles.filterReturnValue(asMethodHandle, implMhSuspended)
            } else if (isFutureResult) {
                asMethodHandle = MethodHandles.collectArguments(
                    implReturnCF, 0, asMethodHandle
                )
            } else {
                asMethodHandle = MethodHandles.dropArguments(asMethodHandle, 2, Continuation::class.java)
            }

            asMethodHandle = asMethodHandle.asType(
                MethodType.methodType(
                    Any::class.java,
                    EventContext::class.java, Any::class.java, Continuation::class.java
                )
            )

            // final return type: (EventContext, Object, Continuation)Object
            val evtTypeKlass = evtType.second
            return handler@{ evt ->
                if (!evtTypeKlass.isInstance(evt)) return@handler

                return@handler kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn { cont ->
                    return@suspendCoroutineUninterceptedOrReturn asMethodHandle.invokeExact(this@handler, evt, cont)
                }
            }
        }

        fun processKotlinFunction(
            func: KFunction<*>
        ): suspend EventContext.(evt: Any) -> Unit {
            if (!func.isSuspend) {
                return processJavaFunction(func.javaMethod!!)
            }

            val paramsList = func.parameters.toMutableList()

            val thisParam: KParameter? = paramsList.removeFirst { it.kind == KParameter.Kind.INSTANCE }
            val ctxParam: KParameter? = paramsList.removeFirst { it.type.classifier == EventContext::class }
            paramsList.removeAll { it.isOptional }

            if (paramsList.size != 1) {
                error("Could not detect event type from $func")
            }
            val evtParam: KParameter = paramsList[0]
            val evtType = evtParam.type.classifier
            if (evtType !is KClass<*>) {
                error("$evtType not a constant klass type")
            }

            return handler@{ evt ->
                if (!evtType.isInstance(evt)) return@handler

                val callmap = mutableMapOf<KParameter, Any?>()
                thisParam?.let { callmap[it] = this@EventHost }
                ctxParam?.let { callmap[it] = this@handler }
                callmap[evtParam] = evt

                func.callSuspendBy(callmap)
            }
        }

        val reactedTarget = target.withContext(context)

        generateSequence<Class<*>>(javaClass) { crtKlass ->
            val sup = crtKlass.superclass
            if (sup == EventHost::class.java)
                return@generateSequence null

            return@generateSequence sup
        }
            .flatMap { it.declaredMethods.asSequence() }
            .mapNotNull { met ->
                val theListen = met.getDeclaredAnnotation(Listen::class.java) ?: return@mapNotNull null

                return@mapNotNull theListen to (met to met.kotlinFunction)
            }
            .mapValue { (_, func) ->
                func.second?.let { kfunc ->
                    return@mapValue processKotlinFunction(kfunc)
                }
                return@mapValue processJavaFunction(func.first)
            }
            .forEach { (anno, handler) ->
                reactedTarget.listen(priority = anno.value, handler)
            }

    }

    protected open fun lookup(): MethodHandles.Lookup {
        val lookupMe = MethodHandles.lookup()
        try {
            return MethodHandles.privateLookupIn(javaClass, lookupMe)
        } catch (_: Throwable) {
        }

        return lookupMe
    }

    protected open fun exceptionCaught(context: CoroutineContext, error: Throwable) {
        klassLogger.warn("Exception in {} but exceptionCaught not implemented", this, error)
    }

    public companion object {
        private val lookup0 = MethodHandles.lookup()

        private val klassLogger = LoggerFactory.getLogger(EventHost::class.java)

        private val implCAsCF by lazy {
            lookup0.findStatic(
                EventHost::class.java,
                "implContinuationAsCompletableFuture",
                MethodType.methodType(CompletableFuture::class.java, Continuation::class.java)
            )
        }

        private val implReturnCF by lazy {
            lookup0.findStatic(
                EventHost::class.java,
                "implAsyncReturnCompleteFuture",
                MethodType.methodType(Any::class.java, CompletableFuture::class.java, Continuation::class.java)
            )
        }

        private val implMhSuspended: MethodHandle by lazy {
            MethodHandles.constant(java.lang.Object::class.java, kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED)
        }

        @PublishedApi
        @JvmStatic
        internal fun implAsyncReturnCompleteFuture(resp: CompletableFuture<Any?>?, cont: Continuation<Unit>): Any {
            if (resp == null) {
                return Unit
            }
            resp.handle { _, u ->

                if (u != null) {
                    cont.resumeWithException(u)
                } else {
                    cont.resume(Unit)
                }
            }
            return kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
        }

        @PublishedApi
        @JvmStatic
        internal fun implContinuationAsCompletableFuture(cont: Continuation<Unit>): CompletableFuture<*> {
            return CompletableFuture<Any?>().also { resp ->
                resp.handle { _, u ->
                    if (u != null) {
                        cont.resumeWithException(u)
                    } else {
                        cont.resume(Unit)
                    }
                }
            }
        }
    }
}