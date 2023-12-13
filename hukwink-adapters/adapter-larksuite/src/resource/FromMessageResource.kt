package com.hukwink.hukwink.adapter.larksuite.resource

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.resource.ExternalResource

internal abstract class FromMessageResource : ExternalResource {
    internal abstract val messageId: String
    internal abstract val fileKey: String
    internal open val resourceType: String get() = "file"

    override fun openResource(bot: Bot): ExternalResource.ResourceHandle {
        // /open-apis/im/v1/messages/:message_id/resources/:file_key
        bot as LarksuiteBot
        val httpd = bot.configuration.httpServerDaemon
        val url =
            httpd.serverPrefix + bot.larksuiteResourceExposeAdapter.folder + "/" + resourceType + "/" + messageId + "/" + fileKey

        return ConstExternalResource(url)
    }

    private class ConstExternalResource(
        override val url: String
    ) : ExternalResource.ResourceHandle {
        override fun close() {
        }
    }
}