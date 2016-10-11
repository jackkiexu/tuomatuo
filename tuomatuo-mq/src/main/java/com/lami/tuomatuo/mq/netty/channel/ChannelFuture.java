package com.lami.tuomatuo.mq.netty.channel;

import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2016/9/21.
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

    void addListener(ChannelFutureListener listener);
    void removeListener(ChannelFutureListener listener);

    ChannelFuture await()throws InterruptedException;
    ChannelFuture awaitUninterruptibly();

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;
    boolean await(long timoutMillis) throws InterruptedException;
    boolean awaitUninterruptibly(long timeout, TimeUnit unit);
    boolean awaitUninterruptibly(long timeoutMillis);

}
