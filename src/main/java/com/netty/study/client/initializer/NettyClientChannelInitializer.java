package com.netty.study.client.initializer;

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
public class NettyClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext context;
    private boolean isWebsocket;
    private WebsocketTextFrameHandler websocketTextFrameHandler;
    private HttpClientHandler clientHandler;

    public NettyClientChannelInitializer(SslContext context, boolean isWebsocket) {   //1
        this.context = context;
        this.isWebsocket = isWebsocket;
        if (isWebsocket) {
            websocketTextFrameHandler = new WebsocketTextFrameHandler();
        } else {
            clientHandler = new HttpClientHandler();
        }
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (context != null) {
            SSLEngine engine = context.newEngine(ch.alloc());  //2

            engine.setUseClientMode(true); //3
            SslHandler sslHandler = new SslHandler(engine, true);
            sslHandler.setHandshakeTimeout(30, TimeUnit.SECONDS);
            sslHandler.setCloseNotifyFlushTimeout(30, TimeUnit.SECONDS);
            /*pipeline.addFirst("ssl", sslHandler);*/
        }

        pipeline.addLast("decoder", new HttpResponseDecoder());  //1
        pipeline.addLast("encoder", new HttpRequestEncoder());  //2
        pipeline.addLast("codec", new HttpClientCodec());  //1
        pipeline.addLast("decompressor", new HttpContentDecompressor()); //
        if (this.isWebsocket) {
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024));
            pipeline.addLast(new WebSocketServerProtocolHandler("/websocket", "websocket", false));
            pipeline.addLast(websocketTextFrameHandler);
        } else {
            pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024));
            pipeline.addLast(clientHandler);
        }


    }
}
