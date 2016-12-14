package com.lami.tuomatuo.mq.netty.util.concurrent;

/**
 * Specil {@link Future} which ia writable
 * Created by xujiankang on 2016/12/14.
 */
public interface Promise<V> extends Future<V> {

    /**
     * Marks this future as a success and notifies all
     * listeners
     *
     * If it is success or failed already it will throw an {@link IllegalStateException}
     * @param result
     * @return
     */
    Promise<V> setSuccess(V result);

    /**
     * Marks this future as a success and notifies all
     * listeners
     *
     * @param result {@code true} if and only if successfully marked this future as a success. Otherwise {@code false} because this future is
     *                           already marked as either a success or failure
     * @return
     */
    boolean trySuccess(V result);

    /**
     * Marks this future as a failure and notifies all listeners
     *
     * If is is success or failed already it will throw an {@link IllegalStateException}
     * @param cause
     * @return
     */
    Promise<V> setFailure(Throwable cause);

    /**
     * Marks this future as failure and notifies all listener
     *
     * @param cause
     * @return {@code true } if only if successfully
     */
    boolean tryFailure(Throwable cause);

    /**
     * Make this future impossible to cancel
     * @return {@code} if and successfully marked this future as uncancellable or it is already done
     * without being cancelled {@code false} if this future has been cancelled already
     */
    boolean setUncancellable();


}
