package com.hukwink.hukwink.message

public open class Mention(
    public val target: String,
) : MessageElement {
    override fun toString(): String {
        return "[mention:$target]"
    }

    override fun contentToString(): String {
        return "@$target"
    }
}