package com.lami.tuomatuo.mq.netty.util.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * The result of an asynchronous operation
 *
 * Created by xjk on 12/13/16.
 */
public interface Future<V> extends java.util.concurrent.Future<V> {

    /**
     * Return {@code true} if and only if the I/O operation was completed successfully
     * @return
     */
    boolean isSuccess();

    /**
     * returns {@code true} if and only if the operation can be cancelled via {@link #cancel(boolean)}
     * @return
     */
    boolean isCancellable();

    /**
     * Return the cause of the failed I/O opearion if the I/O operation has failed
     * @return the cause of the failure is not completed yet
     */
    Throwable cause();

    /**
     * Adds the specified listener to this future. The
     * specified listener is notified when this future is
     * {@linkplain #isDone() done} If specied listener is notified immediately
     *
     * @param listener
     * @return
     */
    Future<V> addListener(GenericFutureListener<? extends Future<? super V>> listener);

    /**
     * Adds the specified listener to this future. The
     * specified listener are notified when this future is
     * {@linkplain #isDone() done } If this future is already
     * completed, the specified listeners are notified immediately
      * @param listeners
     * @return
     */
    Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    /**
     * Remove the first occurence of the specified listener from this future
     * The specified listener is no longer notified when this
     * future is not associated with this future. this method
     * does nothing and returns silenty
     * @param listener
     * @return
     */
    Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    /**
     * Remove the first occurrence for each of the listeners from this future
     * The specified listeners are no longer notified when this
     * future is {@linkplain #isDone() done} If the specified
     * listeners are not associated with this future, this method
     * does nothing and returns silently
     * @param listeners
     * @return
     */
    Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    /**
     * Waits for this future until it is done. and rethrows tha cause of the failure if this future
     * failed
     */
    Future<V> sync() throws InterruptedException;

    /**
     * Waits for this future until it is done, and rethrows the cause of the failure if this future failed
     * @return
     */
    Future<V> syncUninterruptibly();

    /**
     * Waits for this future to be completed
     * @throws InterruptedException
     *          if the current thread was interrupted
     */
    Future<V> await() throws InterruptedException;

    /**
     * Waits for this future to be completed without
     * interruption. This method catches an {@link InterruptedException} and
     * discards it silently
     * @return
     */
    Future<V> awaitUninterruptibly();

    /**
     * Waits for this future to be completed within the
     * specified time limit
     *
     * @param timeout
     * @param unit
     * @return {@code true} if and only if the future was completed within
     *                      the specified time limit
     * @throws InterruptedException
     *          If the current thread wad interrupted
     */
    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     *
     * Waits for this future to be completed within the specified time limit
     *
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     *          if the current thread was interrupted
     */
    boolean await(long timeoutMillis) throws InterruptedException;

    /**
     * Waits for this future to be completed within the
     * specified time limit without interruption. This method catches an
     * @{link InterruptedException} and discards it silently
     *
     * @param timeout
     * @param unit
     * @return {@code true} if and only if the future was completed within
     *              the specified time limit
     */
    boolean awaitUninterruptibly(long timeout, TimeUnit unit);

    /**
     * Waits for this future to be completed within the
     * specified time limit wthout interruption. This method catches an
     * @{link InterruptedException} and discards it silently
     *
     * @param timeoutMillis
     * @return {@code true} if and only if the future was completed within
     *                      the specified time limit
     */
    boolean awaitUninterruptibly(long timeoutMillis);

    /**
     * Return the result without blocking. If the future is not done yet this will return {@code null}
     * As it is possible that a {@code null} value is used to mark the future as successful you also need to check
     * if the future is ready done with {@link #isDone()} and not rely on the returned {@code null} value
     */
    V getNow();

    /**
     * If the cancellation was successful it will fail the future with an {@link java.util.concurrent.CancellationException}
     * @param mayInterruptIfRunning
     * @return
     */
    @Override
    boolean cancel(boolean mayInterruptIfRunning);
}
