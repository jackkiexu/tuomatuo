package com.lami.tuomatuo.search.base.concurrent.arrayblockingqueue;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * http://www.cnblogs.com/leesf456/p/5533770.html
 *
 * A bounded {@link "BlockingQueue} backed by an
 * array. This queue orders elements FIFO (first-in-first-out). The
 * <em>head</em> of the queue is that element that has been on the
 * queue the longest time. The <em>tail</em> of the queue is that
 * element that has been on the queue that shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue.
 *
 * <p>
 *     This is a classic bounded buffer, in which a
 *     fixed-sized array holds elements inserted by producers and
 *     extracted by consumers. Once created, the capacity cannot be
 *     changed. Attempts to {@code put} an element into a full queue
 *     will result in the operation blocking; attempts to {@code take} an
 *     element from an empty queue will similarly block
 * </p>
 *
 * <p>
 *     This class supports an optional fairness policy for ordering
 *     waiting producer and consumer threads. By default, this ordering
 *     is not guaranteed. However, a queue constructed with fairness set
 *     to {@code true} grants threads access in FIFO order. Fairness
 *     generally decreases throughput but reuces variability and avoids
 *     starvations
 * </p>
 *
 * <p>
 *     This class and its iterator implement all of the
 *     <em>optional</em> methods of the {@link "Collection} and {@link
 *     java.util.Iterator} interfaces
 * </p>
 *
 * <p>
 *     This class is a member of the
 *     <a href="{@docRoot/../technotes/guides/collections/index.html}">Java Collection Framework</a>
 * </p>
 *
 *
 * Created by xujiankang on 2017/2/3.
 */
public class KArrayBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable{

    /**
     * Serialization ID. This class relies on default serialization
     * even for the item array, which is default-serialized, even if
     * it is empty. Otherwise it could not declared final, which is
     * necessary here.
     */
    private static final long serialVersionUID = -817911632652898426L;

    /** The queued items */
    final Object[] items;

    /** items index for next take, poll, peek or remove */
    int takeIndex;

    /** items index for next put, offer, or add  */
    int putIndex;

    /** Number of elements in the queue */
    int count;

    /**
     * Concurrency control uses the classic two-condition algorithm
     * found in any textbook
     */

    /** Main lock for waiting takes */
    final ReentrantLock lock;

    /** Condition for waiting takes */
    private final Condition notEmpty;

    /** Condition for waiting puts */
    private final Condition notFull;

    /**
     * Shared state for currently active iterators, or null if there
     * are known not to be any. Allows queue operations to update
     * iterator state
     */
    transient Itrs itrs = null;


    /**
     * Iterator for KArrayBlockingQueue
     *
     * To maintain weak consistency with respect to puts and takes, we
     * read ahead one slot, so as to not report hasNext true but then
     * not have an element to return
     *
     * We switch into "detached" mode (allowing prompt unlinking from
     * itrs without help from the GC) when all indices are negative, or
     * when hasNext returns false for the first time. This allows the
     * iterator to track concurrent updates completely accurately,
     * except for the corner case of the user calling Iterator.remove()
     * after hasNext() returned false. Even in this case, we ensure
     * that we don't remove the wrong element by keeping track of the
     * expected element to remove, in lastItem. Yes, we may fail to
     * remove lastItem from the queue if it moved due to an interleaved
     * interior remove while in detached mode
     */
    private class Itr implements Iterator<E>{
        /** Index to look for new nextItem; NONE at end */
        private int cursor;

        /** Element to be returned by next call to next(); null if none */
        private E nextItem;

        /** Index of nextItem; NONE if none, REMOVED if moved elsewhere */
        private int nextIndex;

        /** Last element returned; null if none or not detached */
        private E lastItem;

        /** Index of lastItem, NONE if none, REMOVED if moved elsewhere */
        private int lastRet;

        /** Previous value of takeIndex, or DETACHED when detached */
        private int prevTakeIndex;

        /** Previous value of iters.cycles */
        private int prevCycles;

        /** Special index value indicating "not available" or undefined */
        private static final int NONE = -1;

        /**
         * Special index value indicating "removed elsewhere", that is,
         * removed by some operation other than a call to this.remove().
         */
        private static final int REMOVED = -2;

        /** Special value for prevTakeIndex indicating "detached mode" */
        private static final int DETACHED = -3;

        Itr(){
            // assert lock.getHoldCount == 0
            lastRet = NONE;
            final ReentrantLock lock = KArrayBlockingQueue.this.lock;
            lock.lock();
            try{
                if(count == 0){
                    // assert itrs == null;
                    cursor = NONE;
                    nextIndex = NONE;
                    prevTakeIndex = DETACHED;
                }
                else{
                    final int takeIndex= KArrayBlockingQueue.this.takeIndex;
                    prevTakeIndex = takeIndex;
                    nextItem = itemAt(nextIndex = takeIndex);
                    cursor = incCursor(takeIndex);
                    if(itrs == null){
                        itrs = new Itrs(this);
                    } else {
                        itrs.register(this); // in this order
                        itrs.doSomeSweeping(false);
                    }
                    prevCycles = itrs.cycles;

                    /**
                     * assert takeIndex >= 0
                     * assert prevTakeIndex == takeIndex
                     * assert nextIndex >= 0
                     * assert nextItem != null
                     */
                }
            }finally {
                lock.unlock();
            }
        }

        boolean isDetached(){
            // assert lock.getHoldCount() == 1
            return prevTakeIndex < 0;
        }

        private int incCursor(int index){
            // assert lock.getHoldCount() == 1
            if(++index == items.length){
                index = 0;
            }
            if(index == putIndex){
                index = NONE;
            }
            return index;
        }

        private boolean invalidated(int index, int prevTakeIndex,
                                    long dequeues, int length){
            if(index < 0){
                return false;
            }
            int distance = index - prevTakeIndex;
            if(distance < 0){
                distance += length;
            }
            return dequeues > distance;
        }

        /**
         * Adjusts indices to incorporate all dequeues since the last
         * operation on this iterator. Call only from iterating thead
         */
        private void incorporateDequeues(){
            /**
             * assert lock.getHoldCount == 1;
             * assert itrs != null
             * assert !isDetached()
             * assert count > 0
             */

            final int cycles = itrs.cycles;
            final int takeIndex = KArrayBlockingQueue.this.takeIndex;
            final int prevCycles = this.prevCycles;
            final int prevTakeIndex = this.prevTakeIndex;

            if(cycles != prevCycles || takeIndex != prevTakeIndex){
                final int len = items.length;
                /**
                 * How far takeIndex has advanced since the previous
                 * operation of this iterator
                 */
                long dequeues = (cycles - prevCycles) * len
                        + (takeIndex - prevTakeIndex);

                // Check indices for invalidation
                if(invalidated(lastRet, prevTakeIndex, dequeues, len)){
                    lastRet = REMOVED;
                }
                if(invalidated(nextIndex, prevTakeIndex, dequeues, len)){
                    nextIndex = REMOVED;
                }
                if(invalidated(cursor, prevTakeIndex, dequeues, len)){
                    cursor = takeIndex;
                }

                if(cursor < 0 && nextIndex < 0 && lastRet < 0){
                    detach();
                }else{
                    this.prevCycles = cycles;
                    this.prevTakeIndex = takeIndex;
                }
            }
        }

        private void detach(){
            /**
             * Switch to detached mode
             * assert lock.getHoldCount() == 1
             * assert cursor == NONE
             * assert nextIndex < 0
             * assert lastRet < 0 || nextIem == null
             * assert lastRet < 0 ^ lastItem != null
             */
            if(prevTakeIndex >= 0){
                // assert itrs != null
                prevTakeIndex = DETACHED;
                // try to unlink from itrs (but not too hard)
                itrs.doSomeSweeping(true);
            }
        }

        /**
         * For preformance reasons, we would like not to acquire a lock in
         * hasNext in the case. To allow for this, we only access
         * fields(i.e nextItem) that are ot modified by update operations
         * triggered by queue modifications
         * @return
         */
        @Override
        public boolean hasNext() {
            // assert lock.getHoldCount == 0
            if(nextItem != null){
                return true;
            }
            noNext();
            return false;
        }

        private void noNext(){
            final ReentrantLock lock = KArrayBlockingQueue.this.lock;
            lock.lock();
            try{
                // assert cursor == NONE
                // assert nextIndex == NONE
                if(!isDetached()){
                    // assert lastRet >= 0;
                    incorporateDequeues(); // might update lastRet
                    if(lastRet >= 0){
                        lastItem = itemAt(lastRet);
                        // assert lastItem != null
                        detach();
                    }
                }
                // assert isDetached();
                // assert lastRet < 0 ^ lastItem != null
            }finally {
                lock.unlock();
            }
        }

        @Override
        public E next() {
            // assert lock.getHoldCount() == 0;
            final E x = nextItem;
            if(x == null){
                throw new NoSuchElementException();
            }
            final ReentrantLock lock = KArrayBlockingQueue.this.lock;
            lock.lock();
            try{
                if(!isDetached()){
                    incorporateDequeues();
                }
                /**
                 * assert nextIndex != NONE
                 * assert lastItem == null
                 */
                lastRet = nextIndex;
                final int cursor = this.cursor;
                if(cursor >= 0){
                    nextItem = itemAt(nextIndex = cursor);
                    // assert nextItem != null
                    this.cursor = incCursor(cursor);
                }else{
                    nextIndex = NONE;
                    nextItem = null;
                }
            }finally {
                lock.unlock();
            }
            return x;
        }


        public void remove(){
            // assert lock.getHoldCount() == 0
            final ReentrantLock lock = KArrayBlockingQueue.this.lock;
            lock.lock();
            try{
                if(!isDetached()){
                    incorporateDequeues(); // might update lastRet or detach
                }
                final int lastRet = this.lastRet;
                this.lastRet = NONE;
                if(lastRet >= 0){
                    if(!isDetached()){
                        removeAt(lastRet);
                    }else{
                        final E lastItem = this.lastItem;
                        // assert lastItem != null
                        this.lastItem = null;
                        if(itemAt(lastRet) == lastItem){
                            removeAt(lastRet);
                        }
                    }
                }else if(lastRet == NONE){
                    throw new IllegalStateException();
                }
                /**
                 * else lastRet == REMOVE and the last returned element was
                 * previously asynchronously removed via an operation other
                 * than this.remove(), so nothing to do.
                 */


                if(cursor < 0 && nextIndex < 0){
                    detach();
                }
            }finally {
                lock.unlock();
                // assert lastRet == NONE
                // assert lastItem == null
            }
        }


        /**
         * Called to notify the iterator that the queue is empty, or that it
         * has fallen hopelessly behind, so that it should abandon any
         * further iteration, except possibly to return one more element
         * from next(), as promised by returning true from hasNext();
         */
        void shutdown(){
            // assert lock.getHoldCount() == 1
            cursor = NONE;
            if(nextIndex >= 0){
                nextIndex = REMOVED;
            }
            if(lastRet >= 0){
                lastRet = REMOVED;
                lastItem = null;
            }
            prevTakeIndex = DETACHED;
            /**
             * Don't set nextItem to null because we must continue to be
             * able to return it on next();
             * Caller will unlink from itrs when convenient.
             */
        }

        private int distance(int index, int prevTakeIndex, int length){
            int distance = index - prevTakeIndex;
            if(distance < 0){
                distance += length;
            }
            return distance;
        }

        boolean removeAt(int removedIndex){
            // assert lock.getHoldCount() == 1
            if(isDetached()){
                return true;
            }

            final int cycles = itrs.cycles;

            return false;
        }




    }

    /**
     * Returns a {@link Spliterator} over the elements in this queue.
     *
     * <p>
     *     The returned spliterator is
     *     <a href="package-summary.html#Weakly">weakly consistent</a>
     * </p>
     *
     * <p>
     *     The {@code Spliterator} reports {@link Spliterator#CONCURRENT},
     *     {@link Spliterator#ORDERED}, and {@link Spliterator#NONNULL}.
     * </p>
     *
     * @implNote
     * The {@code Spliterator} implements {@code trySplit} to permit limited
     * parallelism
     *
     * @return a {@code Spliterator} over the elements in this queue
     */
    public Spliterator<E> spliterators(){
        return Spliterators.spliterator(this, Spliterator.ORDERED |
                                Spliterator.NONNULL | Spliterator.CONCURRENT);

    }
}
