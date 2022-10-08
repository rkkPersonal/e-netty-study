package com.netty.study.method;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Steven
 * @date 2022年10月08日 1:56
 */
public class GetMethod implements HttpMethod{
    @Override
    public void response(ChannelHandlerContext ctx, FullHttpResponse response) {

    }

    @Override
    public void request(ChannelHandlerContext ctx, FullHttpRequest request) {

    }
}
