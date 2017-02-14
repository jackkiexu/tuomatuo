package com.lami.tuomatuo.search.base.concurrent.linkedtransferqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2/14/17.
 */
public interface KTransferQueue<E> extends BlockingQueue<E> {

    /**
     * Transfers the element to a waiting consumer immediately, if possible.
     *
     * <p>
     *     More preceisely, transfers the specified element immediately
     *     if there exists a consumer already waiting to receive it (in
     *     {@link #take()}) or timed {@link #poll(long, TimeUnit)},
     *     otherwise returning {@code false} without enqueuing the element
     * </p>
     *
     * @param e the element to transfer
     * @return {@code true} if the element was transferred, else
     *          {@code false}
     * @throws ClassCastException if the class of the specified element
     *          prevent it from being added to this queue
     * @throws NullPointerException if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *          element prevents it from being added to this queue
     */
    boolean tryTransfer(E e);



}
