package com.piggsoft.conf.core

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 *
 * @author yaochen4
 * @since 1.0
 * @version 1.0
 * @create 2017/9/19
 */
class ConfDecoder : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //消息长度必须大于header长度
        if (buf.readableBytes() >= MessageUtils.headerLength) {
            buf.markReaderIndex()
            val encode = buf.readByte()
            val encrypt = buf.readByte()
            val sessionId = buf.readInt()
            val command = buf.readInt()
            val bodyLength = buf.readInt()
            var bytes: ByteArray
            if (bodyLength > 0) {
                buf.resetReaderIndex()
                if (buf.readableBytes() >= bodyLength) {
                    buf.readBytes(bodyLength).readBytes(bytes)
                }
            }
            out += Message(encode, encrypt, sessionId, command, bodyLength, bytes)
        }

    }

}