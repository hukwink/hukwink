package com.hukwink.hukwink.message

public class Mention(
    public val target: String,
) : MessageElement {
    override fun toString(): String {
        return "[mention:$target]"
    }
}