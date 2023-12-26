package com.hukwink.hukwink.message

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.apiinternal.serialization.MediaOuterLayout
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.resource.ExternalResource

public interface Media : ExternalResource, MessageElement {


    public val fileName: String

    public fun openCoverImage(bot: Bot): ExternalResource.ResourceHandle


    public companion object Key {
        @JvmStatic
        public fun outerLayout(): MessageOuterLayoutSerializer<Media> = MediaOuterLayout
    }
}
