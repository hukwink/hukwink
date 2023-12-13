package com.hukwink.hukwink.message

@Suppress("RemoveRedundantQualifierName")
public open class Hyperlink
@JvmOverloads
public constructor(
    content: String,
    public val hyperlink: String,
    styles: Set<PlainText.Style> = emptySet(),
) : PlainText(content, styles) {
}
