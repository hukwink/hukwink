package com.hukwink.hukwink.message

public class PlainText(
    public val content: CharSequence,
) : MessageElement {
    override fun toString(): String {
        return content.toString()
    }
}
