package com.lami.tuomatuo.mq.base.netty.channel;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface ChannelHandlerContext {

    ChannelPipeline getPipeline();

    String getName();

    ChannelHandler getHandler();
    boolean canHandleUpstream();
    boolean canHandleDownstream();

    void sendUpstream(ChannelEvent e);
    void sendDownstream(ChannelEvent e);

}