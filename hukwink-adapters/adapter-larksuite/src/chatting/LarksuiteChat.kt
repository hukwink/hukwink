package com.hukwink.hukwink.adapter.larksuite.chatting

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess.ensureOk
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess.parseToJsonAndVerify
import com.hukwink.hukwink.adapter.larksuite.http.larksuiteAuthorization
import com.hukwink.hukwink.adapter.larksuite.message.LarksuiteEmotion
import com.hukwink.hukwink.adapter.larksuite.message.LarksuiteMessageTitle
import com.hukwink.hukwink.adapter.larksuite.message.LarksuiteReplyInfo
import com.hukwink.hukwink.adapter.larksuite.message.file.LarksuiteFileUploaded
import com.hukwink.hukwink.adapter.larksuite.message.image.LarksuiteImageUploaded
import com.hukwink.hukwink.adapter.larksuite.proto.ProtoSendMessageReply
import com.hukwink.hukwink.chatting.ChatType
import com.hukwink.hukwink.chatting.Chatting
import com.hukwink.hukwink.chatting.MessageReceipt
import com.hukwink.hukwink.contact.ChatInfo
import com.hukwink.hukwink.message.*
import com.hukwink.hukwink.message.MessageUtil.toMessageChain
import io.vertx.core.http.HttpMethod
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.serialization.json.*

public class LarksuiteChat(
    override val bot: LarksuiteBot,
    override val chatType: ChatType,
    override val chatInfo: ChatInfo,
) : Chatting {

    private fun Set<PlainText.Style>.toJson(): List<JsonPrimitive> = asSequence().mapNotNull { style ->
        when (style) {
            PlainText.Style.BOLD -> "bold"
            PlainText.Style.ITALIC -> "italic"
            PlainText.Style.UNDERLINE -> "underline"
            PlainText.Style.LINE_THROUGH -> "lineThrough"
            else -> null
        }
    }.map { JsonPrimitive(it) }.toList()

    override suspend fun sendMessage(message: Message): MessageReceipt {
        val chain = message.toMessageChain()

        val replyTarget = chain[LarksuiteReplyInfo]
        val msgSent = mutableMapOf<String, JsonElement>()

        msgSent["msg_type"] = JsonPrimitive("post")
        kotlin.run msgEncode@{
            if (chain.content.size == 1) {
                val singleElm = chain.content[0]
                // TODO single element special
            }

            val lines = mutableListOf<JsonArray>()

            val currentLine = mutableListOf<JsonElement>()
            chain.content.forEach { elm ->
                if (elm is PlainText) {
                    if (elm.content == "\n") {
                        lines.add(JsonArray(currentLine.toList()))
                        currentLine.clear()
                        return@forEach
                    }
                    currentLine.add(buildJsonObject {
                        put("tag", "text")
                        put("text", elm.content.toString())
                        val styles = elm.styles.toJson()
                        if (styles.isNotEmpty()) {
                            put("style", JsonArray(styles))
                        }
                    })
                }
                if (elm is Hyperlink) {
                    currentLine.add(buildJsonObject {
                        put("tag", "a")
                        put("text", elm.content.toString())
                        put("href", elm.hyperlink.toString())
                        val styles = elm.styles.toJson()
                        if (styles.isNotEmpty()) {
                            put("style", JsonArray(styles))
                        }
                    })
                }
                if (elm is Mention) {
                    currentLine.add(buildJsonObject {
                        put("tag", "at")
                        put("user_id", elm.target)
                        put("user_name", "TODO: User Name Unknown")
                    })
                }

                if (elm is Image) {
                    if (elm is LarksuiteImageUploaded) {
                        currentLine.add(buildJsonObject {
                            put("tag", "img")
                            put("image_key", elm.imageId)
                        })
                    } else {
                        // TODO
                    }
                }

                if (elm is LarksuiteEmotion) {
                    currentLine.add(buildJsonObject {
                        put("tag", "img")
                        put("emoji_type", elm.emojiType)
                    })
                }
                if (elm is File) {
                    if (elm is LarksuiteFileUploaded) {
                        currentLine.add(buildJsonObject {
                            put("tag", "file")
                            put("file_key", elm.fileId)
                        })
                    }
                }
            }

            if (currentLine.isNotEmpty()) {
                lines.add(JsonArray(currentLine.toList()))
            }

            val finalContent = buildJsonObject {
                putJsonObject("en_us") {
                    put("title", chain[LarksuiteMessageTitle]?.title ?: "")
                    put("content", JsonArray(lines))
                }
            }

            msgSent["content"] = JsonPrimitive(Json.encodeToString(JsonObject.serializer(), finalContent))
        }

        if (bot.configuration.webhookDebugPrint) {
            bot.logger.info(
                "Msg send: {}",
                LarksuiteHttpResponseProcess.jsonPrettyPrint
                    .encodeToString(JsonObject.serializer(), JsonObject(msgSent))
            )
        }

        val httpRequest = if (replyTarget == null) {
            msgSent["receive_id"] = JsonPrimitive(chatInfo.chatId.chatId)
            bot.httpClient.request(
                HttpMethod.POST,
                "/open-apis/im/v1/messages?receive_id_type=" + chatInfo.chatId.chatIdType
            )
        } else {
            bot.httpClient.request(HttpMethod.POST, "/open-apis/im/v1/messages/${replyTarget.parentId}/reply")
        }.coAwait().larksuiteAuthorization(bot)
        val reply = httpRequest.send(Json.encodeToString(JsonObject.serializer(), JsonObject(msgSent))).coAwait()
        val replyContent = reply.ensureOk().body().coAwait().parseToJsonAndVerify()

        if (bot.configuration.webhookDebugPrint) {
            bot.logger.info(
                "Msg send reply: {}",
                LarksuiteHttpResponseProcess.jsonPrettyPrint
                    .encodeToString(JsonObject.serializer(), replyContent)
            )
        }
        val replyObj = LarksuiteHttpResponseProcess.jsonHttpProcess.decodeFromJsonElement(
            ProtoSendMessageReply.serializer(),
            replyContent
        )

        return LarksuiteMessageReceipt(
            messageId = replyObj.data.message_id,
            sentTime = replyObj.data.create_time.toLongOrNull() ?: System.currentTimeMillis(),
        )
    }

    override suspend fun uploadImage(resource: LocalResource): Image {
        return bot.uploadImage(resource)
    }
}
