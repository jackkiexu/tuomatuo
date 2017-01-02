package com.lami.tuomatuo.search.base.concurrent.delayqueue;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * An unbounded {@linkplain BlockingQueue blocking queue} of
 * {@code Delayed} elements, in which an element can only be taken
 * when its delay has expired. The <em>head</em> of the queue is that
 * {@code Delayed} element whose delay expired furthest in the
 * past. If no delay has expired there is no head and {@code poll}
 * {@code getDelay(TimeUnit.NANOSECONDS)} method returns a value less
 * removed using {@code take} or {@code poll}, they are otherwise
 * treated as normal elements. For example, the {@code size} method
 * returns the count of both expired and unexpired elements.
 * This queue does not permit null elements
 *
 * <p>
 *     This class and its iterator implement all of the
 *     <em>optional</em> methods of the {@link Collection} and {@link
 *     Iterator} interfaces. The Iterator provided in method {@link
 *     #iterator()} is <em>not</em> guraranteed to traverse the elements of
 *     the DelayQueue in any particular order
 * </p>
 *
 * <p>
 *     This class is a member of the
 *     <a href="{@docRoot}/../technotes/guides/collections/index.html">
 *      Java Collection Framework
 *     </a>
 * </p>
 *
 * Created by xjk on 1/2/17.
 */
public class KDelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void put(E e) throws InterruptedException {

    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public E take() throws InterruptedException {
        return null;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }

    @Override
    public boolean offer(E e) {
        return false;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }
}
