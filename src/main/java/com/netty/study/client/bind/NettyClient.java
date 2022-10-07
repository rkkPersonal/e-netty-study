package com.netty.study.client.bind;

import com.netty.study.client.initializer.NettyClientChannelInitializer;
import com.netty.study.ssl.SslContextTool;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * @author Steven
 * @date 2022年10月06日 1:41
 */
@Slf4j
public class NettyClient  extends SslContextTool {

    private int port;
    private NioEventLoopGroup bootGroup;
    private boolean isWebsocket;
    private String domain;

    public NettyClient(int port) {
        this(port, "127.0.0.1");
    }

    public NettyClient(int port, String domain) {
        this.port = port;
        this.domain = domain;
        this.isWebsocket = false;
        init();
    }

    public NettyClient(int port, boolean isWebsocket) {
        this.port = port;
        this.isWebsocket = isWebsocket;
        this.domain = "127.0.0.1";
        init();
    }

    public void init() {
        bootGroup = new NioEventLoopGroup(10);
    }

    private void start() {
        try {
            final SslContext sslContext = sslContext();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bootGroup)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .handler(new NettyClientChannelInitializer(sslContext,  this.isWebsocket));       // 连接到达时会创建一个通道
            // (6)
            // Bind and start to accept incoming connections.
            ChannelFuture f = bootstrap.connect(new InetSocketAddress(this.domain, this.port)).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            close();
        }
    }



    private void close() {
        if (bootGroup != null) {
            bootGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        new NettyClient(9000).start();
    }


}
