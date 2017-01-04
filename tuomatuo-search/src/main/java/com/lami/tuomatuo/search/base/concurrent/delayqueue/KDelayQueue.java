package com.lami.tuomatuo.search.base.concurrent.delayqueue;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

    private final transient ReentrantLock lock = new ReentrantLock();
    private final PriorityQueue<E> q = new PriorityQueue<E>();

    /**
     * Thread designated to wait for the element at the head of
     * the queue. This variant of the Leade-Foller parttern
     * (http://www.cs.wustl.edu/~schmidt/POSA/POSA2/) serves to
     * minimize unnecessary timed waiting. When a thread becomes
     * the leader, it waits only for the next delay to elapse, but
     * other threads awaits indefinitely. The leader thread must
     * signal some other thread before returning from take() or
     * pool(...), unless some other thread becomes leader in the
     * interim. Whenever the head of the queue is replaced with
     * an element with an earlier expiration time, the leader
     * filed is invalidated by being reset to null, and some
     * waiting thread, but not necessarily the current leader, is
     * signalled. So waiting threads must be prepared to acquire
     * and lose leadership while waiting
     */
    private Thread leader = null;

    /**
     * Condition signalled when a newer element becomes available
     * at the head of the queue or a new thread may need to
     * become leader
     */
    private final Condition available = lock.newCondition();

    /**
     * Creates a new {@code DelayQueue} that is initially empty
     */
    public KDelayQueue(){}

    /**
     * Creates a {@code KDelayQueue} initially containing the elements of the
     * given collection of {@link Delayed} instances.
     *
     * @param c the collection of elements to initially contain
     * @throws NullPointerException if the specified collection
     *                  or any of its elements are null
     */
    public KDelayQueue(Collection<? extends E> c) { this.addAll(c); }

    /**
     * Inserts the specified element into this delay queue
     *
     * @param e {@code true} (as specified by {@link Collection#add(Object)})
     * @return
     */
    public boolean add(E e){
        return offer(e);
    }

    /**
     * Inserts the specified element into the delay queue
     * @param e the element to add
     * @return  {@code true}
     */
    @Override
    public boolean offer(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            q.offer(e);
            if(q.peek() == e){
                leader = null;
                available.signal();;
            }
            return true;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element into this delay queue. As the queue is
     * unbounded this method will never block
     *
     * @param e the element to add
     * @throws InterruptedException
     */
    @Override
    public void put(E e) throws InterruptedException {
        offer(e);
    }

    /**
     * Inserts the specified elements into this delay queue. As the queue is
     * unbounded this method will never block
     *
     * @param e     the element to add
     * @param timeout   This parameter is ignored as the method never blocks
     * @param unit      This parameter is ignored as the method never blocks
     * @return          {@code true}
     * @throws InterruptedException
     */
    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e);
    }



    /**
     * Retrieves and removes the head of this queue, or returns {@code null}
     * if this queue has no elements with an expired delay
     *
     * @return the head of this queue, or {@code null} if this
     *          queue has no elements with an expired delay
     */
    @Override
    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            E first = q.peek();
            if(first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0){
                return null;
            }else{
                return q.poll();
            }
        }finally {
            lock.unlock();
        }
    }


    /**
     * Retrieves and removes the head of this queue, waiting if necessary
     * until an element with an expired delay is available on this queue
     *
     * @return the head of this queue
     * @throws InterruptedException {@inheritDoc}
     */
    @Override
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for(;;){
                E first = q.peek();
                if(first == null){
                    available.await();
                }else{
                    long delay = first.getDelay(TimeUnit.NANOSECONDS);
                    if(delay <= 0){
                        return q.poll();
                    }
                    first = null; // don't retain ref while waiting
                    if(leader != null){
                        available.await();
                    }else{
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            available.awaitNanos(delay);
                        }finally {
                            if(leader == thisThread){
                                leader = null;
                            }
                        }
                    }
                }
            }
        }finally {
            if(leader == null && q.peek() != null){
                available.signal();
            }
            lock.unlock();
        }
    }

    /**
     * Retrieves and removes  the head of this queue, waiting if necessary
     * until an element with an expired delay is available on this queue,
     * or the specified wait time expires
     *
     * @param timeout
     * @param unit
     * @return  the head of this queue, or {@code null} if the
     *          specified  waiting time elapses before an element with
     *          an expired dely becomes available
     * @throws InterruptedException {@inheritDoc}
     */
    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for(;;){
                E first = q.peek();
                if(first == null){
                    if(nanos <= 0){
                        return null;
                    }else{
                        nanos = available.awaitNanos(nanos);
                    }
                }else{
                    long delay = first.getDelay(TimeUnit.NANOSECONDS);
                    if(delay <= 0){
                        return q.poll();
                    }
                    if(nanos <= 0){
                        return null;
                    }
                    first = null; // don't retain ref while waiting
                    if(nanos < delay || leader != null){
                        nanos = available.awaitNanos(nanos);
                    }else{
                        Thread thisThread= Thread.currentThread();
                        leader = thisThread;
                        try{
                            long timeLeft = available.awaitNanos(delay);
                            nanos -= delay - timeLeft;
                        }finally {
                            if(leader == thisThread){
                                leader = null;
                            }
                        }
                    }
                }
            }
        }finally {
            if(leader == null && q.peek() != null){
                available.signal();
            }
            lock.unlock();
        }
    }

    /**
     * Retrieves, but does not remove, the head of this queue, or
     * returns {@code null} if this queue is empty. Unlike
     * {@code poll}, if no expired elements are available in the queue,
     * this method returns the element that will expire next,
     * if no exists.
     *
     * @return the head of this queue, or {@code null} if this
     *      queue is empty
     */
    @Override
    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.peek();
        }finally {
            lock.unlock();
        }
    }


    @Override
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.size();
        }finally {
            lock.unlock();
        }
    }

    /**
     * Returns first element only if it is expired.
     * Used only by drainTo. Call only when holding lock
     * @return
     */
    private E peekExpired(){
        // assert lock.isHeldByCurrentThread
        E first = q.peek();
        return (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) ?
                null : first;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }


    /**
     * Atomically removes all of the elements from this delay queue.
     * The queue will be empty after this call returns.
     * Elements with an unexpired delay are not waited for; they are
     * simply discarded from the queue
     */
    public void clear(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            q.clear();
        }finally {
            lock.unlock();;
        }
    }

    /**
     * Always returns {@code Integer.MAX_VALUE} because
     * a {@code KDelayQueue} is not capacity constrained
     *
     * @return {@code Integer.MAX_VALUE}
     */
    public int remainingCapacity(){
        return Integer.MAX_VALUE;
    }

    /**
     * Returns an array containing all of the elements in the queue.
     * The returned array elements are in no particular order.
     *
     * <p>
     *     The returned array will be "safe" in that no references to it are
     *     maintained by this queue. (In other words, this method must allocate
     *     a new array). The caller is thus free to modify the returned array
     * </p>
     *
     * <p>This method acts as bridge between array-based and collection-based APIs</p>
     *
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return q.toArray();
        }finally {
            lock.unlock();
        }
    }

    /**
     * Returns an array containing all of the elements in this queue; the
     * runtime type of the returned array is that of the specified array.
     * The returned array elements are in no particular order.
     * If the queue fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this queue
     *
     * <p>
     *    If this queue fits in the specified array with room to spare
     *    (i.e., the array has more elements than this queue), the element in
     *    the array immediately following the end of the queue is set to
     *    {@code null}
     * </p>
     *
     * <p>
     *     Like the {@link #toArray()} method, this method acts as bridge between
     *     array-based and collection-based APIs. Further, this method allows
     *     precise control over the runtime type of the output array, and may,
     *     under certain circumstances, be used to save allocation costs
     * </p>
     *
     * <p>
     *     The following code can be used to dump a delay queue into a newly
     *     allocated array of {@code Delayed}
     * </p>
     *
     * <pre> {@code Delayed[] a = q.toArray(new Delayed[0]); }</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}
     *
     * @param a the array into which the elements of the queue are to
     *          be stored. if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     *          is not a supertype of the runtime type of every element in
     *          this queue
     *
     */
    public <T> T[] toArray(T[] a){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.toArray(a);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Remove a single instance of the specified element from this
     * queue, if it is present, whether or not it has expired.
     * @param o
     * @return
     */
    public boolean remove(Object o){
       final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return q.remove(o);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Identity-based version for use in Itr.remove
     * @param o
     */
    void removeEQ(Object o){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for(Iterator<E> it = q.iterator(); it.hasNext();){
                if(o == it.next()){
                    it.remove();
                    break;
                }
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * Returns an iterator over all the elements (both expired and
     * unexpired) in this queue. The iterator does not return the
     * elements in any particular order.
     *
     * <p>
     *     The returned iterator is
     *     <a href="package-summary.html#Weakly">
     *      <i>
     *          weakly consistent
     *      </i>
     *     </a>
     * </p>
     *
     * @return
     */
    public Iterator<E> iterator() { return new Itr(toArray()); }

    /**
     * Snapshot iterator that works off copy of underlying q array
     */
    private class Itr implements Iterator<E>{

        final Object[] array;       // Array of all elements
        int cursor;                 // index of next element to return
        int lastRet;                // index of last element, or -1 if no such

        public Itr(Object[] array) {
            lastRet = -1;
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return cursor < array.length;
        }

        @Override
        public E next() {
            if(cursor >= array.length){
                throw new NoSuchElementException();
            }
            lastRet = cursor;
            return (E)array[cursor++];
        }

        public void remove(){
            if(lastRet < 0){
                throw new IllegalStateException();
            }
            removeEQ(array[lastRet]);
            lastRet = -1;
        }
    }
}