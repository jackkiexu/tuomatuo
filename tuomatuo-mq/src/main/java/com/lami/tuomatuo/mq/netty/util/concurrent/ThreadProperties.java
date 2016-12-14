package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * Expose detail for a {@link Thread}
 * Created by xujiankang on 2016/12/14.
 */
public interface ThreadProperties {

    /**
     * @see {@link Thread#getState()}
     * @return
     */
    Thread.State state();

    /**
     * @see {@link Thread#isInterrupted()}
     * @return
     */
    boolean isInterrupted();

    /**
     * @see {@link Thread#isDaemon()}
     * @return
     */
    boolean isDaemon();

    /**
     * @see {@link Thread#getName()}
     * @return
     */
    String name();

    /**
     * @see {@link Thread#getId()}
     */
    long id();

    /**
     * @see {@link Thread#getStackTrace()}
     * @return
     */
    StackTraceElement[] stackTrace();

    /**
     * @see {@link Thread#isAlive()}
     * @return
     */
    boolean isAlive();
}
