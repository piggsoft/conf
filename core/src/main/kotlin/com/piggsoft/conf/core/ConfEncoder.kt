package com.piggsoft.conf.core

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import java.nio.ByteBuffer

/**
 *
 * @author yaochen4
 * @since 1.0
 * @version 1.0
 * @create 2017/9/20
 */
class ConfEncoder: MessageToByteEncoder<Message<Any?>>() {

    override fun encode(ctx: ChannelHandlerContext, msg: Message<Any?>, out: ByteBuf) {
        val headerBuffer = ByteBuffer.allocate(MessageUtils.headerLength)
        headerBuffer.put(msg.encode)
                .put(msg.encrypt)
                .putInt(msg.sessionId)
                .putInt(msg.command)
        var bytes = msg.body?.serialize()
        headerBuffer.putInt(bytes?.size ?: 0)

        headerBuffer.flip()
        out.writeBytes(headerBuffer)
        bytes?.let { out.writeBytes(it) }
    }

}