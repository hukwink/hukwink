package com.hukwink.hukwink.resource

import com.hukwink.hukwink.Bot
import java.io.Closeable

public interface ExternalResource {
    public interface ResourceHandle : Closeable {
        public val url: String
    }

    public fun openResource(bot: Bot): ResourceHandle
}
