package com.lami.tuomatuo.search.base.concurrent.synchronousqueue;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 参考资料
 * http://www.cnblogs.com/leesf456/p/5560362.html
 * http://www.cnblogs.com/wanly3643/p/3904681.html
 * http://vickyqi.com/2015/11/30/JDK%E5%B9%B6%E5%8F%91%E5%B7%A5%E5%85%B7%E7%B1%BB%E6%BA%90%E7%A0%81%E5%AD%A6%E4%B9%A0%E7%B3%BB%E5%88%97%E2%80%94%E2%80%94SynchronousQueue/
 *
 *
 *http://www.infoq.com/cn/articles/java-blocking-queue/
 * 阻塞队列提供的四种处理方式
 *  方法/处理方式       抛出异常        返回特殊值       一直阻塞        超时退出
 *      插入方法         add(e)          offer(e)         put(e)          offer(e, time, TimeUnit)
 *      移除方法         remove()        poll()           take()          poll(time, TimeUnit)
 *      检查方法         element()       peek()           不可用          不可用
 *
 * <a href="https://www.cs.rochester.edu/u/scott/papers/2009_Scherer_CACM_SSQ.pdf">
 *      Scalable Synchronous Queues
 * </a>
 *
 * A {@linkplain BlockingQueue blocking queue} in which each insert
 * operation must wait for a corresponding remove operation by another
 * thread, and vice versa. A synchronous queue does not have any
 * internal capacity, not even a capacity of one. You cannot
 * {@code peek} at at a synchronous queue because an element is only
 * present when you try to remove it; you cannot insert an element
 * (using any method) unless another thread is trying to remove it;
 * you cannot iterate as there is nothing to iterate. The
 * <<em>head</em> of the queue is the element that the first queued
 * inserting thread is trying to add to the queue; if there is no such
 * queued thread then no element is available for removal and
 * {@code poll()} will return {@code null}. For purposes of other
 * {@Collection} methods (for example {@code contains}), a
 * {@code SynchronousQueue} acts as an empty collection. This queue
 * does not permit {@code null} elements
 *
 * <p>
 *      Synchronous queues are similar to rendezvous(约会) channels used in
 *      CSP and Ada. They are well suited for handoff(传递) designs, in which an
 *      object running in one thread must sync up with an object running
 *      in another thread in order to hand it some information, event, or
 *      task
 * </p>
 *
 * <p>
 *     This class supports an optional fairness policy for ordering
 *     waiting producer and consumer threads. By default, this ordering
 *     is not guaranteed. However, a queue constructed with fairness set
 *     to {@code true} grants threads access in FIFO order.
 * </p>
 *
 * <p>
 *     This class and its iterator implement all of the
 *     <em>optional</em> methods of the {@link java.util.Collection} and {@link
 *     java.util.Iterator} interface
 * </p>
 *
 * <p>
 *     This class is a member of the
 *     <a href="{@docRoot}/../technotes/guides/collections/index.html"></a>
 * </p>
 *
 * Created by xjk on 12/25/16.
 */
public class KSynchronousQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {

    private static final Logger logger = Logger.getLogger(KSynchronousQueue.class);

    private static final long serialVersionUID = -2835992386168676443L;

    /**
     * This class implements extensions of the the dual stack and dual
     * queue algotithms described in "Non-blocking Concurrent Objects
     * with Condition Synchronization", by W. N. Scherer III and
     * M. L. Scott. 18th Annual Conf. on Distributed Computing.
     * Oct. 2004 (see also
     * http://www.cs.rochester.edu/u/scott/synchronization/pseudocode/duals.html).
     * The (Lifo) stack is used for non-fair mode, and the (Fifo)
     * queue for fair mode. The performance of the two is generally
     * similar, Fifo usually supports higher throughput under
     * contention but Lifo maintains higher thread locality in common
     * applications.
     *
     * A dual queue (and similarly stack) is one that at any given
     * time either holds "data" -- items provided by put operations,
     * or "request" -- slots representing take operations, or is
     * empty. A call to "fullfill" (i.e., a call requesting an item
     * from a queue holding data or vice versa) dequeues a
     * complementary node. The most interesting feature of these
     * queues is that any operation can figure out which mode the
     * queue is in, and act according without needing locks.
     *
     * Both the queue and stack extend abstract class Transferer
     * defining the single method transfer that does a put or a
     * take. These are unified into a single method because in dual
     * data structures, the put and take operations are symmetrical,
     * so nearly all code can be combined. The resulting transfer
     * methods are on the long side, but are easiler to follow than
     * they would be if broken up into nearly-duplicated parts
     *
     * The queue and stack data structures share many conceptual
     * similarities but very few concrete details. For simplicity
     * they are kept distinct so that they can later evolve
     * separately
     *
     * The algorithms here differ from the versions in the above paper
     * in extending them for use in synchronous queues, as well as
     * dealing with cancellation. The main differences include:
     *
     *  1. The original algorithms used bit-marked pointers, but
     *  the ones here use mode bits in nodes, leading to a number
     *  of further adaptations.
     *
     *  2. SynchronousQueues must block threads waiting to become
     *  fulfilled.
     *
     *  3. Support for cancellation via timeout and interrupts,
     *  including cleaning out cancelled nodes/threads
     *  from lists to avoid garbage retention(保留) and memory depletion(耗尽).
     *
     * Blocking is mainly accomplished using LockSupport park/unpark,
     * except that nodes that appear to be the next ones to become
     * fulfilled first spin a bit (on multiprocessors only). Only very
     * busy synchronous queues, spining can dramatically improve
     * throughput. And on less busy ones, the amount of spining is
     * small enough not to be noticeable
     *
     * Cleaning is done in different ways in queue vs stacks. For
     * queues, we can almost always remove a node immediately in O(1)
     * time (modulo retries for consistency checks) when it is
     * cancelled. But if it may be pinned as the current tail, it must
     * wait until some subsequent cancellation. For stacks, we need a
     * potentially O(n) traversal to be sure that we can remove the
     * node, but this can run concurrently with other threads
     * accessing the stack
     *
     * While garbage collection takes care of most node reclamation
     * issues that otherwise complicate nonblocking algorithms, care
     * is taken to "forget" references to data, other nodes, and
     * threads. In cases where setting to null would otherwise
     * conflict with main algorithms, this is  done by changing a
     * node's link to now point to the node itself. This doesn't arise
     * much for Stack nodes (because blocked threads do not hang on to
     * old head pointers), but references  in Queue nodes must be
     * aggressively forgotten to avoid reachability of  everything any
     * node has ever referred  to since arrival
     */

    /**
     * Shared internal API for dual stacks and queues.
     * @param <E>
     */
    abstract static class Transferer<E>{
        /**
         * 逻辑
         * 1. 开始时队列肯定是空的
         * 2. 线程进入队列, 如果队列是空的, 那么就添加该线程进入队列, 然后进行等待(要么有匹配的线程出现, 要么就是该请求超时取消)
         * 3. 第二个线程进入, 如果前面一个线程跟它属于不同类型, 也就是说两者可以匹配的, 那么就从队列删除第一个线程
         * 4. 如果相同的线程, 那么做法参照2.
         *
         * 理清了基本的逻辑, 也就是会有两种情况:
         * 1. 队列为空或者队列中等待线程是相同的类型
         * 2. 队列的等待线程是匹配的类型
         *
         * Performs a put or take
         *
         * @param e  if non-null, the item to be handed to a consumer;
         *           if null, requests that transfer return an item
         *           offered by producer.
         * @param timed if this operation should timeout
         * @param nanos the timeout, in nanosecond
         * @return  if non-null, the item provided or received; if null,
         *          the operation failed due to timeout or interrupt --
         *          the caller can distinguish which of these occurred
         *          by checking Thread.interrupted
         */
        abstract E transfer(E e, boolean timed, long nanos);
    }

    /** The number of CPUs, for spin control */
    static final int NCPUS = Runtime.getRuntime().availableProcessors();

    /**
     * The number of times to spin before blocking in timed waits
     * The value is empirically derived == it works well across a
     * variety of processors and OSes. Empirically, the best value
     * seems not to vary with number of CPUs (beyond 2) so is just
     * a constant.
     */
    static final int maxTimeSpins = (NCPUS < 2) ? 0 : 32;

    /**
     * The number of times to spin before blocking in untimed waits.
     * This is greater than time value because untimed waits spin
     * faster since they don't need to check times on each spin
     */
    static final int maxUntimedSpins = maxTimeSpins * 16;

    /**
     * The number of nanoseconds for which it is faster to spin
     * rather than to use timed park, A rough estimate suffices
     */
    static final long spinForTimeoutThreshold = 1000l;

    /** Dual stack */
    static final class TransferStack<E> extends Transferer<E>{

        /** This extends Scherer-scott dual stack algorithm, differing,
         * among other ways, by using "covering" nodes rather than
         * bit-marked pointers: Fulfilling operations push on marker
         * nodes (with FULFILLING bit set in mode) to reserve a spot
         * to match a waiting node
         */

        /** Modes for SNodes, ORed together in node fields */
        /** Node represents an unfulfilled consumer */
        static final int REQUEST    = 0;
        /** Node represents an unfulfilled producer */
        static final int DATA       = 1;
        /** Node is fulfilling another unfulfilled DATA or REQUEST */
        static final int FULFILLING = 2;
        /** Returns true if m has fulfilling bit set. */
        static boolean isFulfilling(int m) {
            boolean result = (m & FULFILLING) != 0;
            logger.info("isFulfilling m : " + m + ", result:"+ result);
            return result;
        }

        /** Node class for TransferStacks */
        static final class SNode{
            volatile SNode next;        // next node in stack
            volatile SNode match;       // the node matched to this
            volatile Thread waiter;     // to control park/unpark
            Object item;                // data; or null for REQUESTs
            int mode;
            // Note: item and mode fields don't need to be volatile
            // since they are always written before, and read after,
            // other volatile/atomic operations.


            public SNode(Object item) {
                this.item = item;
            }

            boolean casNext(SNode cmp, SNode val){
                return cmp == next &&
                        unsafe.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            /**
             * Tries to match node a to this node, if so, waking up thread.
             * Fulfillers call tryMatch to identify their waiters.
             * Waiters block until they have been matched
             *
             * @param s the node to match
             * @return true if successfully matched to s
             */
            boolean tryMatch(SNode s){
                if(match == null &&
                        unsafe.compareAndSwapObject(this, matchOffset, null, s)
                        ){
                    Thread  w = waiter;
                    if(w != null){ // waiters need at most one unpark
                        waiter = null;
                        LockSupport.unpark(w);
                    }
                    return true;
                }

                return match == s;
            }

            /**
             * Tries to cancel a wait by matching node to itself
             */
            void tryCancel(){
                unsafe.compareAndSwapObject(this, matchOffset, null, this);
            }

            boolean isCancelled(){
                return match == this;
            }

            @Override
            public String toString() {
                return "SNode{" +
                        "match=" + match +
                        ", waiter=" + waiter +
                        ", item=" + item +
                        ", mode=" + mode +
                        '}';
            }

            // Unsafe mechanics
            private static Unsafe unsafe;
            private static long matchOffset;
            private static long nextOffset;

            static {
                try{
                    unsafe = UnSafeClass.getInstance();
                    Class<?> k = SNode.class;
                    matchOffset = unsafe.objectFieldOffset(k.getDeclaredField("match"));
                    nextOffset = unsafe.objectFieldOffset(k.getDeclaredField("next"));
                }catch (Exception e){

                }
            }
        }

        /** The head (top) of the stack */
        volatile SNode head;

        boolean casHead(SNode h, SNode nh){
//            new RuntimeException().printStackTrace();
            logger.info("head:"+head+", h:"+h + ", nh:"+nh);
            boolean first = h == head;
            boolean second = unsafe.compareAndSwapObject(this, headOffset, h, nh);
            logger.info("first: " + first + ", second:"+second);
            return first && second;
        }

        /**
         * Creates or resets fields of a node. Called only from transfer
         * where the node to push on stack is lazily created and
         * reused when possible to help reduce intervals between reads
         * and CASes of head and to avoid surges of garbage when CASes
         * to push nodes fail due to contention
         *
         * @param s
         * @param e
         * @param next
         * @param mode
         * @return
         */
        static SNode snode(SNode s, Object e, SNode next, int mode){
            logger.info("init SNode :" + s + ", e:"+e+", next:"+next+", mode:"+mode);
            if(s == null) s = new SNode(e);
            s.mode = mode;
            s.next = next;
            return s;
        }


        /**
         * Puts or takes an item
         *
         * @param e  if non-null, the item to be handed to a consumer;
         *           if null, requests that transfer return an item
         *           offered by producer.
         * @param timed if this operation should timeout
         * @param nanos the timeout, in nanosecond
         * @return
         */
        @Override
        E transfer(E e, boolean timed, long nanos) {
            /**
             * Basic algorithm is to loop trying one of three actions:
             *
             * 1. If apparently empty or already containing nodes of same
             *    mode, try to push node on stack and wait for a match,
             *    returning it, or null if cancelled.
             *
             *  2. If apparently containing node of complementary mode,
             *     try to push a fulfilling node on to stack, match
             *     with corresponding waiting node, pop both from
             *     stack, and return matched item. The matching or
             *     unlinking might not actually be necessary because of
             *     other threads performing action 3:
             *
             *  3. If top of stack already holds another fulfilling node,
             *     help it out by doing its match and/or pop
             *     operations, and then continue. The code for helping
             *     is essentially the same as for fulfilling, except
             *     that it doesn't return the item
             */

            SNode s = null; // constructed/reused as needed
            int mode = (e == null)? REQUEST : DATA;
            logger.info("head:"+head + ", mde:"+mode);

            for(;;){
                SNode h = head;
                logger.info("head:"+head);
                if(h == null || h.mode == mode){ // empty or same-mode
                    if(timed && nanos <= 0){ // cann't wait
                        if(h != null && h.isCancelled()){
                            casHead(h, h.next); // pop cancelled node
                        }
                        else {
                            return null;
                        }
                    }
                    else if(casHead(h, s = snode(s, e, h, mode))){
                        SNode m = awaitFulfill(s, timed, nanos);
                        if(m == s){ // wait was cancelled
                            clean(s);
                            return null;
                        }
                        if((h = head) != null && h.next == s){
                            casHead(h, s.next); // help s's fulfiller
                        }
                        return (E) ((mode == REQUEST) ? m.item : s.item);
                    }
                }
                else if(!isFulfilling(h.mode)){ // try to fulfill
                    logger.info("h:"+h+", h.isCancelled():"+h.isCancelled());
                    if(h.isCancelled()){        // already cancelled
                        casHead(h, h.next);     // pop and retry
                    }
                    else if(casHead(h, s = snode(s, e, h, FULFILLING|mode))){
                        for(;;){ // loop until matched or waiters disapper
                            SNode m = s.next;       // m is s's match
                            if(m == null){          // all waiters are gone
                                casHead(s, null);   // pop fulfill node
                                s = null;           // use new node next time
                                break;              // restart main loop
                            }
                            SNode mn = m.next;
                            boolean tryMatch = m.tryMatch(s);
                            logger.info(" m.tryMatch(s):"+tryMatch+", m:"+m+", s:"+s);
                            if(tryMatch){
                                casHead(s, mn);     // pop both s and m
                                return (E) ((mode == REQUEST) ? m.item : s.item);
                            }
                            else{ // lost match
                                s.casNext(m, mn);   // help unlink
                            }
                        }
                    }
                } else {                    // help a fulfiller
                    SNode m = h.next;       // m is h's match
                    if(m == null){          // waiter is gone
                        casHead(h, null);   // pop fulfilling node
                    }
                    else{
                        SNode mn = m.next;
                        boolean tryMatch = m.tryMatch(h);
                        logger.info("  m.tryMatch(h):"+tryMatch+", m:"+m+", h:"+h);
                        if(tryMatch){  // help match
                            casHead(h, mn); // pop both h and m
                        }
                        else{               // lost match
                            h.casNext(m, mn);// help unlink
                        }
                    }
                }
            }
        }

        /**
         * Spins/blocks until node s is matched by a fulfill operation
         *
         * @param s the waiting node
         * @param timed true if timed wait
         * @param nanos timeout value
         * @return matched node, or s if cancelled
         */
        SNode awaitFulfill(SNode s, boolean timed, long nanos){
            /**
             *  When a node/thread is about to block, it sets its waite
             *  field and then recheck state at least one more time
             *  before actually parking, thus covering race vs
             *  fulfiller noticing that waiter is non-null so should be
             *  woken
             *
             *  When invked by nodes that appear at the point of call
             *  to be at the head of the stack, calls to park are
             *  preceded by spins to avoid blocking when producers and
             *  consumers are arriving very close in time. This can
             *  happen enough to bother only on multiprocessors.
             *
             *  The order of checks for returning out of main loop
             *  reflects fact that interrupts have precedence over
             *  normal returns, which have precedence over
             *  timeouts. (So, on timeout, one last check for match is
             *  done before giving up.) Except that calls from untimed
             *  SynchronousQueue.{poll/offer} don't check interrupts
             *  and don't wait at all, so are trapped in transfer
             *  method rather than calling awaitFulfill
             */

            final long dealline = timed ? System.nanoTime() + nanos : 0L;
            Thread w = Thread.currentThread();
            int spins = (shouldSpin(s) ?
                    (timed ? maxTimeSpins : maxUntimedSpins) : 0);
            for(;;){
                if(w.isInterrupted()){
                    s.tryCancel();
                }
                SNode m = s.match;
                if(m != null){
                    return m;
                }
                if(timed){
                    nanos = dealline - System.nanoTime();
                    if(nanos <= 0l){
                        s.tryCancel();
                        continue;
                    }
                }

                if(spins > 0){
                    spins = shouldSpin(s) ? (spins - 1) : 0;
                }
                else if(s.waiter == null){
                    s.waiter = w; // establish waiter so can park next iter
                }
                else if(!timed){
                    LockSupport.park(this);
                }
                else if(nanos > spinForTimeoutThreshold){
                    LockSupport.parkNanos(this, nanos);
                }
            }
        }

        /**
         * Returns true if node s is at head or there is an active
         * fulfiller
         * @param s
         * @return
         */
        boolean shouldSpin(SNode s){
            SNode h = head;
            logger.info("h == s:"+(h == s)+", head:"+head+", h:"+h+", s:"+s +", isFulfilling(h.mode):"+isFulfilling(h.mode));
            return (h == s || h == null || isFulfilling(h.mode));
        }

        /**
         * Unlinks s from the stack
         * @param s
         */
        void clean(SNode s){
            s.item = null;          // froget item
            s.waiter = null;        // forget thread

            /**
             *  At worst we may need to traverse entrie stack to unlink
             *  s. If ther are multiple concurrent calls to clean, we
             *  might not see s if another thread has already removed
             *  it. But we can stop when we see any node known to
             *  follow s. We use s.next unless it too is cancelled. in
             *  which case we try the node one past. We don't check any
             *  further because we don't want to doubly traverse just to
             *  find sentinel
             */
            SNode past = s.next;
            if(past != null && past.isCancelled()){
                past = past.next;
            }
            // Absorb cancelled nodes at head
            SNode p;
            while((p = head) != null && p != past && p.isCancelled())
                casHead(p, p.next);

            // unsplice embedded nodes
            while(p != null && p != past){
                SNode n = p.next;
                if(n != null && n.isCancelled()){
                    p.casNext(n, n.next);
                }else{
                    p = n;
                }
            }
        }

        // Unsafe mechanics
        private static Unsafe unsafe;
        private static long headOffset;

        static {
            try{
                unsafe = UnSafeClass.getInstance();
                Class<?> k = TransferStack.class;
                headOffset = unsafe.objectFieldOffset(k.getDeclaredField("head"));
            }catch (Exception e){
                throw new Error(e);
            }
        }
    }

    /** Dual Queue */
    static final class TransferQueue<E> extends Transferer<E>{

        /**
         * This extends Scherer-Scott dual queue  algorithm, differing,
         * among other ways, by using modes within nodes rather than
         * marked pointers. The algorithm is a little simpler than
         * that for stacks because fulfillers do not need explicit
         * nodes, and matching is done by CAS'ing QNode.item field
         * from non-null to null (for put) or vice versa (for take)
         */

        /** Node class for TransferQueue */
        static final class QNode{
            volatile QNode next;        // 队列中的next节点
            volatile Object item;       // 对应传递的数据 (produce 是 object, consumer 是 null) <- isDate 就是用这个来进行判别
            volatile Thread waiter;     // producer / consumer 对应的调用线程
            final boolean isData;      // boolean isData = (e != null); 用来判别 producer / consumer

            /** 构造函数 */
            public QNode(Object item, boolean isData) {
                this.item = item;
                this.isData = isData;
            }

            /** CAS 设置 next Node */
            boolean casNext(QNode cmp, QNode val){
                return next == cmp && unsafe.compareAndSwapObject(this, nextOffset, cmp, val);
            }

            /** CAS 设置 item 属性 */
            boolean casItem(Object cmp, Object val){
                return item == cmp && unsafe.compareAndSwapObject(this, itemOffset, cmp, val);
            }

            /** 当调用者 中断(interrupt) 或者等待超时时会调用此方法
             *  将 item 指向自己
             *  在调用  awaitFulfill 方法后会通过返回值和 s (s对应着当前线程) 比较, 若相等就说明 线程是中断的或超时, 则transfer 返回 null
             */
            void tryCancel(Object cmp){
                unsafe.compareAndSwapObject(this, itemOffset, cmp, this);
            }

            /**
             * 判断节点是否是中断/超时结束的
             * @return
             */
            boolean isCancelled(){
                return item == this;
            }

            /**
             * 判断 next == this <- 那什么导致的呢 ? 答案就在 advanceHead 中
             * 通过 next = this 这种方式将node移除queue是nonblockingqueue设计中常用的, ConcurrentLinkedQueue 中也有这种设计
             */
            boolean isOffList(){ return next == this; }

            // Unsafe mechanics
            private static final Unsafe unsafe;
            private static final long itemOffset;
            private static final long nextOffset;

            static {
                try {
                    unsafe = UnSafeClass.getInstance();
                    Class<?> k = QNode.class;
                    itemOffset = unsafe.objectFieldOffset(k.getDeclaredField("item"));
                    nextOffset = unsafe.objectFieldOffset(k.getDeclaredField("next"));
                }catch (Exception e){
                    throw new Error(e);
                }
            }

            @Override
            public String toString() {
                return "QNode{" +
                        ", waiter=" + waiter +
                        ", isData=" + isData +
                        '}';
            }
        }

        /**
         *  这是一个非常典型的 queue , 它有如下的特点
         *  1. 整个队列有 head, tail 两个节点
         *  2. 队列初始化时会有个 dummy 节点
         *  3. 这个队列的头节点是个 dummy 节点/ 或 哨兵节点, 所以操作的总是队列中的第二个节点(AQS的设计中也是这也)
         */

        /** 头节点 */
        transient volatile QNode head;
        /** 尾节点 */
        transient volatile QNode tail;
        /**
         * Reference to a cancelled node that might not yet have been
         * unlinked from queue because it was last inserted node
         * when it was cancelled
         */
        /**
         * 对应 中断或超时的 前继节点,这个节点存在的意义是标记, 它的下个节点要删除
         * 何时使用:
         *      当你要删除 节点 node, 若节点 node 是队列的末尾, 则开始用这个节点,
         * 为什么呢？
         *      大家知道 删除一个节点 直接 A.CASNext(B, B.next) 就可以,但是当  节点 B 是整个队列中的末尾元素时,
         *      一个线程删除节点B, 一个线程在节点B之后插入节点 这样操作容易致使插入的节点丢失, 这个cleanMe很像
         *      ConcurrentSkipListMap 中的 删除添加的 marker 节点, 他们都是起着相同的作用
         */
        transient volatile QNode cleanMe;

        TransferQueue(){
            /**
             * 构造一个 dummy node, 而整个 queue 中永远会存在这样一个 dummy node
             * dummy node 的存在使得 代码中不存在复杂的 if 条件判断
             */
            QNode h = new QNode(null, false);
            head = h;
            tail = h;
        }

        /**
         * 推进 head 节点,将 老节点的 oldNode.next = this, help gc,
         * 这种和 ConcurrentLinkedQueue 中一样
         */
        void advanceHead(QNode h, QNode nh){
            if(h == head && unsafe.compareAndSwapObject(this, headOffset, h, nh)){
                h.next = h; // forget old next help gc
            }
        }

        /** 更新新的 tail 节点 */
        void advanceTail(QNode t, QNode nt){
            if(tail == t){
                unsafe.compareAndSwapObject(this, tailOffset, t, nt);
            }
        }

        /** CAS 设置 cleamMe 节点 */
        boolean casCleanMe(QNode cmp, QNode val){
            return cleanMe == cmp && unsafe.compareAndSwapObject(this, cleanMeOffset, cmp, val);
        }


        /**
         * Puts or takes an item
         * 主方法
         *
         * @param e  if non-null, the item to be handed to a consumer;
         *           if null, requests that transfer return an item
         *           offered by producer.
         * @param timed if this operation should timeout
         * @param nanos the timeout, in nanosecond
         * @return
         */
        @Override
        E transfer(E e, boolean timed, long nanos) {
            /**
             * Basic algorithm is to loop trying to take either of
             * two actions:
             *
             * 1. If queue apparently empty or holding same-mode nodes,
             *    try to add node to queue of waiters, wait to be
             *    fulfilled (or cancelled) and return matching item.
             *
             * 2. If queue apparently contains waiting items, and this
             *    call is of complementary mode, try to fulfill by CAS'ing
             *    item field of waiting node and dequeuing it, and then
             *    returning matching item.
             *
             * In each case, along the way, check for gurading against
             * seeing uninitialized head or tail value. This never
             * happens in current SynchronousQueue, but could if
             * callers held non-volatile/final ref to the
             * transferer. The check is here anyway because it places
             * null checks at top of loop, which is usually faster
             * than having them implicity interspersed
             *
             * 这个 producer / consumer 的主方法, 主要分为两种情况
             *
             * 1. 若队列为空 / 队列中的尾节点和自己的 类型相同, 则添加 node
             *      到队列中, 直到 timeout/interrupt/其他线程和这个线程匹配
             *      timeout/interrupt awaitFulfill方法返回的是 node 本身
             *      匹配成功的话, 要么返回 null (producer返回的), 或正真的传递值 (consumer 返回的)
             *
             * 2. 队列不为空, 且队列的 head.next 节点是当前节点匹配的节点,
             *      进行数据的传递匹配, 并且通过 advanceHead 方法帮助 先前 block 的节点 dequeue
             */
            QNode s = null; // constrcuted/reused as needed
            boolean isData = (e != null); // 1.判断 e != null 用于区分 producer 与 consumer

            for(;;){
                QNode t = tail;
                QNode h = head;
                if(t == null || h == null){         // 2. 数据未初始化, continue 重来
                    continue;                       // spin
                }
                if(h == t || t.isData == isData){   // 3. 队列为空, 或队列尾节点和自己相同 (注意这里是和尾节点比价, 下面进行匹配时是和 head.next 进行比较)
                    QNode tn = t.next;
                    if(t != tail){                  // 4. tail 改变了, 重新再来
                        continue;
                    }
                    if(tn != null){                 // 5. 其他线程添加了 tail.next, 所以帮助推进 tail
                        advanceTail(t, tn);
                        continue;
                    }
                    if(timed && nanos <= 0){        // 6. 调用的方法的 wait 类型的, 并且 超时了, 直接返回 null, 直接见 SynchronousQueue.poll() 方法,说明此 poll 的调用只有当前队列中正好有一个与之匹配的线程在等待被【匹配才有返回值
                        return null;
                    }
                    if(s == null){
                        s = new QNode(e, isData);  // 7. 构建节点 QNode
                    }
                    if(!t.casNext(null, s)){      // 8. 将 新建的节点加入到 队列中
                        continue;
                    }

                    advanceTail(t, s);             // 9. 帮助推进 tail 节点
                    Object x = awaitFulfill(s, e, timed, nanos); // 10. 调用awaitFulfill, 若节点是 head.next, 则进行一些自旋, 若不是的话, 直接 block, 知道有其他线程 与之匹配, 或它自己进行线程的中断
                    if(x == s){                   // 11. 若 (x == s)节点s 对应额线程 wait 超时 或线程中断, 不然的话 x == null (s 是 producer) 或 是正真的传递值(s 是 consumer)
                        clean(t, s);              // 12. 对接点 s 进行清除, 若 s 不是链表的最后一个节点, 则直接 CAS 进行 节点的删除, 若 s 是链表的最后一个节点, 则 要么清除以前的 cleamMe 节点(cleamMe != null), 然后将 s.prev 设置为 cleanMe 节点, 下次进行删除 或直接将 s.prev 设置为cleanMe
                        return null;
                    }

                    if(!s.isOffList()){          // 13. 节点 s 没有 offlist
                        advanceHead(t, s);       // 14. 推进head 节点, 下次就调用 s.next 节点进行匹配(这里调用的是 advanceHead, 因为代码能执行到这边说明s已经是 head.next 节点了)
                        if(x != null){          // and forget fields
                            s.item = s;
                        }
                        s.waiter = null;       // 15. 释放线程 ref
                    }

                    return (x != null) ? (E)x :e;

                }else{                              // 16. 进行线程的匹配操作, 匹配操作是从 head.next 开始匹配 (注意 队列刚开始构建时 有个 dummy node, 而且 head 节点永远是个 dummy node 这个和 AQS 中一样的)
                    QNode m = h.next;               // 17. 获取 head.next 准备开始匹配
                    if(t != tail || m == null || h != head){
                        continue;                  // 18. 不一致读取, 有其他线程改变了队列的结构inconsistent read
                    }

                    /** producer 和 consumer 匹配操作
                     *  1. 获取 m的 item (注意这里的m是head的next节点
                     *  2. 判断 isData 与x的模式是否匹配, 只有produce与consumer才能配成一对
                     *  3. x == m 判断是否 节点m 是否已经进行取消了, 具体看(QNOde#tryCancel)
                     *  4. m.casItem 将producer与consumer的数据进行交换 (这里存在并发时可能cas操作失败的情况)
                     *  5. 若 cas操作成功则将h节点dequeue
                     *
                     *  疑惑: 为什么将h进行 dequeue, 而不是 m节点
                     *  答案: 因为每次进行配对时, 都是将 h 是个 dummy node, 正真的数据节点 是 head.next
                     */
                    Object x = m.item;
                    if(isData == (x != null) ||    // 19. 两者的模式是否匹配 (因为并发环境下 有可能其他的线程强走了匹配的节点)
                            x == m ||               // 20. m 节点 线程中断或者 wait 超时了
                            !m.casItem(x, e)        // 21. 进行 CAS 操作 更改等待线程的 item 值(等待的有可能是 concumer / producer)
                            ){
                        advanceHead(h, m);          // 22.推进 head 节点 重试 (尤其 21 操作失败)
                        continue;
                    }

                    advanceHead(h, m);             // 23. producer consumer 交换数据成功, 推进 head 节点
                    LockSupport.unpark(m.waiter); // 24. 换线等待中的 m 节点, 而在 awaitFulfill 方法中 因为 item 改变了,  所以 x != e 成立, 返回
                    return (x != null) ? (E)x : e; // 25. 操作到这里若是 producer, 则 x != null, 返回 x, 若是consumer， 则 x == null,.返回 producer(其实就是 节点m) 的 e
                }
            }

        }

        /**
         * Spins/blocks until node s is fulfilled
         *
         * 主逻辑: 若节点是 head.next 则进行 spins 一会, 若不是, 则调用 LockSupport.park / parkNanos(), 直到其他的线程对其进行唤醒
         *
         * @param s the waiting node
         * @param e the comparsion value for checking match
         * @param timed true if timed wait
         * @param nanos timeout value
         * @return  matched item, or s of cancelled
         */
        Object awaitFulfill(QNode s, E e, boolean timed, long nanos){

            final long deadline = timed ? System.nanoTime() + nanos : 0L;// 1. 计算 deadline 时间 (只有 timed 为true 时才有用)
            Thread w = Thread.currentThread();   // 2. 获取当前的线程
            int spins = ((head.next == s) ?        // 3. 若当前节点是 head.next 时才进行 spin, 不然的话不是浪费 CPU 吗, 对挖
                    (timed ? maxTimeSpins : maxUntimedSpins) : 0);
            for(;;){                                        // loop 直到 成功
                if(w.isInterrupted()){                      // 4. 若线程中断, 直接将 item = this, 在 transfer 中会对返回值进行判断 (transfer中的 步骤 11)
                    s.tryCancel(e);
                }
                Object x = s.item;
                if(x != e){                                 // 5. 在进行线程阻塞->唤醒, 线程中断, 等待超时, 这时 x != e,直接return 回去
                    return x;
                }
                if(timed){
                    nanos = deadline - System.nanoTime();
                    if(nanos <= 0L){                        // 6. 等待超时, 改变 node 的item值, 进行 continue, 下一步就到  awaitFulfill的第 5 步 -> return
                        s.tryCancel(e);
                        continue;
                    }
                }
                if(spins > 0){                             // 7. spin 一次一次减少
                    --spins;
                }
                else if(s.waiter == null){
                    s.waiter = w;
                }
                else if(!timed){                           // 8. 进行没有超时的 park
                    LockSupport.park(this);
                }
                else if(nanos > spinForTimeoutThreshold){  // 9. 自旋次数过了, 直接 + timeout 方式 park
                    LockSupport.parkNanos(this, nanos);
                }
            }
        }


        /**
         * Gets rid of cancelled node s with original predecessor pred.
         * 对 中断的 或 等待超时的 节点进行清除操作
         */
        void clean(QNode pred, QNode s) {
            s.waiter = null; // forget thread                                        // 1. 清除掉 thread 引用
            /*
             * At any given time, exactly one node on list cannot be
             * deleted -- the last inserted node. To accommodate this,
             * if we cannot delete s, we save its predecessor as
             * "cleanMe", deleting the previously saved version
             * first. At least one of node s or the node previously
             * saved can always be deleted, so this always terminates.
             *
             * 在程序运行中的任何时刻, 最后插入的节点不能被删除(这里的删除指 通过 cas 直接删除, 因为这样直接删除会有多删除其他节点的风险)
             * 当 节点 s 是最后一个节点时, 将 s.pred 保存为 cleamMe 节点, 下次再进行清除操作
             */
            while (pred.next == s) { // Return early if already unlinked           // 2. 判断 pred.next == s, 下面的 步骤2 可能导致 pred.next = next
                QNode h = head;
                QNode hn = h.next;   // Absorb cancelled first node as head
                if (hn != null && hn.isCancelled()) {                              // 3. hn  中断或者超时, 则推进 head 指针, 若这时 h 是 pred 则 loop 中的条件 "pred.next == s" 不满足, 退出 loop
                    advanceHead(h, hn);
                    continue;
                }
                QNode t = tail;      // Ensure consistent read for tail
                if (t == h)                                                        // 4. 队列为空, 说明其他的线程进行操作, 删除了 节点(注意这里永远会有个 dummy node)
                    return;
                QNode tn = t.next;
                if (t != tail)                                                    // 5. 其他的线程改变了 tail, continue 重新来
                    continue;
                if (tn != null) {
                    advanceTail(t, tn);                                            // 6. 帮助推进 tail
                    continue;
                }
                if (s != t) {        // If not tail, try to unsplice              // 7. 节点 s 不是尾节点, 则 直接 CAS 删除节点(在队列中间进行这种删除是没有风险的)
                    QNode sn = s.next;
                    if (sn == s || pred.casNext(s, sn))
                        return;
                }

                QNode dp = cleanMe;                                             // 8. s 是队列的尾节点, 则 cleanMe 出场
                if (dp != null) {    // Try unlinking previous cancelled node
                    QNode d = dp.next;                                          // 9. cleanMe 不为 null, 进行删除删一次的 s节点, 也就是这里的节点d
                    QNode dn;
                    if (d == null ||               // d is gone or              // 10. 这里有几个特殊情况 1. 原来的s节点()也就是这里的节点d已经删除; 2. 原来的节点 cleanMe 已经通过 advanceHead 进行删除; 3 原来的节点 s已经删除 (所以 !d.siCancelled), 存在这三种情况, 直接将 cleanMe 清除
                            d == dp ||                 // d is off list or
                            !d.isCancelled() ||        // d not cancelled or
                            (d != t &&                 // d not tail and        // 11. d 不是tail节点, 且dn没有offlist, 直接通过 cas 删除 上次的节点 s (也就是这里的节点d); 其实就是根据 cleanMe 来清除队列中间的节点
                                    (dn = d.next) != null &&  //   has successor
                                    dn != d &&                //   that is on list
                                    dp.casNext(d, dn)))       // d unspliced
                        casCleanMe(dp, null);                                  // 12. 清除 cleanMe 节点, 这里的 dp == pred 若成立, 说明清除节点s， 成功, 直接 return, 不然的话要再次 loop, 接着到 步骤 13, 设置这次的 cleanMe 然后再返回
                    if (dp == pred)
                        return;      // s is already saved node
                } else if (casCleanMe(null, pred))                          // 原来的 cleanMe 是 null, 则将 pred 标记为 cleamMe 为下次 清除 s 节点做标识
                    return;          // Postpone cleaning s
            }
        }

        /**
         * Gets rid of cancelled node s with original predecessor pred.
         * @param pred
         * @param s
         */
        void clean2(QNode pred, QNode s){
            logger.info("pred:" + pred + ", s :" + s + ", pred.next == s:" + (pred.next == s) + ", Thread.getName:" + Thread.currentThread().getName());
            s.waiter = null; // forget thread

            /**
             * At any given time, exactly one node on list cannot be
             * deleted -- the last inserted node. To accommodate this,
             * If we cannot delete s, we save its predecessor as
             * "cleanMe", deleting  the previously saved version
             * first. At least one of node s or the node previously
             * saved can always be deleted, so this always terminates
             */
            while(pred.next == s){ // Return early if already unlinked
                logger.info("pred.next == s:" + (pred.next == s) + ", Thread.getName:" + Thread.currentThread().getName());
                QNode h = head;
                QNode hn = h.next; // Absorb cancelled first node as head
                logger.info("continue :" +", pred.next == s :" + (pred.next == s) + ", Thread.getName:" + Thread.currentThread().getName());
                if(hn != null && hn.isCancelled()){
                    advanceHead(h, hn);
                    logger.info("continue :" +", pred.next == s :" + (pred.next == s) + "(prev.next = prev) : " + (pred.next == pred) + " , Thread.getName:" + Thread.currentThread().getName());
                    continue;
                }
                QNode t = tail;         // Ensure consistent read for tail
                logger.info("t == h :" + (t == h) + ", t:" + t + ", h:" + h+ ", Thread.getName:" + Thread.currentThread().getName());
                if(t == h){
                    logger.info("return :" + ", Thread.getName:" + Thread.currentThread().getName());
                    return;
                }
                QNode tn = t.next;
                if(t != tail){
                    logger.info("continue :" +", pred.next == s :" + (pred.next == s)  + ", Thread.getName:" + Thread.currentThread().getName());
                    continue;
                }
                if(tn != null){
                    advanceTail(t, tn);
                    logger.info("continue :" +", pred.next == s :" + (pred.next == s)  + ", Thread.getName:" + Thread.currentThread().getName());
                    continue;
                }
                logger.info("s != t : " + (s != t) + ", s :" + s +", t : " + t + ", Thread.getName:" + Thread.currentThread().getName());

                if(s != t){ // If not tail, try to unsplice
                    QNode sn = s.next;
                    if(sn == s || pred.casNext(s, sn)){
                        logger.info("return :" + ", Thread.getName:" + Thread.currentThread().getName());
                        return;
                    }
                }

                logger.info("cleanMe :" + cleanMe+ ", Thread.getName:" + Thread.currentThread().getName());
                QNode dp = cleanMe;
                if(dp != null){ // Try unlinking previous cancelled node
                    QNode d = dp.next;
                    QNode dn;
                    if(d == null ||                                 // d is gone or
                            d == dp ||                              // d is off list or
                            !d.isCancelled() ||                     // d not cancelled or
                            (d != t &&                              // d not tail and
                                    (dn = d.next) != null &&        // has successor
                                    dn != d &&                      // that is on list
                                    dp.casNext(d, dn)               // d unspliced
                            )
                            ){
                        casCleanMe(dp, null);
                    }
                    logger.info("dp == pred:" + (dp == pred) +"continue :" + ", Thread.getName:" + Thread.currentThread().getName());
                    if(dp == pred){
                        return;     // s is already saved node
                    }
                }
                else if(casCleanMe(null, pred)){
                    logger.info("continue :" + ", Thread.getName:" + Thread.currentThread().getName());
                    return;         // Postpone cleaning s
                }
                logger.info("loop another :" + ", Thread.getName:" + Thread.currentThread().getName());
            }

        }

        // unsafe
        private static Unsafe unsafe;
        private static long headOffset;
        private static long tailOffset;
        private static long cleanMeOffset;

        static {
            try {
                unsafe = UnSafeClass.getInstance();
                Class<?> k = TransferQueue.class;
                headOffset = unsafe.objectFieldOffset(k.getDeclaredField("head"));
                tailOffset = unsafe.objectFieldOffset(k.getDeclaredField("tail"));
                cleanMeOffset = unsafe.objectFieldOffset(k.getDeclaredField("cleanMe"));
            }catch (Exception e){
                throw new Error(e);
            }
        }
    }

    /**
     * The transferer. Set only in constructor, but cannot be declared
     * as final without further complicating serialization. Since
     * this is accessed only at most once per public method, there
     * isn't a noticeable performance penalty for using volatile
     * instead of final here
     */
    private transient volatile Transferer<E> transferer;

    /**
     * Creates a {@code SynchronousQueue} with nonfair access policy
     */
    public KSynchronousQueue() { this(false); }

    /**
     * Creates a {@code KSynchronousQueue} with the specified fairness policy
     * @param fair
     */
    public KSynchronousQueue(boolean fair){
        // 通过 fair 值来决定内部用 使用 queue 还是 stack 存储线程节点
        transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
    }

    /**
     * Adds the specified element to this queue, waiting if necessary for
     * another thread to receive it
     *
     * @param e
     * @throws InterruptedException
     */
    public void put(E e) throws InterruptedException{
//        logger.info("put e" + e);
        if(e == null) throw new NullPointerException();
        Object result = transferer.transfer(e, false, 0);
        if(result == null){
            Thread.interrupted();
            throw new InterruptedException();
        }
    }

    /**
     * Inserts the specified element into this queue, waiting if necessary
     * up to the specified wait time for another thread to receive it.
     *
     * @param e
     * @param timeout
     * @param unit
     * @return {@code true} if successful, or {@code false} if the
     *              specified waiting time elapses before a consumer appears
     */
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException{
        if(e == null) throw new NullPointerException();
        if(transferer.transfer(e, true, unit.toNanos(timeout)) != null){
            return true;
        }
        if(!Thread.interrupted()){
            return false;
        }
        throw new InterruptedException();
    }

    /**
     * Inserts the specified element into this queue, if another thread is
     * waiting to receive it.
     *
     * @param e the element to add
     * @return  {@code true} if the element was added to this queue, else
     *          {@code false}
     */
    public boolean offer(E e){
        if(e == null) throw new NullPointerException();
        return transferer.transfer(e, true, 0) != null;
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary
     * for another thread to insert it
     *
     * @return the head of the this queue
     * @throws InterruptedException
     */
    public E take() throws InterruptedException{
        E e = transferer.transfer(null, false, 0);
        if(e != null){
            return e;
        }
        Thread.interrupted();
        throw new InterruptedException();
    }

    /**
     * Retrieves and removes the head of this queue, waiting
     * if necessary up to the specified wait time, for another thread
     * to insert it.
     *
     *
     * @param timeout
     * @param unit
     * @return the head of this queue, or {@code null} if the
     *         specified waiting time elapses before an element is present
     *
     * @throws InterruptedException
     */
    public E poll(long timeout, TimeUnit unit) throws InterruptedException{
        E e = transferer.transfer(null, true, unit.toNanos(timeout));
        if(e != null || !Thread.interrupted()){
            return e;
        }
        throw new InterruptedException();
    }

    /**
     * Retrieves and removes the head of this queue, if another thread
     * is currently making an element available
     *
     * @return the head of this queue, or {@code null} if no
     *          element is available
     */
    public E poll(){
        return transferer.transfer(null, true, 0);
    }

    /**
     * Always return {@code true}
     * A {@code SynchronousQueue} has no internal capacity
     *
     * @return {@code true}
     */
    public boolean isEmpty(){
        return true;
    }

    /**
     * Always returns zero
     * A {@code SynchronousQueue} has no internal capacity
     *
     * @return zero
     */
    @Override
    public int size() {
        return 0;
    }

    /**
     * Always returns zero
     * A {@code SynchronousQueue} has no internal capacity
     * @return zero
     */
    @Override
    public int remainingCapacity() {
        return 0;
    }

    /**
     * Does nothing
     * A {@code SynchronousQueue} has no internal capacity
     */
    public void clear(){

    }

    /**
     * Always returns {@code false}
     * A {@code SynchronousQueue} has no internal capacity
     *
     * @param o
     * @return {@code false}
     */
    public boolean contains(Object o){
        return false;
    }

    /**
     * Always returns {@code false}
     * A {@code SynchronousQueue} has no internal capacity
     *
     * @param o the element to remove
     * @return {@code false}
     */
    public boolean remove(Object o){
        return false;
    }

    /**
     * Returns {@code false} unless the given collection is empty
     * A {@code SynchronousQueue} has no internal capacity
     * @param c
     * @return
     */
    public boolean containsAll(Collection<?> c){
        return c.isEmpty();
    }

    /**
     * Always returns {@code false}
     * A {@code SynchronousQueue} has no internal capacity
     *
     * @param c the collection
     * @return {@code false}
     */
    public boolean removeAll(Collection<?> c){
        return false;
    }

    /**
     * Always returns {@code false}
     * A {@code SynchronousQueue} has no internal capacity
     *
     * @param c the collection
     * @return {@code false}
     */
    public boolean retainAll(Collection<?> c){
        return false;
    }

    /**
     * Always returns {@code null}
     * A {@code KSynchronousQueue} does not return elements
     * unless actively waited on
     *
     * @return {@code null}
     */
    @Override
    public E peek() {
        return null;
    }

    /**
     * Returns an empty iterator in which {@code hasNext} always return
     * {@code false}
     *
     * @return an empty iterator
     */
    public Iterator<E> iterator(){
        return Collections.emptyIterator();
    }

    /**
     * Returns an empty spliterator in which calls to
     * {@link Spliterator#trySplit()} always return {@code null}
     *
     * @return
     * @since 1.8
     */
    public Spliterator<E> spliterator(){
        return Spliterators.emptySpliterator();
    }

    /**
     * Returns a zero-length array
     * @return a zero-length array
     */
    public Object[] toArray(){
        return new Object[0];
    }

    /**
     * Sets the zeroeth element of the specified array to {@code null}
     * (if the array has non-zero length) and returns it
     *
     * @param a
     * @param <T>
     * @return
     */
    public <T> T[] toArray(T[] a){
        if(a.length > 0){
            a[0] = null;
        }
        return a;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        if(c == null){
            throw new NullPointerException();
        }
        if(c == this){
            throw new IllegalArgumentException();
        }
        int n = 0;
        for(E e; (e = poll()) != null;){
            c.add(e);
            ++n;
        }
        return n;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if(c == null){
            throw new NullPointerException();
        }
        if(c == this){
            throw new IllegalArgumentException();
        }
        int n = 0;
        for(E e; n < maxElements && (e = poll()) != null;){
            c.add(e);
            ++n;
        }
        return n;
    }

    /**
     * To cope with serialization strategy in the 1.5 version of
     * SynchronousQueue, we declare some unused classes and fields
     * that exist solely to enable serializability across versions.
     * These fields are never used, so are initialized only if this
     * object is ever serialized or deserialized
     */

    static class WaitQueue implements java.io.Serializable{}

    static class LifoWaitQueue extends WaitQueue{
        private static final long serialVersionUID = -6857468705663163204L;
    }

    static class FifoWaitQueue extends WaitQueue{
        private static final long serialVersionUID = -6857468705663163204L;
    }

    private ReentrantLock qlock;
    private WaitQueue waitingProducers;
    private WaitQueue waitingConsumers;

    /**
     * Saves this queue to a stream (that is, serializes it)
     * @param s
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream s) throws IOException{
        boolean fair = transferer instanceof TransferQueue;
        if(fair){
            qlock = new ReentrantLock(true);
            waitingProducers = new FifoWaitQueue();
            waitingConsumers = new FifoWaitQueue();
        }
        else{
            qlock = new ReentrantLock();
            waitingProducers = new LifoWaitQueue();
            waitingConsumers = new LifoWaitQueue();
        }
        s.defaultWriteObject();
    }

    /**
     * Reconstitutes this queue from a stream (that is, deserializes it).
     *
     * @param s the stream
     * @throws IOException if the class of a serialized object could not be found
     * @throws ClassNotFoundException if an I/O error occurs
     */
    private void readObject(java.io.ObjectInputStream s)
            throws IOException, ClassNotFoundException{
        s.defaultReadObject();
        if(waitingProducers instanceof FifoWaitQueue){
            transferer = new TransferQueue<E>();
        }else{
            transferer = new TransferStack<E>();
        }
    }

    // unsafe mechanics
    static long objectFieldOffset(Unsafe unsafe, String field, Class<?> klazz){
        try{
            return unsafe.objectFieldOffset(klazz.getDeclaredField(field));
        }catch (Exception e){
            // Convert Exception to corresponding Error
            NoSuchFieldError error = new NoSuchFieldError(field);
            error.initCause(e);
            throw error;
        }
    }

}
