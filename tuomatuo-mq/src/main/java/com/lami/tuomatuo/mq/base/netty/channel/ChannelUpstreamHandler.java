package com.lami.tuomatuo.mq.base.netty.channel;

/**
 * Created by xujiankang on 2016/9/22.
 */
public interface ChannelUpstreamHandler extends ChannelHandler {

    void handlerUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception;

}
