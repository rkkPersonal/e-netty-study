package com.netty.study.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author Steven
 * @date 2022年10月06日 0:18
 */
@ChannelHandler.Sharable
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("client receive server message :{}", ((ByteBuf) msg).toString(Charset.defaultCharset()));
        Channel channel = ctx.channel();  //1
        channel.write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
