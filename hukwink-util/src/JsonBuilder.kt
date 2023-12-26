@file:JvmMultifileClass
@file:JvmName("HukwinkUtilKt")

package com.hukwink.hukwink.util

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@DslMarker
public annotation class HukwinkJsonDsl

@HukwinkJsonDsl
public class HukwinkJsonArrayBuilder(
    public val delegate: MutableList<JsonElement>,
) {
    public fun build(): JsonArray = JsonArray(delegate)

    @HukwinkJsonDsl
    public operator fun plusAssign(value: JsonElement) {
        delegate.add(value)
    }

    @HukwinkJsonDsl
    public operator fun plusAssign(value: String) {
        delegate.add(JsonPrimitive(value))
    }

    @HukwinkJsonDsl
    public operator fun plusAssign(value: Number) {
        delegate.add(JsonPrimitive(value))
    }

    @HukwinkJsonDsl
    public operator fun plusAssign(value: Boolean) {
        delegate.add(JsonPrimitive(value))
    }

    @HukwinkJsonDsl
    public inline operator fun plusAssign(builder: HukwinkJsonObjectBuilder.() -> Unit) {
        delegate.add(hukwinkBuildJsonObject(builder))
    }

    @HukwinkJsonDsl
    public inline infix fun arr(builder: HukwinkJsonArrayBuilder.() -> Unit) {
        delegate.add(hukwinkBuildJsonArray(builder))
    }

    @HukwinkJsonDsl
    public inline infix fun obj(builder: HukwinkJsonObjectBuilder.() -> Unit) {
        delegate.add(hukwinkBuildJsonObject(builder))
    }
}

@HukwinkJsonDsl
public class HukwinkJsonObjectBuilder(
    public val delegate: MutableMap<String, JsonElement>,
) {
    public fun build(): JsonObject = JsonObject(delegate)

    @HukwinkJsonDsl
    public operator fun String.invoke(value: JsonElement) {
        delegate[this] = value
    }

    @HukwinkJsonDsl
    public operator fun String.invoke(value: String) {
        delegate[this] = JsonPrimitive(value)
    }

    @HukwinkJsonDsl
    public operator fun String.invoke(value: Number) {
        delegate[this] = JsonPrimitive(value)
    }

    @HukwinkJsonDsl
    public operator fun String.invoke(value: Boolean) {
        delegate[this] = JsonPrimitive(value)
    }


    @HukwinkJsonDsl
    public inline infix fun String.arr(builder: HukwinkJsonArrayBuilder.() -> Unit) {
        delegate[this] = hukwinkBuildJsonArray(builder)
    }

    @HukwinkJsonDsl
    public inline infix fun String.obj(builder: HukwinkJsonObjectBuilder.() -> Unit) {
        delegate[this] = hukwinkBuildJsonObject(builder)
    }
}


@HukwinkJsonDsl
public inline fun hukwinkBuildJsonObject(builder: HukwinkJsonObjectBuilder.() -> Unit): JsonObject {
    return HukwinkJsonObjectBuilder(linkedMapOf()).also(builder).build()
}

@HukwinkJsonDsl
public inline fun hukwinkBuildJsonArray(builder: HukwinkJsonArrayBuilder.() -> Unit): JsonArray {
    return HukwinkJsonArrayBuilder(mutableListOf()).also(builder).build()
}