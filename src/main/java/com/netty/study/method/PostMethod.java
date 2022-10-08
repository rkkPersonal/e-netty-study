package com.netty.study.method;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Steven
 * @date 2022年10月08日 1:56
 */
@Slf4j
public class PostMethod implements HttpMethod{

    @Override
    public void response(ChannelHandlerContext ctx, FullHttpResponse response) {

    }

    @Override
    public void request(ChannelHandlerContext ctx, FullHttpRequest request) {

    }

    @Override
    public void writer(ChannelHandlerContext ctx, Object obj) {

    }

    @Override
    public void writer(ChannelHandlerContext ctx, Object obj, HttpHeaders headers, HttpResponseStatus httpStatus) {

    }
}
