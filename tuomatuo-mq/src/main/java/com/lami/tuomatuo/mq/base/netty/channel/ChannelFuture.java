package com.lami.tuomatuo.mq.base.netty.channel;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface ChannelFuture {

    Channel getChannel();

    boolean isDone();
    boolean isCancelled();
    boolean isSuccess();
    Throwable getCause();

    boolean cancel();

    void setSuccess();
    void setFailure(Throwable cause);


    ChannelFuture close();

}
