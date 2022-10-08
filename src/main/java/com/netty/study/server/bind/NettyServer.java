package com.netty.study.server.bind;

import com.netty.study.server.initializer.NettyChannelInitializer;
import com.netty.study.ssl.SslContextTool;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
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
        workGroup = new NioEventLoopGroup(10);
    }

    private void start() {
        try {

            final SslContext sslContext = sslContext();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bootGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.WARN))
                    .childHandler(new NettyChannelInitializer(sslContext, this.isWebsocket)) ;        // 连接到达时会创建一个通道

            // Bind and start to accept incoming connections.
            Channel f = serverBootstrap.bind(port).sync().channel(); // (7)
            log.info("Server start port :{}", port);
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.closeFuture().sync();
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
        /*new NettyServer(8080).start();*/
        new NettyServer(8081,true).start();
    }


}
