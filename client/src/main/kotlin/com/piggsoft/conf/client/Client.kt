package com.piggsoft.conf.client

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoop
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleStateHandler
import io.netty.util.CharsetUtil
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 *
 * @author piggsoft@163.com
 * @since 1.0
 * @version 1.0
 * @create 2017/9/16
 * Description:
 */
class Client(private val serverHost: String, private val serverPort: Int) {

    val b = Bootstrap()

    var times = 0

    init {
        b.group(NioEventLoopGroup())
                .channel(NioSocketChannel::class.java)
                .handler(object : ChannelInitializer<Channel>() {
                    override fun initChannel(ch: Channel?) {
                        ch?.pipeline()
                                ?.addLast(IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                                ?.addLast(IdleStateTrigger())
                                ?.addLast(StringDecoder())
                                ?.addLast(StringEncoder())
                                ?.addLast(ClientHander())
                                ?.addLast(object: ChannelInboundHandlerAdapter() {
                                    override fun channelUnregistered(ctx: ChannelHandlerContext?) {
                                        retry(ctx?.channel()?.eventLoop())
                                    }
                                })
                    }
                })
    }

    private fun nextRetryDelay(): Long {
        return (2 shl times).toLong()
    }

    fun retry(eventLoop: EventLoop?) {
        times++
        if (times < 13) {
            eventLoop?.schedule({
                doConnect()
            }, nextRetryDelay(), TimeUnit.MILLISECONDS)
        }
    }

    fun doConnect(): ChannelFuture? {
        return b.connect(serverHost, serverPort)?.sync()
                ?.addListener {
                    val f = it as ChannelFuture
                    if (!it.isSuccess) {
                        retry(f.channel()?.eventLoop())
                    }
                }
    }

}

fun main(args: Array<String>) {
    Client("localhost", 8080).doConnect()

}

@ChannelHandler.Sharable
class IdleStateTrigger : ChannelDuplexHandler() {

    private val HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
            CharsetUtil.UTF_8))

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        when (evt) {
            is IdleStateEvent -> {
                if (IdleState.WRITER_IDLE == evt.state())
                    ctx?.channel()?.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                            ?.addListener(ChannelFutureListener.CLOSE_ON_FAILURE)
            }
            else -> super.userEventTriggered(ctx, evt)
        }
    }
}

class ClientHander : ChannelInboundHandlerAdapter() {
    override fun channelInactive(ctx: ChannelHandlerContext?) {
        println("激活时间是：${Date()}")
        println("HeartBeatClientHandler channelInactive")
        super.channelInactive(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        println("server channelRead.. \n ${ctx?.channel()?.remoteAddress()} -> Server : ${msg?.toString()}")
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        println("停止时间是：${Date()}")
        println("HeartBeatClientHandler channelActive")
        super.channelActive(ctx)
    }
}
