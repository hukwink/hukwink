package com.hukwink.hukwink.adapter.larksuite.resource

import com.hukwink.hukwink.Bot
import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.resource.ExternalResource

internal abstract class UploadedResource : ExternalResource {
    internal abstract val fileKey: String
    internal abstract val resourceType: String

    override fun openResource(bot: Bot): ExternalResource.ResourceHandle {
        // /open-apis/im/v1/messages/:message_id/resources/:file_key
        bot as LarksuiteBot
        val httpd = bot.configuration.httpServerDaemon
        val url =
            httpd.serverPrefix + bot.larksuiteResourceExposeAdapter.uploaded + "/" + resourceType + "/" + fileKey

        return ConstExternalResource(url)
    }

    private class ConstExternalResource(
        override val url: String
    ) : ExternalResource.ResourceHandle {
        override fun close() {
        }
    }
}