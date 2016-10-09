package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xujiankang on 2016/9/22.
 */
public interface ChannelSink {

    void eventSunk(ChannelPipeline pipline, ChannelEvent e) throws Exception;

    void exceptionCaught(ChannelPipeline pipeline, ChannelEvent e, ChannelPipelineException cause) throws Exception;
}
