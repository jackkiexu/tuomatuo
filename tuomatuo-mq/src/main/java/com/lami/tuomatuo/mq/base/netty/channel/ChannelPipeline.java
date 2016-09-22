package com.lami.tuomatuo.mq.base.netty.channel;

import java.util.Map;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface ChannelPipeline {

    void addFirst   (String name, ChannelHandler handler);
    void addLast    (String name, ChannelHandler handler);
    void addBefore  (String baseName, String name, ChannelHandler handler);
    void addAfter   (String baseName, String name, ChannelHandler handler);

    void remove(ChannelHandler handler);
    ChannelHandler remove(String name);
    <T extends ChannelHandler> T remove(Class<T> handlerType);
    ChannelHandler removeFirst();
    ChannelHandler removeLast();

    void replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler);
    ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler);
    <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler);

    ChannelHandler getFirst();
    ChannelHandler getLast();

    ChannelHandler get(String name);
    <T extends ChannelHandler> T get(Class<T> handlerType);

    ChannelHandlerContext getContext(ChannelHandler handler);
    ChannelHandlerContext getContext(String name);
    ChannelHandlerContext getContext(Class<? extends ChannelHandler> handlerType);

    void sendUpstream(ChannelEvent e);
    void sendDownstream(ChannelEvent e);

    Channel getChannel();
    ChannelSink getSink();
    void attach(Channel channel, ChannelSink sink);

    Map<String, ChannelHandler> toMap();

}
