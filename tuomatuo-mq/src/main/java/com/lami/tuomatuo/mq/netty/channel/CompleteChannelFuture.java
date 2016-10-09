package com.lami.tuomatuo.mq.netty.channel;

import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/9/22.
 */
public class CompleteChannelFuture implements ChannelFuture {

    private static final Logger logger = Logger.getLogger(CompleteChannelFuture.class);

    private Channel channel;

    public CompleteChannelFuture(Channel channel) {
        if(channel == null) throw new NullPointerException("channel is null");
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isDone() {
        return true;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isSuccess() {
        return false;
    }

    public Throwable getCause() {
        return null;
    }

    public boolean cancel() {
        return false;
    }

    public void setSuccess() {

    }

    public void setFailure(Throwable cause) {

    }

    public void addListener(ChannelFutureListener listener) {
        try {
            listener.operationComplete(this);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An exception was thrown by" +
            ChannelFutureListener.class.getName());
        }
    }

    public void removeListener(ChannelFutureListener listener) {

    }

    public ChannelFuture await() throws InterruptedException {
        return this;
    }

    public ChannelFuture awaitUninterruptibly() {
        return this;
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return true;
    }

    public boolean await(long timoutMillis) throws InterruptedException {
        return true;
    }

    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        return true;
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        return true;
    }
}
