package com.hukwink.hukwink.message

import com.hukwink.hukwink.apiinternal.message.MessageChainImpl
import com.hukwink.hukwink.util.immutable
import com.hukwink.hukwink.util.removeAllAndPick

public object MessageUtil {
    private val emptyMessageChain = MessageChainImpl(emptyMap(), emptyList())

    @JvmStatic
    public fun emptyMessageChain(): MessageChain = emptyMessageChain

    @JvmSynthetic
    public fun messageChainOf(): MessageChain = emptyMessageChain

    @JvmSynthetic
    public fun messageChainOf(vararg elm: Message): MessageChain = elm.iterator().toMessageChain()


    @JvmStatic
    public fun Sequence<@JvmWildcard Message>.toMessageChain(): MessageChain {
        return iterator().toMessageChain()
    }

    @JvmStatic
    public fun Iterable<@JvmWildcard Message>.toMessageChain(): MessageChain {
        return iterator().toMessageChain()
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    public fun Iterator<@JvmWildcard Message>.toMessageChain(): MessageChain {
        val elms = this.asSequence()
            .flatMap { elm ->
                if (elm is MessageChain) elm.asSequence()
                else sequenceOf(elm)
            }
            .toMutableList()
        val metadata = elms.removeAllAndPick { it is MessageMetadata } as List<MessageMetadata>
        elms.removeAll { it !is MessageElement }

        val metadataMap = mutableMapOf<MessageMetadataKey<*>, MessageMetadata>()
        metadata.forEach { mt ->
            mt.key.keyChain.forEach { key ->

                metadataMap.put(key, mt)?.let { old ->
                    old.key.keyChain.asSequence()
                        .filterNot { mt.key.keyChain.contains(it) }
                        .forEach { metadataMap.remove(it) }
                }

            }
        }

        @Suppress("UNCHECKED_CAST")
        return MessageChainImpl(metadataMap, elms.immutable() as List<MessageElement>)
    }

    @JvmStatic
    public fun Message.toMessageChain(): MessageChain {
        if (this is MessageChain) return this
        return sequenceOf(this).toMessageChain()
    }

    @JvmStatic
    public fun MessageChain.asSequence(): Sequence<Message> {
        return metadata.asSequence() + content.asSequence()
    }
}
