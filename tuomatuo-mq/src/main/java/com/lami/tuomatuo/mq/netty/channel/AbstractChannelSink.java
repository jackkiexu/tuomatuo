package com.lami.tuomatuo.mq.netty.channel;

/**
 * Created by xjk on 2016/9/27.
 */
public abstract class AbstractChannelSink implements ChannelSink {

    public void exceptionCaught(ChannelPipeline pipeline, ChannelEvent e, ChannelPipelineException cause) throws Exception {
        Throwable actualCause = cause.getCause();
        if(actualCause == null){
            actualCause = cause;
        }

        Channels.fireExceptionCaught(e.getChannel(), actualCause);
    }
}
