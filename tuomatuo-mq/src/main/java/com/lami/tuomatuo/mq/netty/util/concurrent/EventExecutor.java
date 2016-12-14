package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * The {@link EventExecutor } is a special {@link EventExecutorGroup} which comes
 * with some handly method to see if a {@link Thread} is executed in a event loop
 * Besides this, it also extends the {@link EventExecutorGroup} to allow for a generic
 * way to access methods
 *
 * Created by xjk on 12/14/16.
 */
public interface EventExecutor extends EventExecutorGroup {

    /**
     * Return a reference to itself
     * @return
     */
    @Override
    EventExecutor next();

    /**
     * Return the {@link EventExecutorGroup} which is the parent of this {@link EventExecutor}
     * @return
     */
    EventExecutorGroup parent();

    /**
     * Calls {@link #inEventLoop(Thread)} with {@link Thread#currentThread()} as argument
     * @return
     */
    boolean inEventLoop();

    /**
     * Return {@code true} if the given {@link Thread} is executed in the event loop
     * {@code false} otherwise
     * @param thread
     * @return
     */
    boolean inEventLoop(Thread thread);

    /**
     * Return a new {@link Promise}
     * @param <V>
     * @return
     */
    <V> Promise<V> newPromise();

    /**
     * Create a new {@link ProgressivePromise}
     * @param <V>
     * @return
     */
    <V> ProgressivePromise<V> newProgressivePromise();

    /**
     * Create a new {@link Future} which is marked as successes already. So {@link Future#isSuccess()}
     * will return {@code true}. All {@link FutureListener} added to it will be notified directly. Also
     * every call of blocking methods will just return without blocking
     * @param result
     * @param <V>
     * @return
     */
    <V> Future<V> newSuccessedFuture(V result);

    /**
     * Create a new {@link Future} which is marked as fakued already. So {@link Future#isSuccess()}
     * will return {@code false}. All {@link FutureListener} added to it will be notified directly. Also
     * every call of blocking methods will just return without blocking
     * @param cause
     * @param <V>
     * @return
     */
    <V> Future<V> newFailedFuture(Throwable cause);
}
