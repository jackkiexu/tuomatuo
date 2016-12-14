package com.lami.tuomatuo.mq.netty.util.concurrent;

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
    Future<V> addlistener(GenericFutureListener<? extends Future<? super V>> listener);

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

}
