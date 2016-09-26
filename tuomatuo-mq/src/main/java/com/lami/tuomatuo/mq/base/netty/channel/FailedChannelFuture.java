package com.lami.tuomatuo.mq.base.netty.channel;

/**
 * Created by xujiankang on 2016/9/26.
 */
public class FailedChannelFuture extends CompleteChannelFuture {

    private Throwable cause;

    public FailedChannelFuture(Channel channel, Throwable cause) {
        super(channel);
        this.cause = cause;
    }

    public FailedChannelFuture(Channel channel) {
        super(channel);
    }

    public Throwable getCause(){
        return cause;
    }

    public boolean isSuccess(){
        return false;
    }
}
