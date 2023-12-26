package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.File
import com.hukwink.hukwink.util.ByteArrayHexStringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal object FileOuterLayout :
    AbstractOuterLayoutSerializer<FileOuterLayout.Delegate, File>() {
    @Serializable
    @SerialName("file")
    class Delegate(
        @Serializable(ByteArrayHexStringSerializer::class)
        @SerialName("internal")
        override val buf: ByteArray,
        val fileName: String,
    ) : AbstractDelegate()

    override val serializer: KSerializer<Delegate>
        get() = Delegate.serializer()

    override fun newDelegate(value: ByteArray, message: File): Delegate {
        return Delegate(value, message.fileName)
    }
}