package com.netty.study.server.handler.http;

import com.netty.study.bean.User;
import com.netty.study.serializer.JSONSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author Steven
 * @date 2022年10月07日 1:14
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("server is active :{}", ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest request = null;
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
            String uri = request.uri();
            log.info("服务端收到请求地址  :{}", uri);
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            log.info("请求内容:{}", buf.toString(CharsetUtil.UTF_8));
            buf.release();

            User user = new User();
            user.setUsername("steven");
            user.setData(LocalDateTime.now().toString());
            user.setHobby("Play Basketball");
            JSONSerializer jsonSerializer = new JSONSerializer();

            //将Java对象序列化成为二级制数据包
            byte[] responseBody = jsonSerializer.serialize(user);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBody));
            HttpHeaders responseHeaders = response.headers();
            responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            if (HttpUtil.isKeepAlive(request)) {
                responseHeaders.set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
            }
            ctx.writeAndFlush(response);
            log.info("响应成功返回.......");

        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端异常 ", cause.getMessage());
        ctx.close();
    }
}