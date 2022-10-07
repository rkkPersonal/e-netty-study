package com.netty.study.method;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author Steven
 * @date 2022年10月08日 1:55
 */

public interface HttpMethod {

    void response(ChannelHandlerContext ctx, FullHttpResponse response);

    void request(ChannelHandlerContext ctx, FullHttpRequest request);

}
