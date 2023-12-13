package com.hukwink.hukwink.message

public interface MessageMetadata : Message {
    public val key: MessageMetadataKey<*>
}