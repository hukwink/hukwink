package com.hukwink.hukwink

import com.hukwink.hukwink.config.BotConfiguration
import com.hukwink.hukwink.message.serialization.HukwinkMessageSerialization

public interface BotFactory {
    public fun registerMessageSerializers(serialization: HukwinkMessageSerialization)
    public fun createBot(configuration: BotConfiguration): Bot
}
