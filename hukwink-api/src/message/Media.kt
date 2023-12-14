package com.hukwink.hukwink.message

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.resource.ExternalResource

public interface Media : ExternalResource, MessageElement {
    public val fileName: String

    public fun openCoverImage(bot: Bot): ExternalResource.ResourceHandle
}
