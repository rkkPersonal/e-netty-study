package com.netty.study.exception;

import com.netty.study.method.HttpMethod;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Steven
 * @date 2022年10月08日 15:26
 */
public class HandlerExceptionResolver implements HttpMethod {
    @Override
    public void response(ChannelHandlerContext ctx, FullHttpResponse response) {

    }

    @Override
    public void request(ChannelHandlerContext ctx, FullHttpRequest request) {

    }
}
