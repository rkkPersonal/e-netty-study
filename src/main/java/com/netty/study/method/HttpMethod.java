package com.netty.study.method;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Steven
 * @date 2022年10月08日 1:55
 */

public interface HttpMethod {

    void response(ChannelHandlerContext ctx, FullHttpResponse response);

    void request(ChannelHandlerContext ctx, FullHttpRequest request);

    public void writer(ChannelHandlerContext ctx, Object obj);

    public void writer(ChannelHandlerContext ctx, Object obj, HttpHeaders headers, HttpResponseStatus httpStatus);

}
