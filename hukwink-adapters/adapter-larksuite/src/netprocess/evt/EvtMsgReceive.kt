package com.hukwink.hukwink.adapter.larksuite.netprocess.evt

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.adapter.larksuite.chatting.LarksuiteChat
import com.hukwink.hukwink.adapter.larksuite.event.LarksuiteIncomingMessageEvent
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess
import com.hukwink.hukwink.adapter.larksuite.message.*
import com.hukwink.hukwink.adapter.larksuite.message.file.LarksuiteFileFromChat
import com.hukwink.hukwink.adapter.larksuite.message.image.LarksuiteImageFromChat
import com.hukwink.hukwink.adapter.larksuite.netprocess.v2.MsgV2Header
import com.hukwink.hukwink.adapter.larksuite.netprocess.v2.MsgV2Processor
import com.hukwink.hukwink.adapter.larksuite.proto.ProtoEventReceiveMessage
import com.hukwink.hukwink.chatting.ChatId
import com.hukwink.hukwink.chatting.ChatType
import com.hukwink.hukwink.contact.ChatInfo
import com.hukwink.hukwink.contact.UserInfo
import com.hukwink.hukwink.message.*
import com.hukwink.hukwink.message.MessageUtil.toMessageChain
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

internal class EvtMsgReceive : MsgV2Processor<ProtoEventReceiveMessage>() {
    override val eventSerializer: KSerializer<ProtoEventReceiveMessage> get() = ProtoEventReceiveMessage.serializer()


    override fun process0(evt: JsonObject, bot: LarksuiteBot, header: MsgV2Header, event: ProtoEventReceiveMessage) {
        val chatId = ChatId(
            chatId = event.message.chat_id,
            chatIdType = "chat_id",
        )
        val chatting = LarksuiteChat(
            bot = bot,
            chatType = when (event.message.chat_type) {
                "p2p" -> ChatType.PRIVATE_CHAT
                else -> error("Unknown chat type " + event.message.chat_type)
            },
            chatInfo = resolveChatInfo(chatId),
        )

        val sender = event.sender.resolveUserInfo(bot)

        bot.eventEngine.fire(
            LarksuiteIncomingMessageEvent(
                bot = bot,
                messages = event.message.parse(bot),
                chatting = chatting,
                sender = sender
            )
        )
    }

    private fun resolveChatInfo(chatId: ChatId): ChatInfo {
        return object : ChatInfo {
            override val chatId: ChatId get() = chatId

            override val chatName: String get() = "TODO"
        }
    }

    private fun ProtoEventReceiveMessage.Sender.resolveUserInfo(bot: LarksuiteBot): UserInfo {
        return object : UserInfo {
            override val userId: String get() = this@resolveUserInfo.sender_id.resolveId(bot = bot)

            override val username: String
                get() = TODO("Not yet implemented")

            override val chatId: ChatId
                get() = TODO("Not yet implemented")

        }
    }
}

@Suppress("RemoveExplicitTypeArguments")
private fun ProtoEventReceiveMessage.Message.parse(bot: LarksuiteBot): MessageChain {
    val extendElms = mutableListOf<Message>()

    val jsonElm = LarksuiteHttpResponseProcess.jsonHttpProcess.parseToJsonElement(content).jsonObject
    val content: Sequence<Message> = when (message_type) {
        "hongbao", "text" -> {
            sequenceOf(
                InternalPlainText(
                    jsonElm["text"]?.jsonPrimitive?.content ?: error("Failed to get text from $content")
                )
            )
        }
        "post" -> sequence<Message> {
            jsonElm["title"]?.jsonPrimitive?.contentOrNull?.let { yield(LarksuiteMessageTitle(it)) }

            // [[{\"tag\":\"text\",\"text\":\"omoAAAsad\",\"style\":[]}],[{\"tag\":\"text\",\"text\":\"aweouiow\",\"style\":[]}]]

            jsonElm["content"]?.jsonArray?.forEach { contentLine ->
                contentLine.jsonArray.forEach { contentValue ->
                    val subElm = contentValue.jsonObject
                    when (subElm["tag"]?.jsonPrimitive?.content) {
                        "text" -> {
                            yield(
                                PlainText(
                                    content = subElm["text"]?.jsonPrimitive?.content.orEmpty(),
                                    styles = subElm["style"]?.jsonArray?.parseTextStyle().orEmpty(),
                                )
                            )
                        }
                        "a" -> {
                            yield(
                                Hyperlink(
                                    content = subElm["text"]?.jsonPrimitive?.content.orEmpty(),
                                    hyperlink = subElm["href"]?.jsonPrimitive?.content.orEmpty(),
                                    styles = subElm["style"]?.jsonArray?.parseTextStyle().orEmpty(),
                                )
                            )
                        }
                        "at" -> yield(InternalAt(subElm["user_id"]?.jsonPrimitive?.content.orEmpty()))
                        "img" -> yield(
                            LarksuiteImageFromChat(
                                imageId = subElm["image_key"]?.jsonPrimitive?.content.orEmpty(),
                                messageId = message_id,
                            )
                        )
                        "media" -> yield(
                            LarksuiteMedia(
                                imageKey = subElm["image_key"]?.jsonPrimitive?.content.orEmpty(),
                                fileKey = subElm["file_key"]?.jsonPrimitive?.content.orEmpty(),
                                fileName = "",
                                duration = 0L,
                                messageId = message_id,
                            )
                        )
                        "emotion" -> yield(LarksuiteEmotion(subElm["emoji_type"]?.jsonPrimitive?.content.orEmpty()))
                    }
                }
                yield(InternalLineSpliterator)
            }
        }
        "image" -> sequenceOf(
            LarksuiteImageFromChat(
                imageId = jsonElm["image_key"]?.jsonPrimitive?.content.orEmpty(),
                messageId = message_id,
            )
        )
        "file", "folder" -> sequenceOf(
            LarksuiteFileFromChat(
                fileKey = jsonElm["file_key"]?.jsonPrimitive?.content.orEmpty(),
                fileName = jsonElm["file_name"]?.jsonPrimitive?.content.orEmpty(),
                isFolder = message_type == "folder",
                messageId = message_id,
            )
        )
        "audio" -> sequenceOf(
            LarksuiteAudio(
                fileKey = jsonElm["file_key"]?.jsonPrimitive?.content.orEmpty(),
                duration = jsonElm["duration"]?.jsonPrimitive?.long ?: 0L,
                messageId = message_id,
            )
        )
        "media" -> sequenceOf(
            LarksuiteMedia(
                imageKey = jsonElm["image_key"]?.jsonPrimitive?.content.orEmpty(),
                fileKey = jsonElm["file_key"]?.jsonPrimitive?.content.orEmpty(),
                fileName = jsonElm["file_name"]?.jsonPrimitive?.content.orEmpty(),
                duration = jsonElm["duration"]?.jsonPrimitive?.long ?: 0L,
                messageId = message_id,
            )
        )
        "sticker" -> sequenceOf(
            LarksuiteSticker(
                fileKey = jsonElm["file_key"]?.jsonPrimitive?.content.orEmpty(),
            )
        )
        "interactive" -> sequenceOf() // TODO
        "share_calendar_event" -> sequenceOf() // TODO
        "calendar" -> sequenceOf() // TODO
        "general_calendar" -> sequenceOf() // TODO
        "share_chat" -> sequenceOf() // TODO
        "share_user" -> sequenceOf() // TODO
        "system" -> sequenceOf() // TODO
        "location" -> sequenceOf() // TODO
        "video_chat" -> sequenceOf() // TODO
        "todo" -> sequenceOf() // TODO
        "vote" -> sequenceOf() // TODO
        else -> {
            error("Unknown how to parse $content with $message_type")
        }
    }

    if (parent_id.isNotEmpty()) {
        extendElms.add(LarksuiteReplyInfo(parentId = parent_id, rootId = root_id))
    }
    extendElms.add(
        LarksuiteMessageSource(
            messageId = message_id,
            rootId = root_id,
            parentId = parent_id,
        )
    )


    return (extendElms.asSequence() + content).flatMap { elm ->
        if (elm is InternalPlainText) {
            val rawContent = elm.content
            val matcher = AT_USER_X.matcher(rawContent)
            val resp = mutableListOf<Message>()
            var unhit = 0
            while (matcher.find()) {
                resp.add(PlainText(content = rawContent.substring(unhit, matcher.start())))
                val target = rawContent.substring(matcher.start(), matcher.end())
                unhit = matcher.end()

                val ment = mentions.find { it.key == target }
                if (ment == null) {
                    resp.add(PlainText(content = target))
                } else {
                    resp.add(Mention(target = ment.id.resolveId(bot)))
                }
            }
            resp.add(PlainText(content = rawContent.substring(unhit)))
            return@flatMap resp.asSequence()
        }
        if (elm is InternalAt) {
            val atTarget = mentions.find { it.key == elm.user_id }
            if (atTarget == null) {
                return@flatMap sequenceOf(PlainText("@" + elm.user_id))
            } else {
                return@flatMap sequenceOf(Mention(target = atTarget.id.resolveId(bot)))
            }
        }
        return@flatMap sequenceOf(elm)
    }.filter { elm ->
        val elmType = elm.javaClass
        if (elmType == PlainText::class.java) {
            return@filter (elm as PlainText).content.isNotEmpty()
        }
        return@filter true
    }.toMessageChain()
}

private val AT_USER_X = """@_user_\d+""".toRegex().toPattern()

private fun JsonArray.parseTextStyle(): Set<PlainText.Style> {
    return asSequence().map { e ->
        e.jsonPrimitive.content
    }.mapNotNull { value ->
        when (value) {
            "bold" -> PlainText.Style.BOLD
            "underline" -> PlainText.Style.UNDERLINE
            "lineThrough" -> PlainText.Style.LINE_THROUGH
            "italic" -> PlainText.Style.ITALIC
            else -> null
        }
    }.toSet()
}

