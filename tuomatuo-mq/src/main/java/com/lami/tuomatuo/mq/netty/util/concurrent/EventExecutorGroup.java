package com.lami.tuomatuo.mq.netty.util.concurrent;


import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ScheduledFuture;

/**
 * The {@link EventExecutorGroup} is responsible for providing the {@link EventExecutor}'s to use
 * via its {@link #next()} method. Besides this, It is also responsible for handling their
 * life-cycle and allows shutting then down in a global fashion
 *
 * Created by xjk on 12/14/16.
 */
public interface EventExecutorGroup extends ScheduledExecutorService, Iterable<EventExecutor> {

    /**
     * Return {@code true} if and only if this executor was started to be
     * {@linkplain #shutdownGracefully() shut down gracefully} or wa {@linkplain #isShuttingDown()} shut down.
     *
     * @return
     */
    boolean isShuttingDown();

    /**
     * Shortcut method for {@link #shutdownGracefully(long, long, java.util.concurrent.TimeUnit)} with sensible default values
     * @return the {@link #terminationFuture()}
     */
    Future<?> shutdownGracefully();

    /**
     * Signals this executor that the caller wants the executor to be shut down. Once this method is called,
     * {@link #isShuttingDown()} starts to return {@code true}, and executor prepares to shut itself down.
     * Unlike {@link #shutdown()}, graceful shut down ensures that no tasks are submitted for <i>'the quiet period'</i>
     * (usually a couple seconds) before it shuts itself down. If a task is submitted during the quiet period.
     * it is guaranteed to be accepted and the quiet period will start over
     *
     * @param quietPeriod the quiet period as described in the documentation
     * @param timeout     the maximum amount of time to wait until the executor is {@linkplain #shutdown()}
     *                    regradless if a task was submitted during the quiet period
     * @param unit        the unit of {@code quietPeriod} and {@code timeout}
     * @return
     */
    Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);

    /**
     * Return the {@link Future} which is notified when this executor has been terminated
     * @return
     */
    Future<?> terminationFuture();

    /**
     * {@link #shutdownGracefully(long, long, TimeUnit)} or {@link #shutdownGracefully()} instead
     */
    @Override
    void shutdown();

    /**
     * {@link #shutdownGracefully(long, long, TimeUnit)} or {@link #shutdownGracefully()} instead
     * @return
     */
    @Override
    List<Runnable> shutdownNow();

    /**
     * Return one of the {@link EventExecutor}'s that belong to this group
     * @return
     */
    EventExecutor next();

    /**
     * Return a read-only {@link Iterator} over all {@link EventExecutor}, which are handled by this
     * {@link EventExecutorGroup} at the time of invoke this method
     * @return
     */
    @Override
    Iterator<EventExecutor> iterator();

    @Override
    Future<?> submit(Runnable task);

    @Override
    <T> Future<T> submit(Runnable task, T result);

    @Override
    <T> Future<T> submit(Callable<T> task);

    @Override
    ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);

    @Override
    <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    @Override
    ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
}
