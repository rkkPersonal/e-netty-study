package com.netty.study.exception;

import com.netty.study.bean.ResultDTO;
import com.netty.study.method.HttpMethod;
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
 * @date 2022年10月08日 15:26
 */
@Slf4j
public class HandlerExceptionResolver implements HttpMethod {
    @Override
    public void response(ChannelHandlerContext ctx, FullHttpResponse response) {

    }

    @Override
    public void request(ChannelHandlerContext ctx, FullHttpRequest request) {

    }

    @Override
    public void writer(ChannelHandlerContext ctx, Object obj) {
        this.writer(ctx, obj, null, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public void writer(ChannelHandlerContext ctx, Object obj, HttpHeaders headers, HttpResponseStatus httpStatus) {
        if (!Optional.ofNullable(obj).isPresent()) {
            obj = "如果持续发生，请联系客服进行反馈.";
        }
        ResultDTO resultDTO = ResultDTO.failure(500, "系统内部异常", obj);
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
    }

}
