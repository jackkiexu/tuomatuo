package com.lami.tuomatuo.search.base.concurrent.linkedblockingqueue;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * http://www.cnblogs.com/leesf456/p/5539071.html
 *
 * An optionally-bounded {@link BlockingQueue} based on
 * linked nodes
 * This queue orders elements FIFO (first-in-first-out)
 * The <em>head</em> of the queue is that element that has been on the
 * queue the shortest time. new elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue
 * Lonked queues typically have higher throughout than array-based queues but
 * less predictable preformance in most concurrent applications
 *
 * <p>
 *     The optional capacity bound constructor argument serves as a
 *     way to prevent excessive queue expansion. The capacity, if unspecified,
 *     is equal to {@link Integer#MAX_VALUE}. Linked nodes are
 *     dynamically created upon each insertion unless this would bring the
 *     queue above capacity
 * </p>
 *
 * <p>
 *     This class and its iterator implement all of the
 *     <em>optional</em> methods of the {@link Collection} and {@link
 *     Iterator} interfaces
 * </p>
 *
 * <p>
 *     This class is a memeber of the Java Collections Framework
 * </p>
 *
 * Created by xjk on 1/28/17.
 */
public class KLinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {

    private static final long serialVersionUID = -6903933977591709194L;

    /** Linked list node class */
    static class Node<E>{
        E item;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head.next
         * - null, meaning there is no successor (this is the last node)
         */
        Node<E> next;
        Node(E x){
            item = x;
        }
    }

    /** The capacity bound, or Integer.MAX_VALUE if none */
    private final int capacity;

    /** Current number of elements */
    private final AtomicInteger count = new AtomicInteger();

    /**
     * Head of linked list
     * Invariant: head.item == null
     */
    transient Node<E> head;

    /**
     * Tail of linked list
     * Invariant: last.next == null
     */
    private transient Node<E> last;

    /** Lock held by take, poll, etc */
    private final ReentrantLock takeLock = new ReentrantLock();

    /** Wait queue for waiting takes */
    private final Condition notEmpty = takeLock.newCondition();

    /** Lock held by put, offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();

    /** Wait queue for waiting puts */
    private final Condition notFull = putLock.newCondition();

    /**
     * Signals a waiting take. Called only from put/offer (which do not
     * otherwise ordinarily lock takeLock.)
     */
    private void signalNotEmpty(){
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        }finally {
            takeLock.unlock();
        }
    }

    /** Signal a waiting put. Called only from take/poll */
    private void signalNotFull(){
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        }finally {
            putLock.unlock();
        }
    }

    /**
     * Links node at end of queue
     *
     * @param node the node
     */
    private void enqueue(Node<E> node){
        // assert putLock.isHeldByCurrentThread()
        // assert last.next == null
        last = last.next = node;
    }


    /**
     * Removes a node from head of queue
     *
     * @return the node
     */
    private E dequeue(){
        // assert takeLock.isHeldByCurrentThread();
        // assert head.item == null;
        Node<E> h = head;
        Node<E> first = h.next;
        h.next = h; // help GC
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }


    /**
     * Locks to prevent both puts and takes
     */
    void fullyLock(){
        putLock.lock();
        takeLock.lock();
    }

    /**
     * Unlocks to allow both puts and takes
     */
    void fullyUnlock(){
        takeLock.unlock();
        putLock.unlock();
    }

    /**
    // Tells whether both locks are held by current thread
    boolean isFullyLocked(){
        return (putLock.isHeldByCurrentThread() &&
                takeLock.isHeldByCurrentThread());
    }*/

    /**
     * Creates a {@code KLinkedBlockingQueue} with a capacity of
     * {@link Integer#MAX_VALUE}
     */
    public KLinkedBlockingQueue(){
        this(Integer.MAX_VALUE);
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    /**
     * Creates a {@code KLinkedBlockingQueue} with the given (fixed) capacity
     *
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if {@code capacity} is not greater
     *                                  than zero
     */
    public KLinkedBlockingQueue(int capacity){
        if(capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        last = head = new Node<E>(null);
    }

    /**
     * Creates a {@code KLinkedBlockingQueue} with a capacity of
     * {@link Integer#MAX_VALUE}, initially containing the elements of the
     * given collection
     * added in traversal order of the collection's iterator
     *
     * @param c the collection of elements to initially contain
     * @throws NullPointerException if the specified collection or any
     *                  of its elements are null
     */
    public KLinkedBlockingQueue(Collection<? extends E> c){
        this(Integer.MAX_VALUE);
        final ReentrantLock putLock = this.putLock;
        putLock.lock(); // Never contended, but necessary for visibility
        try {
            int n = 0;
            for(E e : c){
                if(e == null){
                    throw new NullPointerException();
                }
                if(n == capacity){
                    throw new IllegalStateException(" Queue full ");
                }
                enqueue(new Node<E>(e));
                ++n;
            }
            count.set(n);
        }finally {
            putLock.unlock();
        }
    }


    /**
     * This doc comment is overridden to remove the reference to collections
     * greater in size than Integer.MAX_VALUE.
     * Returns the number of elements in this queue.
     *
     * @return the number of the elements in this queue.
     */
    public int size(){
        return count.get();
    }

    /**
     * this doc comment is a modified copy of the inherited doc comment,
     * without the reference to unlimited queues
     *
     * Returns the number of additional elements that this queue can ideally
     * (in the absence of memory or resource constraints) accept without
     * blocking. This is always equal to the initial capacity of this queue
     * less the current {@code size} of this queue
     *
     * <p>
     *     Note that you <em>can not</em> always tell if an attempt to insert
     *     an element will succeed by inspecting {@code remainingCapacity}
     *     because it may be the case that another thread is about to
     *     insert or remove an element
     * </p>
     *
     * @return
     */
    public int remainingCapacity(){
        return capacity - count.get();
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
     * Inserts the specified element at the tail of this queue, waiting if
     * necessary for space to become available
     *
     * @param e
     * @throws InterruptedException
     */
    public void put(E e) throws InterruptedException{
        if(e == null) throw new NullPointerException();
        // Note: convention in all put/take/etc is to preset local var
        // holding count negativeto indicate failure unless set.
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLocK = this.putLock;
        final AtomicInteger count = this.count;
        putLocK.lockInterruptibly();
        try {
            /**
             * Note that count is used in wait guard even though it is
             * not protected by lock. This works because count can
             * only decrease at this point (all other puts are shut
             * out by lock), and we (or some other waiting put) are
             * signalled if it ever changes from capacity. Similarly
             * for all other uses of count in other wait guards
             */

            while(count.get() == capacity){
                notFull.await();
            }
            enqueue(node);
            c = count.getAndIncrement();
            if(c + 1 < capacity){
                notFull.signal();
            }

        }finally {
            putLock.unlock();
        }
        if(c == 0){
            signalNotEmpty();
        }
    }


    /**
     * Inserts the specified element at the tail of this queue, waiting if
     * necessary up to the specified wait time for space to become available
     *
     * @param e
     * @param timeout
     * @param unit
     * @return {@code true} if successful, or {@code false} if
     *          the specified waiting time elapses before space is available
     * @throws InterruptedException
     */
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException{
        if(e == null) throw new NullPointerException();
        long nanos = unit.toNanos(timeout);
        int c = -1;
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            while(count.get() == capacity){
                if(nanos <= 0){
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(new Node<E>(e));
            c = count.getAndIncrement();
            if(c + 1 < capacity){
                notFull.signal();
            }
        }finally {
            putLock.unlock();
        }
        if(c == 0){
            signalNotEmpty();
        }
        return true;
    }

    /**
     * Inserts the specified element at the tail of this queue if it is
     * possible to do so immediately without exceeding the queue's capacity
     * returning {@code true} upon success and {@code false} if this queue
     * is full.
     * When using a capacity-restricted queue, this method is generally
     * preferable to method {@link BlockingQueue#add(Object)} which can fail to
     * insert an element only by throwing an exception
     *
     * @param e
     * @return
     */
    public boolean offer(E e){
        if(e == null) throw new NullPointerException();
        final AtomicInteger count = this.count;
        if(count.get() == capacity){
            return false;
        }
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            if(count.get() < capacity){
                enqueue(node);
                c = count.getAndIncrement();
                if(c + 1 < capacity){
                    notFull.signal();
                }
            }
        }finally {
            putLock.unlock();
        }
        if(c == 0){
            signalNotEmpty();
        }
        return c >= 0;
    }





    public E take() throws InterruptedException{
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try {
            while(count.get() == 0){
                notEmpty.await();
            }
            x = dequeue();
            c = count.getAndDecrement();
            if(c > 1){
                notEmpty.signal();
            }
        }finally {
            takeLock.unlock();
        }
        if(c == capacity){
            signalNotFull();
        }
        return x;
    }


    public E poll(long timeout, TimeUnit unit) throws InterruptedException{
        E x = null;
        int c = -1;
        long nanos = unit.toNanos(timeout);
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();
        try{
            while(count.get() == 0){
                if(nanos <= 0){
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            x = dequeue();
            c = count.getAndDecrement();
            if(c > 1){
                notEmpty.signal();
            }
        }finally {
            takeLock.unlock();
        }
        if(c == capacity){
            signalNotFull();
        }
        return x;
    }



    public E poll(){
        final AtomicInteger count = this.count;
        if(count.get() == 0){
            return null;
        }
        E x = null;
        int c = -1;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            if(count.get() > 0){
                x = dequeue();
                c = count.getAndDecrement();
                if(c > 1){
                    notEmpty.signal();
                }
            }
        }finally {
            takeLock.unlock();
        }
        if(c == capacity){
            signalNotFull();
        }
        return x;
    }

    public E peek(){
        if(count.get() == 0) return null;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            Node<E> first = head.next;
            if(first == null){
                return null;
            }
            else{
                return first.item;
            }
        }finally {
            takeLock.unlock();
        }
    }


    /** Unlinks interior Node p with predecessor trail */
    void unlink(Node<E> p, Node<E> trail){
        // assert isFullLocked();
        // p.next is not changed, to allow iterators that are
        // traversing p to maintain their weak-consistency guarantee
        p.item = null;
        trail.next = p.next;
        if(last == p){
            last = trail;
        }
        if(count.getAndDecrement() == capacity){
            notFull.signal();
        }
    }

}
