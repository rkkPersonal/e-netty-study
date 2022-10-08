package com.netty.study.server.handler.http;

import com.alibaba.fastjson.JSON;
import com.netty.study.bean.User;
import com.netty.study.exception.HandlerExceptionResolver;
import com.netty.study.method.HttpMethodFactory;
import com.netty.study.util.HttpParameterWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

/**
 * @author Steven
 * @date 2022年10月07日 1:14
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private static final String FAVICON_ICO = "/favicon.ico";

    private HandlerExceptionResolver handlerExceptionResolver = new HandlerExceptionResolver();
    private boolean isHttp = true;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("server is active :{}", ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest request = null;
        if (msg instanceof FullHttpRequest) {
            request = (FullHttpRequest) msg;
            if (is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
            String uri = request.uri();
            HttpMethod method = request.method();
            log.info("服务端收到请求地址  :{}", request.uri());
            if (method.equals(HttpMethod.GET)) {
                User requestBody = HttpParameterWrapper.queryParameter(uri, User.class);
                log.info("请求参数:{}", JSON.toJSONString(requestBody));
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            log.info("请求内容:{}", buf.toString(CharsetUtil.UTF_8));
            buf.release();
            HttpMethod method = request.method();
            if (method.equals(HttpMethod.GET)) {
                User user = User.builder().username("steven").data(LocalDateTime.now().toString()).hobby("Play Basketball").build();
                com.netty.study.method.HttpMethod httpGetMethod = HttpMethodFactory.getMethod(HttpMethodFactory.GET);
                httpGetMethod.writer(ctx, user);
            }


        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端异常:{}", cause.getMessage());
        if (isHttp) {
            handlerExceptionResolver.writer(ctx, "如果持续发生，请联系客服进行反馈");
        }
        ctx.close();
    }
}