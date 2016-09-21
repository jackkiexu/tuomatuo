package com.lami.tuomatuo.mq.base.netty.channel;

import java.util.EventListener;

/**
 * Created by xujiankang on 2016/9/21.
 */
public interface ChannelFutureListener extends EventListener {

    static ChannelFutureListener CLOSE = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture future) throws Exception {
            future.getChannel().close();
        }
    };

    /**
     * Invoke when the operation associated with the {@link ChannelFuture}
     * has been completed
     * @param future
     * @throws Exception
     */
    void operationComplete(ChannelFuture future) throws Exception;
}
