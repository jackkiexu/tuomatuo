package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xujiankang on 2016/9/22.
 */
public interface ChannelDownstreamHandler extends ChannelHandler {

    void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)throws Exception;

}
