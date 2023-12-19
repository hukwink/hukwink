package com.hukwink.hukwink.message

import com.hukwink.hukwink.resource.ExternalResource

public interface File : MessageElement, ExternalResource {
    public val fileName: String
}
