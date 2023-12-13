package com.hukwink.hukwink.message

@Suppress("RemoveRedundantQualifierName")
public open class PlainText @JvmOverloads public constructor(
    public val content: CharSequence,
    public val styles: Set<PlainText.Style> = emptySet(),
) : MessageElement {
    override fun toString(): String {
        return content.toString()
    }

    public enum class Style {
        BOLD,
        UNDERLINE,
        ITALIC,
        LINE_THROUGH,
    }
}
