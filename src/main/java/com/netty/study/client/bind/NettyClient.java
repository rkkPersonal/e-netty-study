package com.netty.study.client.bind;

import com.netty.study.server.NettyChannelInitializer;
import com.netty.study.ssl.SslContextTool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

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
        this(port, "localhost");
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
        this.domain = "localhost";
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
                    .handler(new NettyChannelInitializer(sslContext, true, this.isWebsocket, true));       // 连接到达时会创建一个通道
            // (6)
            // Bind and start to accept incoming connections.
            ChannelFuture f = bootstrap.connect(new InetSocketAddress(this.domain, this.port)).sync(); // (7)
            log.info("client bind port :{}", port);
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.

            f.channel().writeAndFlush("Hi Client 开始发送数据.....");
            f.channel().closeFuture().addListener(future -> {
                if (future.isSuccess()) {
                    log.info("端口绑定成功:{}", port);
                } else {
                    future.cause().printStackTrace();
                }
            }).sync();
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
