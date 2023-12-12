package com.hukwink.hukwink.chatting

public open class ChatType(
    public val typeName: String,
) {
    override fun toString(): String {
        return "ChatType[$typeName]"
    }

    override fun hashCode(): Int {
        return typeName.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    public companion object {
        public val PRIVATE_CHAT: ChatType = ChatType("PRIVATE_CHAT")
        public val GROUP_CHAT: ChatType = ChatType("GROUP_CHAT")
    }
}