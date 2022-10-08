package com.netty.study.server.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * @author Steven
 * @date 2022年10月08日 21:46
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> { //1 private final String wsUri;
    private static final File INDEX;
    private String wsUri;

    static {

        try {
            InputStream resourceAsStream = HttpRequestHandler.class.getResourceAsStream("chat_client.html");
            INDEX = new File("chat_client.html");
            FileUtils.copyInputStreamToFile(resourceAsStream, INDEX);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to locate chat_client.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.uri())) {
            ctx.fireChannelRead(request.retain());                  //2
        } else {
            if (HttpUtil.is100ContinueExpected(request)) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
                return;
            }
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");//4
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

            boolean keepAlive = HttpUtil.isKeepAlive(request);

            if (keepAlive) {                                        //5
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);                    //6

            if (ctx.pipeline().get(SslHandler.class) == null) {     //7
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);           //8
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);        //9
            }

            file.close();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "异常");
        cause.printStackTrace();
        ctx.close();
    }
}



