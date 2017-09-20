package com.piggsoft.conf.server.netty

import com.piggsoft.conf.core.ConfDecoder
import com.piggsoft.conf.core.ConfEncoder
import com.piggsoft.conf.core.Message
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleStateHandler
import java.util.concurrent.TimeUnit

/**
 *
 * @author piggsoft@163.com
 * @since 1.0
 * @version 1.0
 * @create 2017/9/16
 * Description:
 */
class Server {

    fun startServer(port: Int) {
        val bossGroup = NioEventLoopGroup()
        val workerGroup = NioEventLoopGroup()
        ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(PipelineInitializer())
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind(port)
                .sync()
                .channel()
                ?.closeFuture()
                ?.sync()
    }

}

fun main(args: Array<String>) {
    Server().startServer(8080)
}

class PipelineInitializer : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel?) {
        ch?.pipeline()
                ?.addLast(IdleStateHandler(1, 0, 0, TimeUnit.MINUTES))
                ?.addLast(IdleStateTrigger())
                ?.addLast("decoder", ConfDecoder())
                ?.addLast("encoder", ConfEncoder())
                ?.addLast(ServerHander())
    }

}

@ChannelHandler.Sharable
class IdleStateTrigger : ChannelInboundHandlerAdapter() {

    var loss_connect_time = 0

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        when (evt) {
            is IdleStateEvent -> {
                if (IdleState.READER_IDLE == evt.state()) {
                    loss_connect_time++
                    if (loss_connect_time > 4) {
                        ctx?.channel()?.close()
                    }
                }
            }
            else -> super.userEventTriggered(ctx, evt)
        }
    }
}

class ServerHander : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        println("${ctx?.channel()?.remoteAddress()} -> Server : ${msg?.let { it as Message<Any> ; println(it.command)}}")    }
}