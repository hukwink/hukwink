package com.hukwink.hukwink.chatting

import com.hukwink.hukwink.resource.LocalResource

public interface FileSupported {
    public suspend fun sendFile(resource: LocalResource): MessageReceipt
}
