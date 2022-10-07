package com.netty.study.server.initializer;

import com.netty.study.client.handler.http.HttpClientHandler;
import com.netty.study.server.handler.http.HttpServerHandler;
import com.netty.study.server.websocket.WebsocketTextFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @author Steven
 * @date 2022年10月06日 0:06
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext context;
    private boolean isWebsocket;
    private WebsocketTextFrameHandler websocketTextFrameHandler;
    private HttpServerHandler serviceHandler;

    public NettyChannelInitializer(SslContext context, boolean isWebsocket) {   //1
        this.context = context;
        this.isWebsocket = isWebsocket;
        if (isWebsocket) {
            websocketTextFrameHandler = new WebsocketTextFrameHandler();
        } else {
            serviceHandler = new HttpServerHandler();
        }
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (context != null) {
            SSLEngine engine = context.newEngine(ch.alloc());  //2
            engine.setUseClientMode(false);
            SslHandler sslHandler = new SslHandler(engine, true);
            sslHandler.setHandshakeTimeout(30, TimeUnit.SECONDS);
            sslHandler.setCloseNotifyFlushTimeout(30, TimeUnit.SECONDS);
            pipeline.addFirst("ssl", sslHandler);
        }
        pipeline.addLast("decoder", new HttpRequestDecoder());  //3
        pipeline.addLast("encoder", new HttpResponseEncoder());  //4
        pipeline.addLast("codec", new HttpServerCodec());  //2
        pipeline.addLast("compressor", new HttpContentCompressor()); //4
        pipeline.addLast("httpServerException", new HttpServerExpectContinueHandler());
        if (this.isWebsocket) {
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024));
            pipeline.addLast(new WebSocketServerProtocolHandler("/websocket", "websocket", false));
            pipeline.addLast(websocketTextFrameHandler);
        } else {
            pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024));
            pipeline.addLast(serviceHandler);
        }
    }
}
