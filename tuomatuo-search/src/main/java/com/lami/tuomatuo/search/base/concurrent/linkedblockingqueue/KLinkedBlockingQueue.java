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
    /**
     * Linked 的数据节点, 这里有个特点, LinkedBlockingQueue 开始构建时会创建一个dummy节点(类似于 ConcurrentLinkedQueue)
     * 而整个队列里面的头节点都是 dummy 节点
     * @param <E>
     */
    static class Node<E>{
        E item;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head.next
         * - null, meaning there is no successor (this is the last node)
         */
        /**
         * 在进行 poll 时 会将 node.next = node 来进行 help gc
         * next == null 指的是要么是队列的 tail 节点
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
     * Head 节点的不变性 head.item == null <- 这是一个 dummy 节点(dummy 节点什么作用呢, 主要是方便代码, 不然的话需要处理一些 if 的判断, 加大代码的复杂度, 尤其是非阻塞的实现)
     */
    transient Node<E> head;

    /**
     * Tail of linked list
     * Invariant: last.next == null
     * Tail 节点的不变性 last.next == null <- 尾节点的 next 是 null
     */
    private transient Node<E> last;

    /** ReentrantLock Condition 的常见使用方式 */
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
     * 节点 入队列 (PS: 这里因为有个 dummy 节点, 不需要判空 <- 现在有点感觉 dummy 节点的作用了吧)
     * @param node the node
     */
    private void enqueue(Node<E> node){
        // assert putLock.isHeldByCurrentThread()
        // assert last.next == null
        last = last.next = node;
    }


    /**
     * Removes a node from head of queue
     * 节点出队列 这里有个注意点 head 永远是 dummy 节点, dequeue 的值是 head.next.item 的值
     * 在 dequeue 后 将 原  head 的后继节点设置为 head(成为了 dummy 节点)
     * @return the node
     */
    private E dequeue(){
        // assert takeLock.isHeldByCurrentThread();
        // assert head.item == null;
        Node<E> h = head;       // 这里的 head 是一个 dummy 节点
        Node<E> first = h.next; // 获取真正的节点
        h.next = h;             // help GC
        head = first;           // 重行赋值 head
        E x = first.item;       // 获取 dequeue 的值
        first.item = null;      // 将 item 置 空
        return x;
    }


    /**
     * Locks to prevent both puts and takes
     * 获取  put/take 锁 (在 remove 时需要)
     */
    void fullyLock(){
        putLock.lock();
        takeLock.lock();
    }

    /**
     * Unlocks to allow both puts and takes
     * 释放 take/put 锁
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


    @Override
    public Iterator<E> iterator() {
        return null;
    }

    /**
     * Creates a {@code LinkedBlockingQueue} with a capacity of
     * {@link Integer#MAX_VALUE}.
     */
    public KLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
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
        this.capacity = capacity; // 指定 queue 的容量
        last = head = new Node<E>(null); // 默认的在 queue 里面 创建 一个 dummy 节点
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
     *  将元素加入到 queue 的尾部
     * @param e
     * @throws InterruptedException
     */
    public void put(E e) throws InterruptedException{
        if(e == null) throw new NullPointerException();
        // Note: convention in all put/take/etc is to preset local var
        // holding count negativeto indicate failure unless set.
        // 有趣的 变量 c 下面会有对它的讲解
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLocK = this.putLock;
        final AtomicInteger count = this.count;  // 获取 queue 的数量 count (这是 count 只可能 减, 不能增)
        putLocK.lockInterruptibly(); // 获取 put 的lock
        try {
            /**
             * Note that count is used in wait guard even though it is
             * not protected by lock. This works because count can
             * only decrease at this point (all other puts are shut
             * out by lock), and we (or some other waiting put) are
             * signalled if it ever changes from capacity. Similarly
             * for all other uses of count in other wait guards
             */
            /**
             * 若 queue 的容量满了 则进行 await,直到有人进行通知
             * 那何时进行通知呢?
             * 有两种情况进行通知,
             *      (1) 有线程进行 put/offer 成功后且 (c + 1) < capacity 时
             *      (2) 在线程进行 take/poll 成功 且 (c == capacity) (PS: 这里的 c 指的是 在进行 take/poll 之前的容量)
             */

            while(count.get() == capacity){     // 容量满了, 进行等待
                notFull.await();
            }
            enqueue(node);                        // 进行节点的入队操作
            c = count.getAndIncrement();          // 进行节点个数的增加1, 返回原来的值
            if(c + 1 < capacity){               // 说明 现在的 put 操作后 queue 还没满
                notFull.signal();               // 唤醒其他在睡的线程
            }

        }finally {
            putLock.unlock();                   // 释放锁
        }
        if(c == 0){                             // c == 0 说明 原来queue是空的, 所以这里 signalNotEmpty 一下, 唤醒正在 poll/take 等待中的线程
            signalNotEmpty();
        }
    }


    /**
     * Inserts the specified element at the tail of this queue, waiting if
     * necessary up to the specified wait time for space to become available
     *
     *  支持中断和超时的 offer 节点
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
        final ReentrantLock putLock = this.putLock;     // 获取 put lock
        final AtomicInteger count = this.count;         // 获取 queue 的容量
        putLock.lockInterruptibly();
        try {
            while(count.get() == capacity){             // queue的容量满了进行 带 timeout 的 await
                if(nanos <= 0){                           //  用光了 timeout 直接 return false
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);      // 直接 await (PS: 返回值 nanos <= 0 说明 等待是超时了, 正常 await 并且 被 signal nanos > 0; 具体详情会在 Condition 那一篇中详细说明)
            }
            enqueue(new Node<E>(e));                    // 节点若队列
            c = count.getAndIncrement();                // 获取入队列之前的容量
            if(c + 1 < capacity){                     // c + 1 < capacity 说明 现在的 offer 成功后 queue 还没满
                notFull.signal();                     // 唤醒其他正在 await 的线程
            }
        }finally {
            putLock.unlock();                           // 释放锁
        }
        if(c == 0){
            signalNotEmpty();                            // c == 0 说明 原来queue是空的, 所以这里 signalNotEmpty 一下, 唤醒正在 poll/take 等待中的线程
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
     * 插入元素到 queue 的尾部
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

    /**
     * 取走 queue 中呆着时间最长的节点的 item (其实就是 head.next.item 的值)
     * @return
     * @throws InterruptedException
     */
    public E take() throws InterruptedException{
        E x;
        int c = -1;
        final AtomicInteger count = this.count;          // 获取 queue 的容量
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();                      // 获取 lock
        try {
            while(count.get() == 0){                      // queue 为空, 进行 await
                notEmpty.await();
            }
            x = dequeue();                                 // 将 head.next.item 的值取出, head = head.next
            c = count.getAndDecrement();                   // queue 的容量计数减一
            if(c > 1){
                notEmpty.signal();                        // c > 1 说明 进行 take 后 queue 还有值
            }
        }finally {
            takeLock.unlock();                              // 释放 lock
        }
        if(c == capacity){                                // c == capacity 说明一开始 queue 是满的, 调用 signalNotFull 进行唤醒一下 put/offer 的线程
            signalNotFull();
        }
        return x;
    }

    /**
     * 带 timeout 的poll 操作, 获取 head.next.item 的值
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public E poll(long timeout, TimeUnit unit) throws InterruptedException{
        E x = null;
        int c = -1;
        long nanos = unit.toNanos(timeout);             //  计算超时时间
        final AtomicInteger count = this.count;       // 获取 queue 的容量
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lockInterruptibly();                   // 获取 lock
        try{
            while(count.get() == 0){                   // queue 为空, 进行 await
                if(nanos <= 0){                        // timeout 用光了, 直接 return null
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);   // 调用 condition 进行 await, 在 timeout之内进行 signal -> nanos> 0
            }
            x = dequeue();                             // 节点出queue
            c = count.getAndDecrement();               // 计算器减一
            if(c > 1){                                 // c > 1 说明 poll 后 容器内还有元素, 进行 换新 await 的线程
                notEmpty.signal();
            }
        }finally {
            takeLock.unlock();                         // 释放锁
        }
        if(c == capacity){                           // c == capacity 说明一开始 queue 是满的, 调用 signalNotFull 进行唤醒一下 put/offer 的线程
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
    /**
     * 直接将这个方法看做是 将 节点 p 从 queue 中进行删除
     * @param p
     * @param trail
     */
    void unlink(Node<E> p, Node<E> trail){
        // assert isFullLocked();
        // p.next is not changed, to allow iterators that are
        // traversing p to maintain their weak-consistency guarantee
        p.item = null;                      // 删除 p.item
        trail.next = p.next;                // 删除节点 p
        if(last == p){                      // 若节点p 是last, 则将p的前继节点trail置为 last
            last = trail;
        }
        if(count.getAndDecrement() == capacity){    // count.getAndDecrement() == capacity 说明 queue 在删除节点之前是满的, 所以唤醒一下在 put/offer 的线程
            notFull.signal();
        }
    }

    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present. More formally, removes an element {@code e} such
     * that {@code o.equals(e)}, if this queue contains one or more such
     * elements
     * Returns {@code true} if this queue contained the specified element
     * (or equivalently, if this queue changed as a result of the call)
     *
     * 删除 queue 中的节点
     *
     * @param o element to be removed from this queue, if present
     * @return {@code true} if this queue changed as a result of the call
     */
    public boolean remove(Object o){
        if(o == null) return false;
        fullyLock();                                         // 获取所有锁

        try {
            for(Node<E> trail = head, p = trail.next;     // 进行变量的初始化 trail是 p 的前继节点
                    p != null;
                    trail = p, p = p.next){
                if(o.equals(p.item)){
                    unlink(p, trail);                      // 调用 unlink 进行删除
                    return true;
                }
            }
            return false;
        }finally {
            fullyUnlock();                                  // 释放所有锁
        }
    }


}
