package com.hukwink.hukwink

import com.hukwink.hukwink.config.BotConfiguration

public interface BotFactory {
    public fun createBot(configuration: BotConfiguration): Bot
}
