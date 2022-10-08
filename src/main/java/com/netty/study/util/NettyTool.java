package com.netty.study.util;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Steven
 * @date 2022年10月08日 22:01
 */
public class NettyTool {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final Map<String, Channel> userChannelGroup = new ConcurrentHashMap<>();

    public static ChannelGroup getChannelGroup() {
        return channels;
    }

    public static Map<String, Channel> getUserChannelGroup() {
        return userChannelGroup;
    }
}
