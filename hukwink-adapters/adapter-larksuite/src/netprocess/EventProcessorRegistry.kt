package com.hukwink.hukwink.adapter.larksuite.netprocess

import com.hukwink.hukwink.adapter.larksuite.netprocess.evt.EvtMsgReceive

public class EventProcessorRegistry {
    public var defaultProcessor: EventProcessor? = null
    public val processors: MutableMap<String, EventProcessor> = mutableMapOf()

    init {
        processors["im.message.receive_v1"] = EvtMsgReceive()
    }

    public fun getProcessor(eventType: String): EventProcessor? {
        return processors[eventType]
    }
}
