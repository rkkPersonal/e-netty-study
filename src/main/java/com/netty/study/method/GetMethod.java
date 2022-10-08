package com.netty.study.method;

import com.netty.study.bean.ResultDTO;
import com.netty.study.serializer.JSONSerializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author Steven
 * @date 2022年10月08日 1:56
 */
@Slf4j
public class GetMethod implements HttpMethod {
    @Override
    public void response(ChannelHandlerContext ctx, FullHttpResponse response) {

    }

    @Override
    public void request(ChannelHandlerContext ctx, FullHttpRequest request) {

    }

    public void writer(ChannelHandlerContext ctx, Object obj, HttpHeaders headers, HttpResponseStatus httpStatus) {
        ResultDTO resultDTO = ResultDTO.success(obj);
        JSONSerializer jsonSerializer = new JSONSerializer();
        byte[] responseBody = jsonSerializer.serialize(resultDTO);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, httpStatus, Unpooled.wrappedBuffer(responseBody));
        HttpHeaders responseHeaders = response.headers();
        if (Optional.ofNullable(headers).isPresent()) {
            responseHeaders.setAll(headers);
        } else {
            responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        log.info("响应成功返回.......");
    }

    public void writer(ChannelHandlerContext ctx, Object obj) {
        this.writer(ctx, obj, null, HttpResponseStatus.OK);
    }


}
