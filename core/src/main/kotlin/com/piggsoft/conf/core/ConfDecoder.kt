package com.piggsoft.conf.core

import com.piggsoft.conf.core.message.FileChange
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
        //消息长度必须大于header长度
        if (buf.readableBytes() >= MessageUtils.headerLength) {
            buf.markReaderIndex()
            val encode = buf.readByte()
            val encrypt = buf.readByte()
            val sessionId = buf.readInt()
            val command = buf.readInt()
            val bodyLength = buf.readInt()
            var bytes: ByteArray? = null
            if (bodyLength > 0) {
                buf.resetReaderIndex()
                if (buf.readableBytes() >= bodyLength) {
                    bytes = ByteArray(bodyLength)
                    buf.readBytes(bodyLength).readBytes(bytes)
                }
            }
            out += Message<Any>().apply {
                this.encode = encode
                this.encrypt = encrypt
                this.sessionId = sessionId
                this.command = command
                this.bodyLength = bodyLength
                this.body = createBody(command, bytes)
            }
        }

    }

    private inline fun createBody(command: Int, bodyBytes: ByteArray?) : Any? {
        return when(command) {
            CommandType.PING.command -> null
            CommandType.FILECHANGE.command -> deserialize<FileChange>(bodyBytes)
            else -> null
        }
    }

}