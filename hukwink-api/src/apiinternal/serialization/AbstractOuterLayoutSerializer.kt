package com.hukwink.hukwink.apiinternal.serialization

import com.hukwink.hukwink.message.Message
import com.hukwink.hukwink.message.serialization.MessageOuterLayoutSerializer
import com.hukwink.hukwink.util.HukwinkInternalApi
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.annotations.ApiStatus


@ApiStatus.Internal
@HukwinkInternalApi
public abstract class AbstractOuterLayoutSerializer<T : AbstractOuterLayoutSerializer.AbstractDelegate, MsgT : Message> :
    MessageOuterLayoutSerializer<MsgT> {

    public abstract class AbstractDelegate {
        public abstract val buf: ByteArray
    }

    public abstract val serializer: KSerializer<T>
    public abstract fun newDelegate(value: ByteArray, message: MsgT): T

    override val descriptor: SerialDescriptor get() = serializer.descriptor

    override fun deserialize(decoder: Decoder): ByteBuf {
        return Unpooled.wrappedBuffer(serializer.deserialize(decoder).buf)
    }

    override fun serialize(encoder: Encoder, byteBuf: ByteBuf, message: MsgT) {
        val tmpArray = ByteArray(byteBuf.readableBytes()).also { buf ->
            byteBuf.getBytes(byteBuf.readerIndex(), buf)
        }
        serializer.serialize(encoder, newDelegate(tmpArray, message))
    }
}