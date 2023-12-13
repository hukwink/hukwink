package com.hukwink.hukwink.message

public open class MessageMetadataKey<T>(
    public val parentKey: MessageMetadataKey<*>? = null,
) {
    public val keyChain: List<MessageMetadataKey<*>> = keySequence().toList().asReversed()

    public fun keySequence(): Sequence<MessageMetadataKey<*>> =
        generateSequence<MessageMetadataKey<*>>(this) { it.parentKey }
}
