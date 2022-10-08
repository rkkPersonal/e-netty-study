package com.netty.study.server.websocket;

import com.alibaba.fastjson.JSONObject;
import com.netty.study.util.NettyTool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Steven
 * @date 2022年10月06日 1:15
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class WebsocketTextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(10, 20, 30L, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(100), r -> new Thread(r, "Work task pool========>>>" + r.hashCode()), (r, executor) -> log.error("线程被拒绝执行了..."));

    @Resource
    private RedisTemplate redisTemplate;


    /**
     * Handle text frame
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();

        JSONObject jsonObject = JSONObject.parseObject(text);
        String userId = jsonObject.getString("userId");
        NettyTool.getUserChannelGroup().put(userId, ctx.channel());
        // 将用户ID作为自定义属性加入到channel中，方便随时channel中获取用户ID
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        ctx.channel().attr(key).setIfAbsent(userId);
        Channel incoming = ctx.channel();
        for (Channel channel : NettyTool.getChannelGroup()) {
            if (channel != incoming) {
                boolean active = channel.isActive();
                if (active) {
                    channel.writeAndFlush(new TextWebSocketFrame("[" + incoming.remoteAddress() + "]" + msg.text()));
                   /* Object userInfo = redisTemplate.opsForValue().get(userId);
                    if (Optional.ofNullable(userInfo).isPresent()) {
                        redisTemplate.delete(userId);
                    }*/
                } else {
                    //TODO  if use is not online ,The message will be storage to db or cache
                    executor.execute(() -> {
                        /*redisTemplate.opsForValue().set(userId, true);*/
                        log.warn("用户不在线: 存储离线信息");
                    });
                }

            } else {
                channel.writeAndFlush(new TextWebSocketFrame("在线人数：" + NettyTool.getUserChannelGroup().size() + "--[You]" + msg.text()));
            }
        }

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        /*NettyTool.getChannelGroup().add(incoming);*/
        log.info("Client:" + incoming.remoteAddress() + "在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        /*NettyTool.getChannelGroup().remove(incoming);*/
        System.out.println("Client:" + incoming.remoteAddress() + "掉线");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // Broadcast a message to multiple Channels
        ctx.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 加入"));
        NettyTool.getChannelGroup().add(incoming);
        log.info("Client:" + incoming.remoteAddress() + "加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // Broadcast a message to multiple Channels
        ctx.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 离开"));
        log.info("Client:" + incoming.remoteAddress() + "离开");
        // A closed Channel is automatically removed from ChannelGroup,
        // so there is no need to do "channels.remove(ctx.channel());"
        NettyTool.getChannelGroup().remove(incoming);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        log.info("Client:" + incoming.remoteAddress() + "异常");
        // 当出现异常就关闭连接
        log.error("服务异常:{}", cause);
        ctx.close();
    }
}
