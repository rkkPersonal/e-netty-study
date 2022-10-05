package com.netty.study.server.websocket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author Steven
 * @date 2022年10月06日 1:15
 */
@ChannelHandler.Sharable
public class WebsocketTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // Handle text frame
    }

}
