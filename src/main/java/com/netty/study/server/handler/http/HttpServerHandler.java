package com.netty.study.server.handler.http;

import com.alibaba.fastjson.JSON;
import com.netty.study.bean.ResultDTO;
import com.netty.study.bean.User;
import com.netty.study.exception.HandlerExceptionResolver;
import com.netty.study.method.HttpMethodFactory;
import com.netty.study.serializer.JSONSerializer;
import com.netty.study.util.HttpWrapper;
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
            String uri = request.uri();
            HttpMethod method = request.method();
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
            log.info("服务端收到请求地址  :{}", request.uri());
            if (method.equals(HttpMethod.GET)) {
                User user = HttpWrapper.queryParameter(uri, User.class);
                log.info("请求参数:{}", JSON.toJSONString(user));
                com.netty.study.method.HttpMethod getMethod = HttpMethodFactory.getMethod(HttpMethodFactory.GET);
                getMethod.response(ctx,null);
            }
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
            ResultDTO<User> resultDTO = ResultDTO.success(user);
            JSONSerializer jsonSerializer = new JSONSerializer();
            byte[] responseBody = jsonSerializer.serialize(resultDTO);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseBody));
            HttpHeaders responseHeaders = response.headers();
            responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            if (HttpUtil.isKeepAlive(request)) {
                responseHeaders.set(CONNECTION, HttpHeaderValues.KEEP_ALIVE.toString());
            }
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            log.info("响应成功返回.......");

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
            ResultDTO<String> resultDTO = ResultDTO.failure(500, "系统内部异常", "如果持续发生，请联系客服进行反馈.");
            JSONSerializer jsonSerializer = new JSONSerializer();
            byte[] responseBody = jsonSerializer.serialize(resultDTO);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer(responseBody));
            HttpHeaders responseHeaders = response.headers();
            responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            handlerExceptionResolver.response(ctx, response);
        }
        ctx.close();
    }
}