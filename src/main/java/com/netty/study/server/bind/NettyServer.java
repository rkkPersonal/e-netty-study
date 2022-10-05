package com.netty.study.server.bind;

import com.netty.study.server.NettyChannelInitializer;
import com.netty.study.ssl.SslContextTool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Steven
 * @date 2022年10月05日 23:59
 */
@Slf4j
public class NettyServer extends SslContextTool {

    private int port;

    private NioEventLoopGroup bootGroup;
    private NioEventLoopGroup workGroup;
    private boolean isWebsocket;

    public NettyServer(int port) {
        this.port = port;
        this.isWebsocket = false;
        init();
    }

    public NettyServer(int port, boolean isWebsocket) {
        this.port = port;
        this.isWebsocket = isWebsocket;
        init();
    }

    public void init() {
        bootGroup = new NioEventLoopGroup(10);
        workGroup = new NioEventLoopGroup(10, GlobalEventExecutor.INSTANCE);
    }

    private void start() {
        try {

            final SslContext sslContext = sslContext();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bootGroup, workGroup)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new NettyChannelInitializer(sslContext, true, this.isWebsocket))         // 连接到达时会创建一个通道
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            // Bind and start to accept incoming connections.
            ChannelFuture f = serverBootstrap.bind(port).sync(); // (7)
            log.info("Server start port :{}", port);
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("端口绑定成功:{}", port);
                    } else {
                        future.cause().printStackTrace();
                    }
                }
            }).sync();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            close();
        }
    }


    private void close() {
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        if (bootGroup != null) {
            bootGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        new NettyServer(9000).start();
    }


}
