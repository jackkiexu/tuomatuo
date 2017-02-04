package com.lami.tuomatuo.search.base.concurrent.arrayblockingqueue;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
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



    /************************************ Internal helper methods *************************/
    /**
     * Circularly decreament i
     * @param i
     * @return
     */
    final int dec(int i){
        return ((i == 0)? items.length : i) - 1;
    }

    /**
     * Returns item at index i
     * @param i
     * @return
     */
    final E itemAt(int i){
        return (E)items[i];
    }

    /**
     * Throws NullPointerException if argument is null
     * @param v
     */
    private static void checkNotNull(Object v){
        if(v == null){
            throw new NullPointerException();
        }
    }

    /**
     * Inserts element at current put position, advances, and signals
     * Call only when holding lock
     * @param x
     */
    private void enqueue(E x){
        // assert lock.getHoldCount() == 1;
        // assert items[putIndex] == null
        final Object[] items = this.items;
        items[putIndex] = x;
        if(++putIndex == items.length){
            putIndex = 0;
        }
        count++;
        notEmpty.signal();
    }

    /**
     * Extracts element at current take position, advances, and signals
     * Call only when holding lock
     * @return
     */
    private E dequeue(){
        // assert lock.getHoldCount() == 1
        // assert items[takeIndex] != null
        final Object[] items = this.items;
        E x = (E)items[takeIndex];
        items[takeIndex] = null;
        if(++takeIndex == items.length){
            takeIndex = 0;
        }
        count--;
        if(itrs != null){
            itrs.elementDequeued();
        }
        notFull.signal();
        return x;
    }


    /**
     * Delete items at array index removeIndex
     * Utility for remove(Object) and iterator.remove
     * Call only when holding lock
     *
     * @param removeIndex
     */
    void removeAt(final int removeIndex){
        // assert lock.getHoldCount() == 1;
        // assert items[removeIndex] != null
        // assert removeIndex >= 0 && removeIndex < items.length
        final Object[] items = this.items;
        if(removeIndex == takeIndex){
            // rmeoing fornt item: just advance
            items[takeIndex] = null;
            if(++takeIndex == items.length){
                takeIndex = 0;
            }
            count--;
            if(itrs != null){
                itrs.elementDequeued();
            }
        }
        else {
            // an "interior" remove
            // slide over all others up through putIndex
            final int putIndex = this.putIndex;
            for(int i = removeIndex;;){
                int next = i + 1;
                if(next == items.length){
                    next = 0;
                }
                if(next != putIndex){
                    items[i] = items[next];
                    i = next;
                }else{
                    items[i] = null;
                    this.putIndex = i;
                    break;
                }
            }

            count--;
            if(itrs != null){
                itrs.removeAt(removeIndex);
            }
        }
        notFull.signal();
    }

    /**
     * Creates an {@code KArrayBlockingQueue} with the given (fixed)
     * capacity and default access policy
     *
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if {@code capacity < 1}
     */
    public KArrayBlockingQueue(int capacity) {
        this(capacity, false);
    }

    /**
     * Creates an {@code KArrayBlockingQueue} with the given (fixed)
     * capacity and the specified access policy
     *
     * @param capacity the capacity of this queue
     * @param fair if {@code true} then queue accesses for threads blocked
     *             on insertion pr removal, are processed in FIFO order;
     *             if {@code false} the access order is unspecified
     * @throws IllegalArgumentException if {@code capacity < 1}
     */
    public KArrayBlockingQueue(int capacity, boolean fair) {
        if(capacity < 0){
            throw new IllegalArgumentException();
        }
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    /**
     * Creates an {@code KArrayBlockingQueue} with the given (fixed)
     * capacity, the specified access policy and initially containing the
     * elements of the given collection
     * added in traversal order of the collection's iterator
     *
     * @param capacity the capacity of this queue
     * @param fair if {@code true} then queue accesses for threads blocked
     *             on insertion or removal, are processed in FIFO order
     *             if {@code false} the access order is unspecified
     * @param c the collection of elements to initially contain
     * @throws IllegalArgumentException if {@code capacity} is less than
     *              {@code c.size()}, or less than 1
     * @throws NullPointerException if the specified collection or any
     *              of its elements are null
     */
    public KArrayBlockingQueue(int capacity, boolean fair, Collection<? extends E> c) {
        this(capacity, fair);

        final ReentrantLock lock = this.lock;
        lock.lock(); // Lock only for visibility, not mutual exclusion
        try{
            int i = 0;
            try{
                for(E e : c){
                    checkNotNull(e);
                    items[i++] = e;
                }
            }catch (Exception e){
                throw new IllegalArgumentException(e);
            }
            count = i;
            putIndex = (i == capacity)? 0: i;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the tail of this queue if it is
     * possible to do som immediately without exceeding the queue's capacity
     * returning {@code true} upon success and throwing an
     * {@code IllegalStateException} if this queue is full
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Collection#add(Object)})
     * @throws IllegalStateException if the queue is full
     * @throws NullPointerException if the specified element is null
     */
    public boolean add(E e){
        return super.add(e);
    }


    /**
     * Insert the specified element at the tail of this queue if it is
     * possible to do so immediately without exceeding the queue's capacity
     * returning {@code true} upon success and {@code false} if this queue
     * is full. This method is genegrally preferable to method {@link #add}
     * which can fail to insert an element only by throwing ac exception
     *
     * @throws NullPointerException if the specified element is null
     * @return
     */
    public boolean offer(E e){
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            if(count == items.length){
                return false;
            }
            else{
                enqueue(e);
                return true;
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the tail of this queue, waiting
     * for space to become available if the queue is full
     *
     * @param e
     * @throws InterruptedException
     */
    public void put(E e) throws InterruptedException{
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try{
            while(count == items.length){
                notFull.await();
            }
            enqueue(e);
        }finally {
            lock.unlock();
        }
    }


    /**
     * Inserts the specified element at the tail of this queue, waiting
     * up to the specified wait for space to becomeavilable if
     * the queue is full
     *
     * @param e
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException{
        checkNotNull(e);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();

        try{
            while(count == items.length){
                if(nanos <= 0){
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(e);
            return true;
        }finally {
            lock.unlock();
        }
    }

    public E poll(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (count == 0)? null : dequeue();
        }finally {
            lock.unlock();
        }
    }

    public E take() throws InterruptedException{
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while(count == 0){
                notEmpty.await();
            }
            return dequeue();
        }finally {
            lock.unlock();
        }
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException{
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try{
            while(count == 0){
                if(nanos <= 0) return  null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            return dequeue();
        }finally {
            lock.unlock();
        }
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
    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return itemAt(takeIndex); // null when queue is empty
        }finally {
            lock.unlock();
        }
    }


    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return count;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Shared data between iterators and their queue, allowing queue
     * modifications to update iterators when elements are removed
     *
     * This adds a lot of complexity for the sake of correctly
     * handling some uncommon operations, but the combination of
     * circular-arrays and supporting iterior removes (i.e, those
     * not at head) would cause iterators to sometimes lose their
     * places and/or (rereport elements they shouldn't. To avoid
     * this, when a queue has one or more iterators, it keeps iterator
     * state consistent by:
     *
     * (1) keeping track of the number of "cycles", that is, the number of time takeIndex has wrapped around to 0.
     * (2) notifying all iterators via the callback removeAt whenever
     *      an iterator element is removed (and thus other elements may
     *      be shifted)
     *
     * These suffice to eliminate iterator inconsistencies, but
     * unfortunately add the secondary responsibility of maintaining
     * the list of iterators. We track all active iterators in a
     * simple linked list (accessed only when the queue's lock is
     * held) of weak references to Itr. The list is cleaned up using
     * 3 different mechanisms:
     *
     *  (1) Whenever a new iterator is created, do some 0(1) checking for
     *          stale list elements
     *  (2) Whenever takeIndex wraps around to 0, check for iterators
     *          that have been unused for more than one wrap-around cycle
     *  (3) Whenever the queue becomes mepty, all iterators are nbotified
     *          and this entire data structure is discarded
     *
     *
     *  So in addition to the removeAt callback that is necessary for
     *  correctness, iterators have the shutdown and takeIndex wrapped
     *  callbacks that help remove stale iterators from the list
     *
     *  Whenever a list element is examinated, it is expunged if either
     *  the GC has determined that the iterator is discarded, or if the
     *  iterator reports that it is "detached" (does not need any
     *  further state updates). Overhead is maximal when takeIndex
     *  never advances, iterators are discarded before they are
     *  exhausted, and all removals are interior removes, in which case
     *  all stale iterators are discovered by the GC. But even in this
     *  case we don't increase the amortized complexity
     *
     *  Care must be taken to keep list sweeping methods from
     *  reentrantly invoking another such method, causing subtle
     *  corruption bugs
     */
    class Itrs {

        /** Node in a linked list of weak iterator references */
        private class Node extends WeakReference<Itr> {
            Node next;

            public Node(Itr referent, Node next) {
                super(referent);
                this.next = next;
            }
        }

        /** Incremented whenever takeIndex wraps around to 0 */
        int cycles = 0;

        /** Linked list of weak iterator references */
        private Node head;

        /** Used to expunge stale iterators */
        private Node sweeper = null;

        private static final int SHORT_SWEEP_PROBES = 4;
        private static final int LONG_SWEEP_PROBES = 16;

        public Itrs(Itr initial) {
            register(initial);
        }

        /**
         * Sweeps itrs, looking for and expunging stale iterators
         * If at least one was found, tries harder to find more
         * Called only from iterating thread
         *
         * @param tryHarder whether to start in try-harder mode, because
         *                  there is known to be at least one iterator to collect
         */
        void doSomeSweeping(boolean tryHarder){
            /**
             * assert lock.getHoldCount() == 1;
             * assert head != null
             */
            int probes = tryHarder ? LONG_SWEEP_PROBES : SHORT_SWEEP_PROBES;
            Node o, p;
            final Node sweeper = this.sweeper;
            boolean passedGo; // to limit search to one full sweep

            if(sweeper == null){
                o = null;
                p = head;
                passedGo = true;
            }
            else {
                o = sweeper;
                p = o.next;
                passedGo = false;
            }

            for(;probes > 0; probes--){
                if(p == null){
                    if(passedGo){
                        break;
                    }
                    o = null;
                    p = head;
                    passedGo = true;
                }
                final Itr it = p.get();
                final Node next = p.next;
                if(it == null || it.isDetached()){
                    // found a discarded /exhausted iterator
                    probes = LONG_SWEEP_PROBES; // "try harder"
                    // unlink p
                    p.clear();
                    p.next = null;
                    if(o == null){
                        head = next;
                        if(next == null){
                            // We've run out of iterators to track retire
                            itrs = null;
                            return;
                        }
                    }
                    else {
                        o.next = next;
                    }
                }else{
                    o = p;
                }
                p = next;
            }

            this.sweeper = (p == null) ? null : o;
        }

        /**
         * Adds a new iterator to the linked list of tracked iterators
         * @param itr
         */
        void register(Itr itr){
            // assert lock.getHoldCount() == 1
            head = new Node(itr, head);
        }

        /**
         * Called whenever takeIndex wraps around to 0
         * Notifies all iterators, and expunges any that are now stale
         */
        void takeIndexWrapped(){
            // assert lock.getHoldCount() == 1
            cycles++;
            for(Node o = null, p = head; p != null;){
                final Itr it = p.get();
                final Node next = p.next;
                if(it == null || it.takeIndexWrapped()){
                    // unlink p
                    // assert it == null || it.isDetached
                    p.clear();
                    p.next = null;
                    if(o == null){
                        head = next;
                    }else{
                        o.next = next;
                    }
                }else{
                    o = p;
                }
                p = next;
            }
            if(head == null){ // no more iterators to track
                itrs = null;
            }
        }

        /**
         * Called whenever an iterior remove (not at takeIndex) occured
         * Notifies all iterator, and expunges any that are now stale
         *
         * @param removeIndex
         */
        void removeAt(int removeIndex){
            for(Node o = null, p = head; p != null;){
                final Itr it = p.get();
                final Node next = p.next;
                if(it == null || it.removeAt(removeIndex)){
                    // unlink p
                    // assert it == null || it.isDetached
                    p.clear();
                    p.next = null;
                    if(o == null){
                        head = next;
                    }else{
                        o.next = next;
                    }
                }else{
                    o = p;
                }
                p = next;
            }
            if(head == null){ // no more iterators to track
                itrs = null;
            }
        }

        /**
         * Called whenever the queue becomes empty
         *
         * Notifies all active iterators that the queue is empty
         * clears all weak refs, and unlinks the itrs data structure
         */
        void queueIsEmpty(){
            // assert lock.getHoldCount() == 1
            for(Node p = head; p != null; p = p.next){
                Itr it = p.get();
                if(it != null){
                    p.clear();
                    it.shutdown();
                }
            }
            head = null;
            itrs = null;
        }


        /**
         * Called whenever an element has been dequeued (at takeIndex)
         */
        void elementDequeued(){
            // assert lock.getHoldCount() == 1
            if(count == 0){
                queueIsEmpty();
            }
            else if(takeIndex == 0){
                takeIndexWrapped();
            }
        }
    }


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

        /**
         * Called whenever an interior remove (not at takeIndex) occured
         * @param removedIndex
         * @return true if this iterator should be unlinked from itrs
         */
        boolean removeAt(int removedIndex){
            // assert lock.getHoldCount() == 1
            if(isDetached()){
                return true;
            }

            final int cycles = itrs.cycles;
            final int takeIndex = KArrayBlockingQueue.this.takeIndex;
            final int prevCycles = this.prevCycles;
            final int prevTakeIndex = this.prevTakeIndex;
            final int len = items.length;
            int cycleDiff = cycles - prevCycles;
            if(removedIndex < takeIndex){
                cycleDiff++;
            }
            final int removeDistance =
                    (cycleDiff * len) + (removedIndex - prevTakeIndex);
            // assert removeDistance >= 0
            int cursor = this.cursor;
            if(cursor >= 0){
                int x = distance(cursor, prevTakeIndex, len);
                if(x == removeDistance){
                    if(cursor == putIndex){
                        this.cursor = cursor = NONE;
                    }
                }
                else if(x > removeDistance){
                    // assert cursor != prevTakeIndex
                    this.cursor = cursor = dec(cursor);
                }
            }

            int lastRet = this.lastRet;
            if(lastRet >= 0){
                int x = distance(lastRet, prevTakeIndex, len);
                if(x == removeDistance){
                    this.lastRet = lastRet = REMOVED;
                }
                else if(x > removeDistance){
                    this.lastRet = lastRet = dec(lastRet);
                }
            }

            int nextIndex = this.nextIndex;
            if(nextIndex >= 0){
                int x = distance(nextIndex, prevTakeIndex, len);
                if(x == removeDistance){
                    this.nextIndex = nextIndex = REMOVED;
                }else if(x > removeDistance){
                    this.nextIndex = nextIndex = dec(nextIndex);
                }
            }
            else if(cursor < 0 && nextIndex < 0 && lastRet < 0){
                this.prevTakeIndex = DETACHED;
                return true;
            }
            return false;
        }

        /**
         * Called whenever takeIndex wraps around to zero
         *
         * @return true if this iterator should be unlinked from itrs
         */
        boolean takeIndexWrapped(){
            // assert lock.getHoldCount() == 1
            if(isDetached()){
                return true;
            }
            if(itrs.cycles - prevCycles > 1){
                /**
                 * All the elements that exist at the time of the last
                 * operation are gone, so abandon further iteration
                 */
                shutdown();
                return true;
            }
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
