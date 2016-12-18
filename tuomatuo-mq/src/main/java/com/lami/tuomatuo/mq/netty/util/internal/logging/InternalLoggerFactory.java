package com.lami.tuomatuo.mq.netty.util.internal.logging;

/**
 * Created by xjk on 12/18/16.
 */
public abstract class InternalLoggerFactory {

    private static volatile InternalLoggerFactory defaultFactory =
            newDefaultFactory(InternalLoggerFactory.class.getName());

    private static InternalLoggerFactory newDefaultFactory(String name){
        return null;
    }
}
