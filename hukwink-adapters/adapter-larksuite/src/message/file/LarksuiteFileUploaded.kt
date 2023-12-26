package com.hukwink.hukwink.adapter.larksuite.message.file

import com.hukwink.hukwink.adapter.larksuite.resource.UploadedResource
import com.hukwink.hukwink.message.File

internal class LarksuiteFileUploaded(
    val fileId: String,
    override val fileName: String,
) : File, UploadedResource() {
    override val fileKey: String get() = fileId
    override val resourceType: String get() = "uploadedFile"

    override fun contentToString(): String = "[file:$fileName]"

    override fun toString(): String = contentToString()
}