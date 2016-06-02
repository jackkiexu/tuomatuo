package com.lami.tuomatuo.search.base.concurrent.spinlock;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import org.apache.log4j.Logger;
import org.javacc.parser.REndOfFile;
import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by xujiankang on 2016/5/26.
 */
public class MyAQS extends AbstractOwnableSynchronizer implements java.io.Serializable {

    private static final Logger logger = Logger.getLogger(MyAQS.class);
    private static final long serialVersionUID = -6424321242585704540L;
    public static final long spinForTimeoutThreshold = 1000l;

    private static final Unsafe unsafe = UnSafeClass.getInstance();
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    public transient volatile Node head;
    public transient volatile Node tail;
    public volatile int state;

    static {
        try {
            stateOffset = unsafe.objectFieldOffset
                    (MyAbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset
                    (MyAbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset
                    (MyAbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset
                    (Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset
                    (Node.class.getDeclaredField("next"));

        } catch (Exception ex) { throw new Error(ex); }
    }

    public MyAQS() {
    }


    protected final int getState(){
        return state;
    }

    protected final void setState(int newState){
        this.state = newState;
    }

    protected final boolean compareAndSetState(int expect, int update){
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
    }

    private static final boolean compareAndSetNext(Node node, Node expect, Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }

    /**
     * insert into the queue tail
     * @param node
     * @return
     */
    public Node enq(final Node node){
        for(;;){
            Node t = tail;
            if(t == null){ // if tail is null, then set new Node() to the head/tail
                if(compareAndSetHead(new Node())){
                    tail = head;
                }
            }else{
                node.prev = t; // let new node.prev is the new Node()
                if(compareAndSetTail(t, node)){ // set node to the tail (the head is a new head this moment)
                    t.next = node; // set t.next = the node
                    return t;
                }
            }
        }
    }

    public void unparkSuccessor(Node node){
        int ws = node.waitStatus;
        if(ws < 0){
            compareAndSetWaitStatus(node, ws, 0);
        }

        Node s = node.next;
        if(s == null || s.waitStatus > 0){
            s = null;
            for(Node t = tail; tail != null && t != node; t = t.prev){
                if(t.waitStatus <= 0){
                    s = t;
                }
            }
        }
        if(s != null){
            LockSupport.unpark(s.thread);
        }
    }

    public void doReleaseShared(){
        /**
         * 确保释放的传播性, 即使在并发情况下, 多个线程在获取, 释放
         * 如果需要唤醒, 则通常尝试头节点的 unparkSuccessor 动作
         * 但是如果他不符合唤醒的条件, 为了确保正确 release, 那么则把头节点的 state 设置为
         * 的 state 设置为 PROPAGATE. 此外, 在执行该行为时,为了以防万一有新节点的加入我们的行为必须在循环中, 而且如果在修改失败
         * 那么也需要重新尝试修改
         */
        for(;;){
            Node h = head;
            if(h != null && h != tail){
                int ws = h.waitStatus;
                if(ws == Node.SIGNAL){
                    if(!compareAndSetWaitStatus(h, Node.SIGNAL, 0)){
                        continue;
                    }
                    unparkSuccessor(h);
                }else
                /** 为什么这里要把 state 状态修改为 Node.PROPAGATE ? 可以想象一下什么情况下的节点的状态被修改为0
                 *  线程 1 调用 doReleaseShared() 的方法释放头节点, 此时头节点的状态设置为 0, compareAndSetWaitStatus(h, Node.SIGNAL, 0)
                 *  然后 unparkSuccessor(h); AQS 的头节点则被唤醒重试尝试出队. 注意: 此时的头节点状态为 0!
                 *  线程 2 调用且成功进入到 doReleaseShared() 方法, 此时获取头节点状态为 0(新的节点还未被 setHead), 既然能进入到这里, 总不能释放失败吧
                 *  然后则把头节点由 0 修改为 Node.PROPAGATE, 这样我们在关注下 setHeadAndPropagate 方法
                 *  if(propagate > 0 || h == null || h.waitStatus < 0) {
                 *      Node a = node.next;
                 *      if(s == null || s.isShared())
                 *   }
                 *   可以看到这时候 h.waitStatus 是小于 0 的, 则保证了 并发情况下线程2 的释放成功
                 */
                if(ws == 0 && !compareAndSetWaitStatus(h, 0, Node.PROPAGATE)){
                    continue;
                }
            }
            /**
             * 为什么这里加个  h == head
             * 思考什么情况下这里的头部会被改变, 上面也说了: Additionally, we must loop in case a new node is added while we are doing this
             * 假设当前 AQS 队列没有任何等待的节点, 即 head == tail, 这时候上面的 if 判断不成立, 执行到这里适合再次判断 h== head, 如果有新节点添加进来
             * 则 h != head, 会重新尝试释放. 我的结论： 应该是为了保证在多线程情况下的尽可能成功性
             */
            if(h == head)
                break;;
        }
    }

    public void doAcquireInterruptibly(int arg) throws InterruptedException{
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for(;;){
                final Node p = node.predecessor();
                if(p == head && tryAcquire(arg)){
                    setHead(node);
                    p.next = null;
                    failed = false;
                    return;
                }
                if(shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()){
                    throw new InterruptedException();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException{
        long lastTime = System.nanoTime();
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for(;;){
                final Node p = node.predecessor();
                if(p == head && tryAcquire(arg)){
                    setHead(node);
                    p.next = null;
                    failed = true;
                    return true;
                }
                if(nanosTimeout <= 0){
                    return false;
                }
                if(shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold){
                    LockSupport.parkNanos(this, nanosTimeout);
                }
                long now = System.nanoTime();
                nanosTimeout -= now - lastTime;
                lastTime = now;
                if(Thread.interrupted()){
                    throw new InterruptedException();
                }
            }
        }finally {
            if(failed){
                cancelAcquire(node);
            }
        }
    }

    public void doAcquireShared(int arg){
        // 添加当前线程为一个共享模式的节点
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;

        try {
            boolean interrupted = false;
            for(;;){
                final Node p = node.predecessor();
                if(p == head){
                    int r = tryAcquireShared(arg);
                    // 如果当前节点的前驱节点 == head 且 state 值大于0 则认为成功
                    if(r >= 0){
                        setHeadAndPropagate(node, r);
                        p.next = null;
                        if(interrupted){
                            selfInterrupt();
                        }
                        failed = false;
                        return;
                    }

                    // 判断当前节点是否应该被阻塞, 则是阻塞等待其他线程 release
                    if(shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()){
                        interrupted = true;
                    }
                }
            }
        } finally {
            // 如果出异常, 没有完成当前节点的出队, 则取消当前节点
            if(failed){
                cancelAcquire(node);
            }
        }
    }

    public void doAcquireSharedInterruptibly(int arg) throws InterruptedException{
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;

        try {
            for(;;){
                final Node p = node.predecessor();
                if(p == head){
                    int r = tryAcquireShared(arg);
                    if(r >= 0){
                        setHeadAndPropagate(node, r);
                        p.next = null;
                        failed = false;
                        return ;
                    }
                }
                if(shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()){
                    throw new InterruptedException();
                }
            }
        }finally {
            if(failed){
                cancelAcquire(node);
            }
        }
    }

    public boolean doAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException{
        long lastTime = System.nanoTime();
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;

        try {
            for(;;){
                final Node p = node.predecessor();
                if(p == head){
                    int r = tryAcquireShared(arg);
                    if(r >= 0){
                        setHeadAndPropagate(node, r);
                        p.next = null;
                        failed = false;
                        return true;
                    }
                }

                if(nanosTimeout <= 0)
                    return false;
                if(shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold){
                    LockSupport.parkNanos(this, nanosTimeout);
                }

                long now = System.nanoTime();
                nanosTimeout -= now - lastTime;
                lastTime = now;
                if(Thread.interrupted()){
                    throw new InterruptedException();
                }
            }
        } finally {
            if(failed){
                cancelAcquire(node);
            }
        }
    }

    public final void acquireInterruptibly(int arg) throws InterruptedException{
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        if(!tryAcquire(arg)){
            doAcquireInterruptibly(arg);
        }
    }

    public final boolean tryAcquire(int arg, long nanosTimeout) throws InterruptedException{
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
    }

    public final void acquire(int arg){
        /**
         * 首先调用 tryAcquire(arg) 值尝试获取, 如果成功则返回 true. !true 则等于false 不需要进入 acquireQueued(addWaiter(Node.EXCLUSIVE), arg)
         * 进行排队等待再次成功获取
         */
        if(tryAcquire(arg) &&
                acquireQueued(addWaiter(Node.EXCLUSIVE), arg)){
            selfInterrupt();
        }
    }



    public static void selfInterrupt(){
        Thread.currentThread().interrupt();
    }

    public final boolean parkAndCheckInterrupt(){
        LockSupport.park(this);
        return Thread.interrupted();
    }

    public void setHead(Node node){
        head = node;
        node.thread = null;
        node.prev = null;
    }

    public void setHeadAndPropagate(Node node, int propagate){
        Node h = head;
        // 首先设置 node 为头节点
        setHead(node);
        if(propagate > 0 || h == null || h.waitStatus < 0){
            Node s = node.next;
            if(s == null || s.isShared()){
                doReleaseShared();
            }
        }
    }

    public static boolean shouldParkAfterFailedAcquire(Node pred, Node node){
        int ws = pred.waitStatus;
        // 该节点如果状态如果为 SIGNAL. 则返回 true, 然后park挂起线程
        if(ws == Node.SIGNAL){
            return true;
        }
        // 表明该节点已经被取消, 向前循环重新调整链表节点
        if(ws > 0){
            do{
                /**
                 * Predecessor was cancelled. Skip over predecessors and indicate retry
                 */
                node.prev = pred = pred.prev;
            }while(pred.waitStatus > 0);
        }else{
            /**
             * 执行到这里代表节点是0或者 propagate, 然后标记他们为 SIGNAL, 但是
             * 还不能 park 挂起线程. 需要重试是否能获取, 如果不能则挂起的话, 线程会一直抢占 CPU
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }

    public boolean acquireQueued(final Node node, int arg){
        boolean failed = true;

        try {
            boolean interrupted = false;
            for(;;){
                // 获取当前节点的前驱节点
                final Node p = node.predecessor();
                /**
                 *  如果前驱节点是头节点且尝试取得成功, 则替换当前节点链表的头节点, 然后返回
                 *  问题: 为什么是前驱节点而不是当前节点? 因为我们队列在初始化时候生成了个虚拟头节点, 相当多出来个头节点
                 */
                if(p == head && tryAcquire(arg)){
                    setHead(node);
                    // 设置前驱节点的后节点为 null, 使前驱节点成为不可达, 方便 GC
                    p.next = null;
                    failed = false;
                    return interrupted;
                }
                /**
                 * 判断当前节点的线程是否应该被挂起, 如果应该被挂起则挂起. 等待 release 唤醒释放
                 * 问题: 为什么要挂起当前线程? 因为如果不挂起的话, 线程会一直抢占着 CPU
                 */
                if(shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()){
                    interrupted = true;
                }
            }
        }finally {
            if(failed){
                // 在队列中取消当前节点
                cancelAcquire(node);
            }
        }
    }

    public void cancelAcquire(Node node){
        // Ignore if node doesn't exist
        if(node == null){
            return;
        }

        node.thread = null;
        // Skip cancelled predecessors
        Node pred = node.prev;

        // 迭代剔除已被取消的节点
        while(pred.waitStatus > 0){
            node.prev = pred = pred.prev;
        }

        Node predNext = pred.next;
        node.waitStatus  = Node.CANCELLED;
        if(node == tail && compareAndSetTail(node, pred)){
            compareAndSetNext(pred, predNext, null);
        }else{
            int ws;
            if(pred != head &&
                    (
                            (ws = pred.waitStatus) == Node.SIGNAL ||
                                    (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))
                            ) &&
                    pred.thread != null
                    ){
                Node next = node.next;
                if(next != null && next.waitStatus <= 0){
                    compareAndSetNext(pred, predNext, next);
                }
            }else{
                /**
                 * 1. 头部 head
                 * 2. 当前节点的前驱节点状态为 SIGNAL + 前驱节点不为 null
                 * 3. 如果前驱节点不是取消状态且修改前驱节点状态为 SIGNAL 成功 + 前驱节点线程不为 null
                 */
                unparkSuccessor(node);
            }
            node.next = node; // help gc
        }

    }

    public Node addWaiter(Node mode){
        Node node = new Node(Thread.currentThread(), mode);
        // 尝试快速入队列, 及无竞争条件下肯定成功. 如果失败, 则进入 enq 自旋重试入队
        Node pred = tail;
        if(pred != null){
            node.prev = pred;
            // CAS 替换当前尾部. 成功则返回
            if(compareAndSetTail(pred, node)){
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }

    public final boolean release(int arg){
        if(tryRelease(arg)){
            Node h = head;
            if(h != null && h.waitStatus != 0){
                unparkSuccessor(h);
            }
            return true;
        }
        return false;
    }

    public final void acquireShared(int arg){
        if(tryAcquireShared(arg) < 0){
            doAcquireShared(arg);
        }
    }

    public final void acquireSharedInterruptibly(int arg) throws InterruptedException{
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        if(tryAcquireShared(arg) < 0){
            doAcquireSharedInterruptibly(arg);
        }
    }

    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException{
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        return tryAcquireShared(arg) >= 0 || doAcquireSharedNanos(arg, nanosTimeout);
    }

    public Thread fullGetFirstQueuedThread(){
        Node h, s;
        Thread st;
        if( (
                (h = head) != null
                    && (s = h.next) != null &&
                        s.prev == head &&
                        (st = s.thread) != null
                )
             ||
                (
                        (h = head) != null &&
                                (s = h.next) != null &&
                                s.prev == head &&
                                (st = s.thread) != null
                        )
                ){
            return st;
        }

        Node t = tail;
        Thread firstThread = null;
        while(t != null && t != head){
            Thread tt = t.thread;
            if(tt != null)
                firstThread = tt;
            t = t.prev;
        }
        return firstThread;
    }

    public final boolean isQueued(Thread thread){
        if(thread == null){
            throw  new NullPointerException();
        }
        for(Node p = tail; p != null; p = p.prev){
            if(p.thread == thread){
                return true;
            }
        }
        return false;
    }

    public final boolean apparentlyFirstQueuedIsExclusive(){
        Node h, s;
        return (h = head) != null &&
                (s = h.next) != null &&
                !s.isShared() &&
                s.thread != null;
    }

    public final boolean hasQueuedPredecessors(){
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t &&
                ((s=h.next) == null || s.thread != Thread.currentThread());

    }

    public final int getQueueLength(){
        int n = 0;
        for(Node p = tail; p != null; p = p.prev){
            if(p.thread != null){
                ++n;
            }
        }
        return n;
    }

    public final Collection<Thread> getQueueThreads(){
        ArrayList<Thread> list = new ArrayList<Thread>();
        for(Node p = tail; p != null; p = p.prev){
            Thread t = p.thread;
            if(t != null){
                list.add(t);
            }
        }
        return list;
    }

    public final Collection<Thread> getExclusiveQueueThreads(){
        ArrayList<Thread> list = new ArrayList<Thread>();
        for(Node p = tail; p != null; p = p.prev){
            if(!p.isShared()){
                Thread t = p.thread;
                if(t != null)
                    list.add(t);
            }
        }
        return list;
    }

    public final Collection<Thread> getSharedQueuedThreads(){
        ArrayList<Thread> list = new ArrayList<Thread>();
        for(Node p = tail; p != null; p = p.prev){
            if(p.isShared()){
                Thread t = p.thread;
                if(t != null){
                    list.add(t);
                }
            }
        }
        return list;
    }

    public String toString(){
        int s = getState();
        String q = hasQueuedThreads() ? "non" : "";
        return super.toString() +
                "[State = ]" + s + "," + q + "empty queue]";
    }

    public boolean isOnSyncQueue(Node node){
        if(node.waitStatus == Node.CONDITION || node.prev == null){
            return false;
        }
        if(node.next != null){
            return true;
        }
        return findNodeFromTail(node);
    }

    public final boolean findNodeFromTail(Node node){
        Node t = tail;
        for(;;){
            if( t == node){
                return true;
            }
            if(t == null){
                return false;
            }
            t = t.prev;
        }
    }

    public final boolean transferForSignal(Node node){
        /**
         * If can not change waitStatus, the node has been cancelled
         */
        if(!compareAndSetWaitStatus(node, Node.CONDITION, 0)){
            return false;
        }
        Node p = enq(node);
        int ws = p.waitStatus;
        if(ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL)){
            LockSupport.unpark(node.thread);
        }
        return true;
    }

    public final boolean transferAfterCancelledWait(Node node){
        if(compareAndSetWaitStatus(node, Node.CONDITION, 0)){
            enq(node);
            return true;
        }
        while(!isOnSyncQueue(node))
            Thread.yield();
        return false;
    }

    public int fullyRelease(Node node){
        boolean failed = true;

        try {
            int savedState = getState();
            if(release(savedState)){
                failed = false;
                return savedState;
            }else{
                throw  new IllegalMonitorStateException();
            }
        } finally {
            if(failed){
                node.waitStatus = Node.CANCELLED;
            }
        }
    }

    public final boolean owns(ConditionObject condition){
        if(condition == null)
            throw  new NullPointerException();
        return condition.hasWaiters();
    }

    public final boolean hasWaiters(ConditionObject condition){
        if(!owns(condition)){
            throw  new IllegalArgumentException();
        }
        return condition.hasWaiters();
    }

    public final int getWaitQueueLength(ConditionObject condition){
        if(!owns(condition)){
            throw new IllegalArgumentException("Not owner");
        }
        return condition.getWaitQueueLength();
    }

    public final Collection<Thread> getWaitingThreads(ConditionObject condition){
        if(!owns(condition)){
            throw new IllegalArgumentException("Not owner");
        }
        return condition.getWaitingThreads();
    }

    /**
     * sub class will implement these methods
     * 尝试在独占模式下获取, 这个方法应该查下对象的状态是否被允许在独占模式下获取, 如果是才获取
     * 这个方法通常由线程执行获取时调用, 如果该方法返回false, 且该线程还未进入队列, 则该线程会进去AQS队列排队后挂起线程
     * 直到其他线程调用release进行通知已被的线程释放. 该方法可以备用来实现 Lock.tryLock
     * @param arg
     * @return
     */
    public boolean tryAcquire(int arg){
        throw new UnsupportedOperationException();
    }

    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * 共享模式下尝试获取. 该方法应该确定在共享模式下, Object 的状态值是否允许被获取
     * 如果该方法返回结果被认为失败(值<0), 则当前线程进入 AQS 的同步队列阻塞等待
     * 直到其他线程调用 release 释放
     * @param arg
     * @return
     */
    public int tryAcquireShared(int arg){
        throw new UnsupportedOperationException();
    }

    public boolean ReleaseShared(int arg){
        throw new UnsupportedOperationException();
    }

    public final boolean hasQueuedThreads() { return head != tail;}

    public final boolean hasContented() { return head != null;}

    public final Thread getFirstQueuedThread() { return (head == tail) ? null : fullGetFirstQueuedThread();}



    public boolean isHeldExclusively(){
        throw new UnsupportedOperationException();
    }


    public static final class Node{
        // SHARED 作为共享模式下的常量
        public static final Node SHARED = new Node();
        // EXCLUSIVE 作为独占模式下的常量
        public static final Node EXCLUSIVE = null;

        // 常量: 表示节点的线程是已被取消的
        public static final int CANCELLED = 1;
        // 常量: 表示当前节点的后继节点的线程需要被唤醒
        public static final int SIGNAL = -1;
        // 常量: 表示线程正在等待某个条件
        public static final int CONDITION = -2;
        // 常量: 表示下一个共享模式的节点应该无条件的传播下去
        public static final int PROPAGATE = -3;

        /**
         * 状态字段: 具有以下值
         * 1 SIGNAL: 当前节点的后继节点已经(或即将)被阻塞(通过park), 所以当 当前节点释放或被取消时候, 一定要 unpark它的后继节点. 为了避免竞争, 获取方法一定要首先设置 node 为 signal, 然后再次重新调用获取方法, 如果失败则阻塞
         * -1 CANCELLED: 当前节点由于超时或者被中断而被取消, 一旦节点被取消, 那么它的状态值不再会被改变, 且当前点的线程不会再次被阻塞
         * -2 CONDITION: 表示当前节点正在条件队列(AQS下的ConditionObject里也维护了队列)中, 在从 conditionObject 队列 转移到同步队列前, 它不会在同步队列(AQS下的队列)中被使用. 当成功转移后, 该节点的状态值将由 CONDITION 设置 0
         * -3 PROPAGATE: 共享模式下的释放操作应该被传播到其他节点. 该状态值在 doReleaseShared 方法中被设置的
         * 0 以上都不是
         */

        /**
         * 该状态值为了简便使用, 所以使用了数值类型. 非负数值意味着该节点不需要被唤醒. 所以, 大多数代码中不需要检查该状态值得确定值, 只需要根据正负值来判断即可对于一个正常的 Node, 他的 waitStatus 他的 waitStatus 初始化值时0.
         * 对于一个 condition 队列中的 node, 他的初始值时 CONDITION, 如果想要修改这个值, 可以使用 AQS 提供 CAS 进行修改
         */
        public volatile int waitStatus;

        /**
         * 指向当前节点的前驱节点, 当前节点依赖前驱节点来检测 waitStatus, 前驱节点是在当前节点入队列时候被设置的
         * 为了提高 GC 效率, 在当前节点出队列时候会把前驱节点设置为 null, 而且, 在取消前驱节点中, 则会在循环直到找到一个非取消的节点
         * 由于头结点永远不会是取消状态, 所以一定能找到
         */
        public volatile Node prev;

        /**
         * 指向当前节点的后继节点, 在当前节点释放时候会唤醒后继节点, 该后继节点也是在入队列时候被分配, 当前驱节点被取消时候, 会重新调整链表的节点链接指向关系. 如: 前驱节点的前驱节点指向当前节点.
         * 且把前驱节点设置为 null. 节点入队操作过程完成前, 入队操作并还未设置前驱节点的后继节点. 所以会看到前驱点的后继节点为 null, 但是这并不意味着前驱节点就是队列的尾节点! 如果后继节点为 null,
         * 我们可以通过从尾节点向前扫描来做双重检测. 一个被取消的节点的后继节点被设置为自身. 即 node.next = node. 这样设置会帮助 isOnSyncQueue的执行效率更高(即执行时间更短. 注意该方法的 if(node.next != null))
         */
        public volatile Node next;

        /**
         * 当前节点的线程. 在构造 Node 时候被初始化, 在节点使用完毕后设置为 null
         * construction and nulled out after use
         */
        public volatile Thread thread;

        /**
         * ConditionObject 链表的后继节点或者代表共享模式的节点 SHARED. Condition 条件队列: 因为 Condition 队列只能在独占模式下被访问,
         * 我们只需要简单的使用链表队列来链接正在等待条件的节点. 再然后它们会被转移到同步队列(AQS队列)再次重新获取
         * 由于条件队列只能在独占模式下使用, 所以我们要表示共享模式的节点的话只要使用特殊值 SHARED 来标明即可
         */
        public Node nextWaiter;

        public Node() { // Used to establish initial head or SHARED marker
        }

        public Node(Thread thread, Node mode) { // Used by addWaiter
            this.thread = thread;
            this.nextWaiter = mode;
        }

        public Node( Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }

        /**
         * 如果 节点是属于共享模式节点则返回 true
         * @return
         */
        public final boolean isShared() {
            return nextWaiter == SHARED;
        }

        /**
         *  returns previous node, or throws NullPointerException if null
         *  Use when predecessor cannot be null, The null check could
         *  be elided, but is present to help th VM
         * @return
         * @throws NullPointerException
         */
        public final Node predecessor() throws NullPointerException{
            Node p = prev;
            if(p == null){
                throw new NullPointerException();
            }else{
                return p;
            }
        }
    }

    public class ConditionObject implements Condition, java.io.Serializable{

        private static final long serialVersionUID = -2389910261143459425L;
        // 条件队列的头节点
        public transient Node firstWaiter;
        // 条件队列的尾节点
        public transient Node lastWaiter;

        public static final int REINTERRUPT = 1;
        public static final int THROW_IE = -1;

        public ConditionObject() {
        }

        /**
         * 添加一个新节点到条件队列
         * 可以看到, 在修改队列节点结构时候并没有使用 CAS, 这是因为通常 使用 condition 的前提必须在独占模式的 lock 下
         * @return
         */
        public Node addConditionWaiter(){
            Node t = lastWaiter;
            // 如果条件队列的尾节点已被取消, 则调用 unlinkedCancelledWaiters 重新调整结构
            if(t != null && t.waitStatus != Node.CONDITION){
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);
            if(t == null){
                firstWaiter = node;
            }else{
                t.nextWaiter = node;
            }
            lastWaiter = node;
            return node;
        }

        /**
         * 持有锁的情况下, 从 condition 等待队列中分离已取消的节点
         * 该方法只有在条件队列中发生节点取消或者添加新的节点的时候发现尾节点已被取消时调用
         * 该方法需要避免垃圾滞留(没有 signal 时候), 所以即使它需要完整遍历， 但也只有在由于没有 signal 而导致的超时时
         * 或者取消时才起作用
         */
        public void unlinkCancelledWaiters(){
            Node t = firstWaiter;
            Node trail = null;
            while( t != null ){
                Node next = t.nextWaiter;
                if(t.waitStatus != Node.CONDITION){
                    t.nextWaiter = null;
                    if(trail == null){
                        firstWaiter = next;
                    }else{
                        trail.nextWaiter = next;
                    }

                    if(next == null){
                        lastWaiter = trail;
                    }
                }else{
                    trail = t;
                }
                t = next;
            }
        }

        public void doSignl(Node first){
            do{
                if((firstWaiter = first.nextWaiter) == null){
                    lastWaiter = null;
                }
                first.nextWaiter = null;
            }while(!transferForSignal(first) &&
                    (first = firstWaiter) != null);
        }

        /**
         * 在独立锁模式下, 删除当前 Condition 中等待队列的头节点
         */
        public void signal() {
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            Node first = firstWaiter;
            if(first != null){
                /**
                 * 删除当前 Condition 中的等待队列的头节点, 且转移头节点到 AQS 的同步等待队列中
                 * 注意: 仅仅只是 删除头节点, 并没有唤醒任何节点
                 * 那么疑问来了, 为什么 signal 不唤醒节点却能达到 Object 的signal 一样的效果
                 * 单纯的从这一步解释的通, 因为 signal 代表唤醒线程. AQS 利用 signal 必须得持有独占锁
                 * 在 unlock 时候, 实际上就是唤醒 await 节点. 而这里的 signal 仅仅只是移除等待队列的头部
                 */
                doSignl(first);
            }
        }

        public void await() throws InterruptedException {
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            while(!isOnSyncQueue(node)){
                LockSupport.park(this);
                if((interruptMode = checkInterruptWhileWaitting(node)) != 0){
                    break;
                }
            }

            if(acquireQueued(node, savedState) && interruptMode != THROW_IE){
                interruptMode = REINTERRUPT;
            }
            if(node.nextWaiter != null){
                unlinkCancelledWaiters();
            }
            if(interruptMode != 0){
                reportInterruptAfterWait(interruptMode);
            }
        }

        /**
         *
         */
        public void awaitUninterruptibly() {
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean interrupted = false;
            while(!isOnSyncQueue(node)){
                LockSupport.park(this);
                if(Thread.interrupted()){
                    interrupted = true;
                }
            }
            if(acquireQueued(node, savedState) || interrupted){
                selfInterrupt();
            }
        }

        /**
         * 指定一个相对时间, 如果在相对时间内被唤醒且检查是否满足不再阻塞线程条件, 否知阻塞直到到达过期时间
         * 释放当前线程
         * @param nanosTimeout
         * @return
         * @throws InterruptedException
         */
        public long awaitNanos(long nanosTimeout) throws InterruptedException {

            if(Thread.interrupted()){
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            long lastTime = System.nanoTime();
            boolean timeout = false;
            int interruptMode = 0;
            while(!isOnSyncQueue(node)){
                if(nanosTimeout <= 0l){
                    timeout = transferAfterCancelledWait(node);
                    break;
                }
                if(nanosTimeout >= spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if((interruptMode = checkInterruptWhileWaitting(node)) != 0){
                    break;
                }
                long now = System.nanoTime();
                nanosTimeout -= now - lastTime;
                lastTime = now;
            }

            if(acquireQueued(node, savedState) && interruptMode != THROW_IE){
                interruptMode = REINTERRUPT;
            }
            if(node.nextWaiter != null){
                unlinkCancelledWaiters();
            }
            if(interruptMode != 0){
                reportInterruptAfterWait(interruptMode);
            }

            return nanosTimeout - (System.nanoTime() - lastTime);
        }

        public int checkInterruptWhileWaitting(Node node){
            return Thread.interrupted()?
                    (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT):
                    0;
        }

        /**
         * 根据 mode 来决定是否抛出 异常
         * @param interruptMode
         * @throws InterruptedException
         */
        public void reportInterruptAfterWait(int interruptMode) throws InterruptedException{
            if(interruptMode == THROW_IE){
                throw new InterruptedException();
            } else if(interruptMode == REINTERRUPT){
                selfInterrupt();
            }
        }

        public boolean await(long time, TimeUnit unit) throws InterruptedException {
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            while(!isOnSyncQueue(node)){
                LockSupport.park(this);
                if((interruptMode = checkInterruptWhileWaitting(node)) != 0){
                    break;
                }
            }

            if(acquireQueued(node, savedState) && interruptMode != THROW_IE){
                interruptMode = REINTERRUPT;
            }
            if(node.nextWaiter != null){
                unlinkCancelledWaiters();
            }
            if(interruptMode != 0){
                reportInterruptAfterWait(interruptMode);
            }

            return false;

        }

        // 判断 sync 是否和当前的调用的this是同一个. 追踪下的 AQS 的 owns(ConditionObject condition) 就明白了
        public boolean isOwnedBy(MyAQS sync){
            return sync == MyAQS.this;
        }

        /**
         * 指定一个绝对时间, 如果在绝对时间之前被唤醒, 则线程检查是否满足完成阻塞, 是 则 推出阻塞
         * 否则继续阻塞直到到达绝对时间, 然后才阻塞
         * @param deadline
         * @return
         * @throws InterruptedException
         */
        public boolean awaitUntil(Date deadline) throws InterruptedException {
            if(deadline == null){
                throw new NullPointerException();
            }
            long abstime = deadline.getTime();
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean timedout = false;

            int interruptMode = 0;
            while(!isOnSyncQueue(node)){
                if(System.currentTimeMillis() > abstime){
                    timedout = transferAfterCancelledWait(node);
                    break;
                }
                LockSupport.parkUntil(this, abstime);
                if((interruptMode = checkInterruptWhileWaitting(node)) != 0){
                    break;
                }
            }

            if(acquireQueued(node, savedState) && interruptMode != THROW_IE){
                interruptMode = REINTERRUPT;
            }
            if(node.nextWaiter != null){
                unlinkCancelledWaiters();
            }
            if(interruptMode != 0){
                reportInterruptAfterWait(interruptMode);
            }
            return !timedout;
        }

        /**
         * 删除 condition 等待队列中的节点 first, 且把节点 first 转移到 condition 所属的 AQS 等待队列中
         * @param first
         */
        public void doSignal(Node first){
            do{
                if((firstWaiter = first.nextWaiter) == null){
                    lastWaiter = null;
                }
                firstWaiter = null;
            }while(!transferForSignal(first) &&
                    (first = firstWaiter) != null);
        }

        /**
         * 转移且删除所有的节点
         * @param first
         */
        public void doSignalAll(Node first){
            lastWaiter = firstWaiter = null;
            do{
                Node next = first.nextWaiter;
                first.nextWaiter = null;
                transferForSignal(first);
                first = next;
            }while(first != null);
        }

        /**
         * 在当前的线程拥有独占锁的情况下, 删除当前 condition 中等待队列的所有的线程
         */
        public void signalAll() {
            // 判断是否 拥有独占锁
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            Node first = firstWaiter;
            if(first != null){
                doSignalAll(first);
            }
        }

        public boolean iwOwnedby(MyAQS sync){
            return sync == MyAQS.this;
        }


        /**
         * 查询当前等待队列是否存在, 有效等待 (waitStatus 值为 Condition) 的线程
         * @return
         */
        public final boolean hasWaiters(){
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            for(Node w = firstWaiter; w != null; w = w.nextWaiter){
                if(w.waitStatus == Node.CONDITION){
                    return true;
                }
            }
            return false;
        }

        /**
         * 获取当前等待队列里节点数的估值
         * @return
         */
        public final int getWaitQueueLength(){
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            int n = 0;

            for(Node w = firstWaiter; w != null; w = w.nextWaiter){
                if(w.waitStatus == Node.CONDITION){
                   ++n;
                }
            }
            return n;
        }

        /**
         * 获取当前等待队列里节点里的处于有效等待唤醒状态的线程集合
         * @return
         */
        protected final Collection<Thread> getWaitingThreads(){
            if(!isHeldExclusively())
                throw  new IllegalMonitorStateException();
            ArrayList<Thread> list = new ArrayList<Thread>();
            for(Node w = firstWaiter; w != null; w = w.nextWaiter){
                if(w.waitStatus == Node.CONDITION){
                    Thread t = w.thread;
                    if(t != null){
                        list.add(t);
                    }
                }
            }
            return list;
        }
    }
}
