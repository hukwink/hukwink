package com.hukwink.hukwink.message

import com.hukwink.hukwink.apiinternal.serialization.FileOuterLayout
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.resource.ExternalResource

public interface File : MessageElement, ExternalResource {
    public val fileName: String

    public companion object Key {
        @JvmStatic
        public fun outerLayout(): MessageOuterLayoutSerializer<File> = FileOuterLayout
    }
}
