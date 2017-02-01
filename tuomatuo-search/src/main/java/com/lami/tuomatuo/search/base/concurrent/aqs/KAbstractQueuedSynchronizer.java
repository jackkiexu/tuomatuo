package com.lami.tuomatuo.search.base.concurrent.aqs;

import com.lami.tuomatuo.search.base.concurrent.spinlock.MyAbstractQueuedSynchronizer;
import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;

/**
 * http://blog.csdn.net/yuenkin/article/details/50867530#comments
 * http://gee.cs.oswego.edu/dl/papers/aqs.pdf
 * http://www.ibm.com/developerworks/cn/java/j-jtp04186/
 *
 *
 * Provides a framework for implementing blocking locks locks and related
 * synchronizers (semaphores, event, etc) that rely on
 * first-in-first-out (FIFO) wait queues. This class is designed to
 * be a useful basis for most kinds of synchronizers that rely on a
 * single atomic(@code int) value to represent state. Subclasses
 * must define the protected methods that change this state, and which
 * define what that state means in terms of this object being acquired
 * or released. Given these, the other methods in this class carry
 * out all queuing and blocking mechanics. Subclasses can maintain
 * other state fields, but only the atomically updated {@code int}
 * value manipulated using methods {@link #getState}, {@link #setState}
 * and {@link #compareAndSetState} is tracked with respect
 * to synchronization
 *
 * <p>
 *     Subclasses should be defined as non-public internal helper
 *     classes that are used to implement the synchronization properties
 *     of their enclosing class, Class
 *     {@code KAbstractQueuedSynchronizer} does not implement any
 *     synchronization interface. Instead it defines methods such as
 *     {@link #acquireInterruptibly} that can be invoked as
 *     appropriate by concrete locks and related synchronizers to
 *     implement their public methods
 * </p>
 *
 * <p>
 *     This class supports either or both a default <em>exclusive</em>
 *     mode and a <em>shared</em> mode. When acquired in exclusive mode,
 *     attempted acquires by other threads cannot succeed. This class
 *     does not understand; these differences except in the mechanical
 *     sense that when a shared mode acquire succeeds, the next
 *     waiting thread (if n=one exists) must also determine modes share
 *     the same FIFO queue. Usually, implementation subclass support only
 *     one of these modes, but both can come into play for example in a
 *     {@link "ReadWriteLock}, Subclass that support only exclusive or
 *     only shared modes need not define the methods supporting the unused mode
 * </p>
 *
 * <p>
 *     This class defines a nested {@link com.lami.tuomatuo.search.base.concurrent.spinlock.MyAbstractQueuedSynchronizer.ConditionObject} class that
 *     can be use as a {@link "Condition} implementation by subclasses
 *     supporting exclusive mode for which method {@link #isHeldExclusively}
 *     reports whether synchronization is exclusively
 *     held with respect to the current thread, method {@link #release}
 *     invoked with the current {@link #getState} value fully releases
 *     this object, and {@link #acquire}, given this saved state value,
 *     eventually restores this object to its previous acquired state. No
 *     {@code KAbstractQueueSynchronizer} method otherwise creates such a
 *     condition, so if this constraint cannot be met, do not use it. The
 *     behavior of {@link "ConditionObject} depends of course on the
 *     semantics if its synchronizer implementation
 * </p>
 *
 * <p>
 *     This class provides inspection(检查), instrumentation and monitoring
 *     methods for the internal queue, as well as similar methods for
 *     condition objects. These can be exported as desired into classes
 *     using an {@code KAbstractQueuedSynchronizer} for their
 *     synchronization mechanics
 * </p>
 *
 * <p>
 *     Serialization of this class stores only the underlying atomic
 *     integer maintaining state, so deserialized objects have empty
 *     thread queues. Typical subclasses requiring serializability will
 *     define a {@code readObject} method that restores this to a known
 *     initial state deserialization
 * </p>
 *
 * <p>
 *     To use this class as the basis of a synchronizer, redefine the
 *     following methods, as applicable by inspecting and or modifying
 *     the synchronization state using {@link #getState}, {@link #setState}
 *     and/or {@link #compareAndSetState}
 * </p>
 *
 * <ul>
 *     <li>{@link #tryAcquire}</li> ReentrantLock
 *     <li>{@link #tryRelease}</li>
 *     <li>{@link #tryAcquireShared}</li> (典型的是在 ReentrantReadWriteLock)
 *     <li>{@link #tryReleaseShared}</li> (典型的是在 ReentrantReadWriteLock)
 *     <li>{@link #isHeldExclusively}</li> 独占锁 需要的
 * </ul>
 *
 * Each of these methods by default throws {@link UnsupportedOperationException}
 * Implementation of these methods
 * must be internally thread-safe, and should in general be short and
 * not block. Defining these methods is the <em>only</em> supported
 * means of using this class. All other methods are declared
 * {@code final} because they cannot be independently varied
 *
 * <p>
 *     You may also find the inherited methods from {@link KAbstractOwnableSynchronizer}
 *     useful to keep track of the thread
 *     owning an exclusive synchronizer. You are encouraged to use them
 *     -- this enables monitoring and diagnostic tools to assist users in
 *     determining which threads hold locks
 * </p>
 *
 * <pre>
 *     Acquire:
 *      while(!tryAcquire(arg)){
 *          <em>enqueue thread if it is not already queued</em>
 *          <em>possibly block current thread</em>
 *      }
 *
 *      Release:
 *      if(tryRelease(arg)){
 *          <em>unlock the first queued thread</em>
 *      }
 * </pre>
 *
 * (Shared mode is similar but may involve cascading signals)
 *
 *  <p id="barging">
 *      Bacause checks in acquire are invoke before
 *      enqueuing , a newly acquiring thread may <em>barge</em> ahead of
 *      others that are blocked and queued. However, you can, if desired,
 *      define {@code tryAcquire} and/or {@code tryAcqureShared} to
 *      disable barging by internally invoking one or more of the inspection
 *      methods, thereby providing a <em>fair</em> FIFO acquisition order.
 *      In particular, most fair synchronizers can define {@code tryAcquire}
 *      to return {@code false} if {@link #"hasQueuedPredecessors"} (a method
 *      specifically designed to be used by fair synchronizers) returns
 *      {@code true}. Other variations are possible
 *  </p>
 *
 * <p>
 *     Throughtput and scalability are generally highest for the
 *     default barging (also known as <em>greedy</em>,
 *     <em>renouncement</em>, and <em>convoy-avoidance</em>) strategy.
 *     While this is not guaranteed to be fair or starvation-free, earlier
 *     queued threads are allowed to recontend before later queued
 *     threads, and each recontention has an unbiased chance to succeed
 *     against incoming threads, Also, while acquires do not
 *     spin; in the usual sense, they may perform multiple
 *     invocations of {@code tryAcquire} interspersed with other
 *     computation before blocking. This gives most of the benefits of
 *     spins when exclusive synchronization is ony briefly held, without
 *     most of the liabilities when it isn't If so desired, you can
 *     argument this by preceding calls to acquire methods with
 *     "fast-path" checks, possibly prechecking {@link #"hasContended"}
 *     and/or {@link #"hasQueuedThreads"} to only do so if the synchronizer
 *     is likely not to be contended
 * </p>
 *
 * <p>
 *     This class provides an efficient and scalable basis for
 *     synchronization in part by specializing its range of use to
 *     synchronizers that can rely on {@code int} state, acquire, and
 *     release parameters, and an internal FIFO wait queue. When this does
 *     not suffice, you can build synchronizers from a lower level using
 *     {@link java.util.concurrent.atomic atomic} classes, your own custom
 *     {@link java.util.Queue} classes, and {@link LockSupport} blocking
 *     support
 * </p>
 *
 * <h3>Usage Example</h3>
 *
 * <p>
 *     Here is a non-reentrant mutual exclusion lock class that uses
 *     the value zero to represent the unlocked state, and one to
 *     represent the locked state. While a non-reentrant lock
 *     does not strictly require recording of the current owner
 *     thread, this class does so anyway to make usage easier to monitor
 *     It also supports conditions and expose
 *     ont of the instrumentation methods
 * </p>
 *
 * <pre>
 *     class Mutex implements Lock, java.io.Serializable{
 *
 *         // Our internal helper class
 *         private static class Sync extends AbstractQueuedSynchronizer{
 *             // reports whether in locked state
 *             protected boolean isHeldExclusively{
 *                 return getState() == 1;
 *             }
 *
 *             // Acquire the lock if state is zero
 *             public boolean tryAcqure(int acquires){
 *                 assert acquires = 1; // Otherwise unused
 *                 if(compareAndSetState(0, 1)){
 *                     setExclusiveOwnerThread(Thread.currentThread());
 *                     return true;
 *                 }
 *                 return false;
 *             }
 *
 *             // Release the lock by setting state to zero
 *             proetected boolean tryRelease(int release){
 *                 assert releases == 1; // Otherwise unused
 *                 if(getState() == 0) throw new IllegalMonitorStateException();
 *                 setExclusiveOwnerThread(null);
 *                 setState(0);
 *                 return true;
 *             }
 *
 *             // Provides a Condition
 *             Condition newCondition() { return new ConditionObject();}
 *
 *             // Deserializers properly
 *             private void readObject(ObjectInputStream s) throw IOException, ClassNotFoundException{
 *                 s.defaultReadObject();
 *                 setState(0); // reset to unlocked state
 *             }
 *         }
 *
 *         // The sync object does all the hard work. We just forward to it
 *         private final Sync sync = new Sync();
 *
 *         private void lock()                  { sync.acquire(1); }
 *         public boolean tryLock()             { return sync.tryAcquire(1); }
 *         public void unlock()                 { sync.release(1); }
 *         public Condition newCondition()      { return sync.newCondition(); }
 *         public boolean isLocked()            { return sync.isHeldExclusively(); }
 *         public boolean hasQueuedThreads()    { return sync.hasQueuedThreads(); }
 *         public void lockInterruptibly() throws InterruptedException{
 *             sync.acquireInterruptibly(1);
 *         }
 *         public boolean tryLock(long timeout, TimeUnit unit) throw interruptedException{
 *             return sync.tryAcquirenanos(1, unit.tonanos(timeout))
 *         }
 *
 *     }
 * </pre>
 *
 * class BooleanLatch {
 *
 *     private static class Sync extends AbstractQueuedSynchronizer{
 *
 *         boolean isSignalled() { return getState() != 0; }
 *
 *         protected int tryAcquireShared(int ignore){
 *             return isSignalled()? 1 : -1;
 *         }
 *
 *         protected boolean tryReleaseShared(int ignore){
 *             setState(1);
 *             return true;
 *         }
 *     }
 *
 *     private final Sync sync = new Sync();
 *     public boolean isSignalled() { return sync.isSignalled(); }
 *     public void signal()         { sync.releaseShared(1); }
 *     public void await() throws InterruptedException {
 *         sync.acquireSharedInterruptibly(1);
 *     }
 * }
 *
 * Created by xujiankang on 2017/1/18.
 */
public abstract class KAbstractQueuedSynchronizer extends KAbstractOwnableSynchronizer implements Serializable {

    protected Logger logger = Logger.getLogger(getClass());

    private static final long serialVersionUID = 7373984972572414691L;

    /**
     * Creates a new {@code KAbstractQueuedSynchronizer} instance
     * with initial synchronization state of zero
     */
    protected KAbstractQueuedSynchronizer(){}

    static final class Node {
        /** marker to indicate a node is wating in shared mode */
        static final Node SHARED = new Node();
        /** marker to indicate a node is waiting in exclusive mode */
        static final Node EXCLUSIVE = null;

        /** waitStatus value yto indicate thread has cancelled */
        static final int CANCELLED = 1;
        /** waitStatus value to indicate successor;s thread needs unparking */
        static final int SIGNAL = -1;
        /** waitStatus value to indicate thread is waiting on condition */
        static final int CONDITION = -2;
        /**
         * waitStatus value to indicate the next acquireShared should
         * unconditionally propagate
         */
        static final int PROPAGATE = -3;

        /**
         * Status field, taking only the values:
         *
         *  SIGNAL:     The successor of this node is (or will soon be)
         *              blocked (via park), so the current node must
         *              unpark its successor when is releases or
         *              cancels. To avoid races, acquire methods must
         *              first indicate they need a signal,
         *              then retry the atomic acquire, and then,
         *              on failure, block
         *
         *
         */
        volatile int waitStatus;

        volatile Node prev;

        volatile Node next;

        volatile Thread thread;

        Node nextWaiter;

        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException{
            Node p = prev;
            if(p == null){
                throw new NullPointerException();
            }else{
                return p;
            }
        }

        Node(){
            // Used to establish initial head or SHARED marker
        }

        Node(Thread thread, Node mode){     // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus){ // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    /**
     * Head of the wait queue, lazily initialized, Except for
     * initialization, it is modified only via method setHead. Note:
     * If head exists, its waitStatus is guaranteed not to be
     * CANCELLED.
     */
    private transient volatile Node head;

    /**
     * Tail of the wait queue, lazily initialized. Modified only via
     * method enq to add new wait node.
     */
    private transient volatile Node tail;

    /**
     * The synchronization state.
     */
    private volatile int state = 0;

    /**
     * Returns the current value of synchronization state.
     * This operation has memory semantics of a {@code volatile} read
     * @return current state value
     */
    protected final int getState(){
        return state;
    }

    /**
     * Sets the value of synchronization state.
     * This operation has memory semantics of a {@code volatile} write.
     * @param newState the new state value
     */
    protected final void setState(int newState){
        state = newState;
    }

    /**
     * Atomically sets synchronization state to the given updated.
     * value if the current state value equals the expected value.
     * This operation has memory semantics of a {@code volatile} read
     * and write
     *
     * @param expect the expect value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that the actual
     *                  value was not equal to the expected value.
     */
    protected final boolean compareAndSetState(int expect, int update){
        // See below for intrinsics(本质) setup to support this
        int oldState = getState();
        logger.info("oldState:"+oldState + ", stateOffset:"+stateOffset + ", expect:"+expect + ", update:"+update);
        return unsafe.compareAndSwapObject(this, stateOffset, expect, update);
    }


    /********************** Queuing utilties ****************/

    /**
     * The number of nanoseconds for which it is faster to spin
     * rather than to use timed park. A rough estimate suffices
     * to improve responsiveness with short timeouts
     */
    static final long spinForTimeoutThreshold = 1000L;

    /**
     * 这个插入会检测head tail 的初始化, 必要的话会初始化一个 dummy 节点, 这个和 ConcurrentLinkedQueue 一样的
     * Insert node into queue, initializing if necessary. See picture above.
     * @param node the node to insert
     * @return node's predecessor 返回的是前继节点
     */
    private Node enq(final Node node){
        for(;;){
            Node t = tail;
            if(t == null){ // Must initialize 初始化一个 dummy 节点 其实和 ConcurrentLinkedQueue 一样
                if(compareAndSetHead(new Node())){
                    tail = head;
                }
            }else{
                node.prev = t;
                if(compareAndSetTail(t, node)){
                    t.next = node;
                    return t;
                }
            }
        }
    }

    /**
     * Creates and enqueues node for current thread and given mode.
     *
     * @param mode Node.EXCLUSIVE for exclusive, Node.SHARED for shared
     * @return the new node
     */
    private Node addWaiter(Node mode){
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if(pred != null){
            node.prev = pred;
            if(compareAndSetTail(pred, node)){
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }

    /**
     * Sets head of queue to be node, thus dequeuing. Called only by
     * acquire methods. Also nulls out unused fields for sake of GC
     * and to suppress unnecessary signals and traversals
     *
     * @param node the node
     */
    private void setHead(Node node){
        head = node;
        node.thread = null;
        node.prev = null;
    }

    /**
     * 唤醒节点 node 的后继节点,
     * Wakes up node's successor, if one exists
     *
     * @param node the node
     */
    private void unparkSuccessor(Node node){
        /**
         * If status is negative (i.e., possibly needing signal) try
         * to clear in anticipation of signalling. It is OK if this
         * fails or if status is changed by waiting thread.
         */
        int ws = node.waitStatus;
        if(ws < 0){
            compareAndSetWaitStatus(node, ws, 0);
        }

        /**
         * Thread to unpark is held in successor, which is normally
         * just the next node. But if cancelled or apparently null,
         * traverse backwards from tail to find the actual
         * non-cancelled successor
         */
        Node s = node.next;
        if(s == null || s.waitStatus > 0){
            s = null;
            for(Node t = tail; t != null && t != node; t = t.prev){
                if(t.waitStatus <= 0){
                    s = t;
                }
            }
        }

        if(s != null){
            LockSupport.unpark(s.thread);
        }
    }

    /**
     * Release action for shared mode -- signals successor and ensures
     * propagation. (Note: For exclusive mode, release just amounts
     * to calling unparkSuccessor of head if it needs signal)
     */
    private void doReleaseShared(){
        /**
         * Ensure that a release propagates, even if there are other
         * in-progress acquires/releases. This proceed in the usual
         * way of trying to unparkSuccessor of the head if it needs
         * signal. But if it does not, status is set to PROPAGATE to
         * ensure that upon release, propagation continues.
         * Additionally, we must loop in case a new node is added
         * while we are doing this. Also, unlike other uses of
         * unparkSuccessor, we need to know if CAS to reset status
         * fails, if so rechecking.
         */
        for(;;){
            Node h = head;
            if(h != null && h != tail){
                int ws = h.waitStatus;
                if(ws == Node.SIGNAL){
                    if(!compareAndSetWaitStatus(h, Node.SIGNAL, 0)){
                        continue; // loop to recheck cases
                    }
                    unparkSuccessor(h);
                }
                else if(ws == 0 &&
                        !compareAndSetWaitStatus(h, 0, Node.PROPAGATE)){
                    continue; // loop on failed CAS
                }
            }

            if(h == head){
                break;
            }

        }
    }

    /**
     * Set head of queue, and checks if successor may be waiting
     * in shared mode, if so propagating if either propagate > 0 or
     * PROPAGATE status was set
     *
     * @param node the node
     * @param propagate the return value from a tryAcquireShared
     */
    private void setHeadAndPropagate(Node node, int propagate){
        Node h = head; // Record old head for check below
        setHead(node);
        /**
         * Try to signal next queued node if:
         *  Propagation was indicated by caller,
         *      or was recorded (as h.waitStatus either before
         *      or after setHead) by a previous operation
         *      (note: this uses sign-check of waitStatus because
         *      PROPAGATE status may transition to SIGNAL)
         *  and
         *      The next node is waiting in shared mode,
         *      or we don't know, because it appears null
         *
         *  The conservation in both of these checks may cause
         *  unnecessary wake-up, but only when there are multiple
         *  racing acquires/releases, so most need signals now or soon
         *  anyway
         */
        if(propagate > 0 || h == null || h.waitStatus < 0 ||
                (h = head) == null || h.waitStatus < 0){
            Node s = node.next;
            if(s == null || s.isShared()){
                doReleaseShared();
            }
        }
    }


    /********************** Utilities for various versions of acquire **************************/


    /**
     * Cancels an ongoing attempt to acquire
     *
     * @param node the node
     */
    private void cancelAcquire(Node node){
        // Ignore if node doesn't exist
        if(node == null){
            return;
        }

        node.thread = null;

        // Skip cancelled predecessors
        Node pred = node.prev;
        while(pred.waitStatus > 0){
            node.prev = pred = pred.prev;
        }

        /**
         * predNext is the apparent node to unsplice. CASes below will
         * fail if not, in which case, we lost race vs another cancel
         * or signal, so no further action is necessary
         */
        Node predNext = pred.next;

        /**
         * Can use unconditional write instead of CAS here.
         * After this atomic step, other Nodes can skip past us,
         * Before, we are free of interference from other threads
         */
        node.waitStatus = Node.CANCELLED;

        // If we are the tail, remove ourselves
        if(node == tail && compareAndSetTail(node, pred)){
            compareAndSetNext(pred, predNext, null);
        }else{
            /**
             * If successor needs signal, try to set pred's next-link
             * so it will get one. Otherwise wake it up to propagate
             */
            int ws;
            if(pred != head &&
                    ((ws = pred.waitStatus) == Node.SIGNAL ||
                            (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
                    pred.thread != null){
                Node next = node.next;
                if(next != null && next.waitStatus <= 0){
                    compareAndSetNext(pred, predNext, next);
                }
            }else{
                unparkSuccessor(node);
            }

            node.next = node; // help GC
        }
    }


    /**
     * Checks and update status for a node that failed to acquire.
     * Returns true if thread should block. This is the main signal
     * control in all acquire loops. Requires that pred == node.prev.
     *
     * @param pred node's predecessor holding status
     * @param node the node
     * @return {@code true} if thread should block
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node){
        int ws = pred.waitStatus;
        if(ws == Node.SIGNAL){
            /**
             * This node has already set status asking a release
             * to signal it, so it can safely park
             */
            return true;
        }

        if(ws > 0){
            /**
             * Predecessor was cancelled. Skip over predecessors and
             * indicate retry
             */
            do{
                node.prev = pred = pred.prev;
            }while(pred.waitStatus > 0);
            pred.next = node;
        }
        else{
            /**
             * waitStatus must be 0 or PROPAGATE. Indicate that we
             * need a signal, but don't park yet. Caller will need to
             * retry to make sure it cannot acquire before parking
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }

        return false;
    }

    /** Convenience method to interrupt current thread */
    static void selfInterrupt(){
        Thread.currentThread().interrupt();
    }

    /**
     * Convenience method to park and then check if interrupted
     *
     * @return {@code true} if interrupt
     */
    private final boolean parkAndCheckInterrupt(){
        LockSupport.park(this);
        return Thread.interrupted();
    }


    /**
     * Various flavors of acquire, varying in exclusive/shared and
     * control modes. Each is mostly the same, but annoyingly
     * different. Only a little bit of factoring is possible due to
     * interaction of exception mechanics (including ensuring that we
     * cancel if tryAcquire throws exception) and other control, at
     * least not without hurting performance too much
     */

    /**
     * Acquires in exclusive uninterruptible mode for thread already in
     * queue. Used by condition wait methods as well as acquire
     *
     * @param node  the node
     * @param arg the acquire argument
     * @return {@code} if interrupted while waiting
     */
    final boolean acquireQueued(final Node node, int arg){
        boolean failed = true;
        try {
            boolean interrupted = false;
            for(;;){
                final Node p = node.predecessor();
                if(p == head && tryAcquire(arg)){
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                if(shouldParkAfterFailedAcquire(p, node) &&
                        parkAndCheckInterrupt()){
                    interrupted = true;
                }
            }
        }finally {
            if(failed){
                cancelAcquire(node);
            }
        }
    }

    /**
     * Acquire in exclusive interruptible mode.
     * @param arg the acquire argument
     */
    private void doAcquireInterruptibly(int arg) throws InterruptedException{
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for(;;){
                final Node p = node.predecessor();
                if(p == head && tryAcquire(arg)){
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }

                if(shouldParkAfterFailedAcquire(p, node) &&
                        parkAndCheckInterrupt()){
                    throw new InterruptedException();
                }
            }
        }finally {
            if(failed){
                cancelAcquire(node);
            }
        }
    }

    /**
     * Acquire in exclusive timed mode
     *
     * @param arg the acquire argument
     * @param nanosTimeout max wait time
     * @return {@code true} if acquired
     */
    private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException{
        if(nanosTimeout <= 0L){
            return false;
        }

        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;

        try {
            for(;;){
                final Node p = node.predecessor();
                if(p == head && tryAcquire(arg)){
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return true;
                }

                nanosTimeout = deadline - System.nanoTime();
                if(nanosTimeout <= 0L){
                    return false;
                }
                if(shouldParkAfterFailedAcquire(p, node) &&
                        nanosTimeout > spinForTimeoutThreshold){
                    LockSupport.parkNanos(this, nanosTimeout);
                }
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

    /**
     * Acquire in shared uninterruptible mode
     * @param arg the acquire argument
     */
    private void doAcquireShared(int arg){
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;

        try {
            boolean interrupted = false;
            for(;;){
                final Node p = node.predecessor();
                if(p == head){
                    int r = tryAcquireShared(arg);
                    if(r >= 0){
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        if(interrupted){
                            selfInterrupt();
                        }
                        failed = false;
                        return;
                    }
                }

                if(shouldParkAfterFailedAcquire(p, node) &&
                        parkAndCheckInterrupt()){
                    interrupted = true;
                }
            }
        }finally {
            if(failed){
                cancelAcquire(node);
            }
        }
    }

    /**
     * Acquire in shared interruptible mode
     * @param arg the acquire argument
     */
    private void doAcquireSharedInterruptibly(int arg) throws InterruptedException{
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;

        try {
            for(;;){
                final Node p = node.predecessor();
                if(p == head){
                    int r = tryAcquireShared(arg);
                    if(r >= 0){
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }

                if(shouldParkAfterFailedAcquire(p, node) &&
                        parkAndCheckInterrupt()){
                    throw new InterruptedException();
                }
            }
        }finally {
            if(failed){
                cancelAcquire(node);
            }
        }
    }

    /**
     * Acquire in shared timed mode
     *
     * @param arg the acquire argument
     * @param nanosTimeout max wait time
     * @return {@code true} if acquired
     */
    private boolean doAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException{
        if (nanosTimeout <= 0L){
            return false;
        }

        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;

        try {
            for(;;){
                final Node p = node.predecessor();
                if(p == head){
                    int r = tryAcquireShared(arg);
                    if(r >= 0){
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return true;
                    }
                }

                nanosTimeout = deadline - System.nanoTime();
                if(nanosTimeout <= 0L){
                    return false;
                }
                if(shouldParkAfterFailedAcquire(p, node) &&
                        nanosTimeout > spinForTimeoutThreshold){
                    LockSupport.parkNanos(this, nanosTimeout);
                }
                if(Thread.interrupted()){
                    throw new InterruptedException();
                }
            }
        }finally {
            if (failed){
                cancelAcquire(node);
            }
        }
    }

    /************************** Main exported methods *********************/

    /**
     * Attempts to acquire in exclusive mode. This method should query
     * if the state of the object permits it to be acquired in the
     * exclusive mode, and if so to acquire it.
     *
     * <p>
     *     This method is always invoked by the thread performing
     *     acquire. If this method reports failure, the acquire method
     *     may queue the thread, if it is not already queued, until it is
     *     signalled by a release from some other thread. This can be used
     *     to implement method {@link "Lock#tryLock}
     * </p>
     *
     * <p>
     *     The default implementation throws {@link UnsupportedOperationException}
     * </p>
     *
     * @param arg the acquire argument. This value is always the one
     *            passed to an acquire method, or is the value saved on entry
     *            to a condition wait. The value is otherwise uninterpreted
     *            and can represent anything you like
     * @return {@code true} if successful. Upon success, this object has
     *              been acquired.
     * @throws IllegalMonitorStateException if acquiring would place this
     *              synchronizer in an illegal state. The excetion must be
     *              thrown in a consistent fashion for synchronization to work
     *              correctly
     * @throws UnsupportedOperationException if exclusive mode is not supported
     */
    protected boolean tryAcquire(int arg){
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in exclusive
     * mode.
     *
     * <p>This method is always invoked by the thread performing release</p>
     *
     * <p>
     *     The default implementation throws {@link UnsupportedOperationException}
     * </p>
     *
     * @param arg the release argument. This value is always the one
     *            passed to a release method. or the current state value upon
     *            entry to a condition wait. The value is otherwise
     *            uninterpreted and can represent anything you like.
     * @return {@code true} if this object is now in a fully released
     *              state, so that any waiting threads may attempt to acquire;
     *              and {@code false} otherwise
     * @throws IllegalMonitorStateException if releasing would place this
     *              synchronizer in an illegal state. This exception must be
     *              thrown in a consistent fashion for synchronization to work
     *              correctly
     * @throws UnsupportedOperationException if exclusive mode is not supported
     */
    protected boolean tryRelease(int arg){
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to acquire in shared mode. This method should query if
     * the state of the object permits it to be acquired in the shared
     * mode, and if so to acquire it
     *
     * <p>
     *     This method is always invoke by the threadperforming
     *     acquire. If this method reports failure. the acquire method
     *     may queue the thread, if it is not already queued, until it is
     *     signalled by a release from some other thread
     * </p>
     *
     * <p>The default implementation throws {@link UnsupportedOperationException}</p>
     *
     * @param arg the acquire argument. This value is always the one
     *            passed to an acquire method, or is the value saved on entry
     *            to a condition wait. The value is otherwise uninterpreted
     *            and can represent anything you like
     * @return a negative value on failure; zero if acquisition in shared
     *              mode succeed but no subsequent shared-mode acquire can
     *              succeed; and a positive value if acquisition in shared
     *              mode succeed and subsequent shared-mode acquires might
     *              also succeed, in which case a subsequent waiting thread
     *              must check availability. (Support for three different
     *              return values enables this method to be used in contexts
     *              where acquires only sometimes act exclusively.) Upon
     *              success, this object has been acquired
     * @throws IllegalMonitorStateException if acquiring would place this
     *              synchronizer in an illegal state. This exception must be
     *              thrown in a consistent fashion for synchronization to work
     *              correctly
     * @throws UnsupportedOperationException if shared mode is not supported
     */
    protected int tryAcquireShared(int arg){
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in shared mode.
     *
     * <p>
     *     This method is always invoked by the thread performing release.
     * </p>
     *
     * <p>
     *     The default implementation throws
     * </p>
     *
     * @param arg the release argument. This value is always the one
     *            passed to a release method, or the current state value upon
     *            entry to a condition wait. The value is otherwise
     *            uninterpreted and can represent anything you like.
     * @return {@code true} if this release of shared mode may permit a
     *              waiting acquire (shared or exclusive) to succeed; and
     *              {@code false} otherwise
     * @throws IllegalMonitorStateException If releasing would place this
     *              synchronizer in an illegal state. This exception must be
     *              thrown in a consistent fashion for synchronization to work
     *              correctly
     * @throws UnsupportedOperationException if shared mode is not supported
     */
    protected boolean tryReleaseShared(int arg){
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if synchronization is held exclusively with
     * respect to the current (calling) thread. This method is invoked
     * upon each call to a non-waiting {@link ConditionObject} method.
     * (Waiting methods instead invoke {@link #release(int)})
     *
     * <p>
     *     The default implementation throws {@link UnsupportedOperationException}
     *     This method is invoked internally only within {@link ConditionObject} methods, so need
     *     not be defined if conditions are not used.
     * </p>
     *
     * @return {@code true} if synchronization is held exclusively
     *          {@code false} otherwise
     * @throws UnsupportedOperationException if conditions are not supported
     */
    protected boolean isHeldExclusively(){
        throw new UnsupportedOperationException();
    }

    /**
     * Acquires in exclusive mode, ignoring interrupts. Implemented
     * by invoking at least once {@link #tryAcquire(int)},
     * returning on success. Otherwise the thread is queued, possibly
     * repeatedly blocking and unblocking, invoking can be used
     * to implement method {@link "Lock#lock}
     *
     * @param arg the acquire argument. This value is conveyed to
     *            {@link #tryAcquire(int)} but is otherwise uninterpreted and
     *            can represent anything you like
     */
    public final void acquire(int arg){
        if(!tryAcquire(arg)&&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg)){
            selfInterrupt();
        }
    }

    /**
     * Acquires in exlusive mode, aborting if interrupted.
     * Implement by the first checking interrupt stats, then invoking
     * at least once {@link #tryAcquire(int)}, returning on
     * success. Otherwise the thread is queued, possibly repeatedly
     * blocking and unblocking, invoking {@link #tryAcquire(int)}
     * until success or the thread is interrupted. This method can be
     * used to implement method {@link "Lock#lockInterruptibly}
     *
     * @param arg the acquire argument. This value is conveyed to
     *            {@link #tryAcquire(int)} but is otherwise uninterpreted and
     *            can represent anything you like.
     * @throws InterruptedException if the current thread is interrupted
     */
    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        if(!tryAcquire(arg)){
            doAcquireInterruptibly(arg);
        }
    }

    /**
     * Attempts to acquire in exclusive mode, aborting if interrupted,
     * and failing if the given timeout elapses. Implemented by first
     * checking interrupt status, then invoking at least once {@link
     * #tryAcquire(int)}, returning on success. Otherwise, the thread is
     * queued, possibly repeatedly blocking and unblocking, invoking
     * {@link #tryAcquire(int)} until success or the thread is interrupted
     * or the timeout elapses. This method can be used to implement
     * method {@link "Lock#tryLock]
     *
     * @param arg the acquire argument, This value is conveyed to
     *          {@link #tryAcquire(int)} but is otherwise uninterpreted and
     *          can represent anything you like
     * @param nanosTimeout the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException if the current thread is interrupted
     */
    public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException{
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
    }

    /**
     * Releasing in exclusive mode. Implemented by unblocking one or
     * more threads if {@link #tryRelease(int)} returns true.
     * This method can be used to implement method {@link "Lock#unlock}.
     *
     * @param arg the release argument. This value is conveyed to
     *            {@link #tryRelease(int)} but is otherwise uninterpreted and
     *            can represent anything you like.
     * @return the value returned from {@link #tryRelease(int)}
     */
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

    /**
     * Acquires in shared mode, ignoring interrupts. Implemented by
     * first invoking at least once {@link #tryAcquireShared(int)},
     * returning on success. Otherwise the thread is queued, possibly
     * repeatedly blocking and unblocking, invoking {@link #tryAcquireShared(int)}
     * until success
     *
     * @param arg the acquire argument. This value is conveyed to
     *            {@link #tryAcquireShared(int)} but is otherwise uninterpreted(不间断)
     *            and can represent anything you like
     */
    public final void acquireShared(int arg){
        if(tryAcquireShared(arg) < 0){
            doAcquireShared(arg);
        }
    }

    /**
     * Acquire in shared mode, aborting if interrupted. Implemented
     * by first checking interrupt status, then invoking at least once
     * {@link #tryAcquireShared(int)}, returning on success. Otherwise the
     * thread is queued, possibly repeatedly blocking and unblocking,
     * invoking {@link #tryAcquireShared(int)} until success or the thread
     * is interrupted
     *
     * @param arg the acquire argument
     *            This value is conveyed to {@link #tryAcquireShared(int)} but is
     *            otherwise uninterpreted and can represent anything
     *            you like
     * @throws InterruptedException if the current thread is interrupted
     */
    public final void acquireSharedInterruptibly(int arg)throws InterruptedException{
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        if(tryAcquireShared(arg) < 0){
            doAcquireSharedInterruptibly(arg);
        }
    }

    /**
     * Attempts to acquire in shared mode. aborting if interrupted, and
     * failing if the given timeout elapses. Implemented by first
     * checking interrupt status, then invoking at least once {@link
     * #tryAcquireShared(int)}, returning on success. Otherwise, the
     * thread is queued, possibly repeatedly blocking and unblocking,
     * invoking {@link #tryAcquireNanos(int, long)} until success or the thread
     * is interrupted or the timeout elapses
     *
     * @param arg the acquire argument. This value is conveyed to
     *            {@link #tryAcquireShared(int)} but is otherwise uninterrupted
     *            and can represent anything you like
     * @param nanosTimeout  the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException if the current thread is interrupted
     */
    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException{
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        return tryAcquireShared(arg) >= 0 ||
                doAcquireSharedNanos(arg, nanosTimeout);
    }

    public final boolean releaseShared(int arg){
        if(tryReleaseShared(arg)){
            doReleaseShared();
            return true;
        }
        return false;
    }

    /******************************************* Queue inspection methods ****************************/

    /**
     * Queries whether any threads are waiting to acquire. Note that
     * because cancellation due to interrupts and timeouts may occur
     * at any time, a {@code true} return does not guarantee that any
     * other will ever acquire
     *
     * <p>
     *     In this implementation, this operation returns in
     *     constant time
     * </p>
     *
     * @return
     */
    public final boolean hasQueuedThreads() {
        return head != tail;
    }

    /**
     * Queries whether any threads have ever contended to acquire this
     * synchronizer; that is if an acquire method has ever blocked
     *
     * <p>
     *     In this omplementation, this operation returns in
     *     constant time
     * </p>
     *
     * @return {@code true} if there has ever been contention
     */
    public final boolean hasContented(){
        return head != null;
    }

    /**
     * Returns the first (longest-waiting) thread in the queue, or
     * {@code null} if no threads are currently queued
     *
     * <p>
     *     In this implementation, this operation narmally returns in
     *     constant time, but may iterate upon contention if other threads are
     *     concurrently modifying the queue
     * </p>
     *
     * @return the first (longest-waiting) thread in the queue, or
     *          {@code null} if no threads are currently queued
     */
    public final Thread getFirstQueuedThread(){
        return (head == tail) ? null : fullGetFirstQueuedThread();
    }

    private Thread fullGetFirstQueuedThread(){
        /**
         * The first node is normally head next. Try to get its
         * thread field, ensuring consistent reads: If thread
         * field is nulled out or s.prev is no longer head, then
         * some other thread(s) concurrently performed sethead in
         * between some of our reads. we try this twice before
         * restorting to traversal
         */

        Node h, s;
        Thread st;

        if((
                (h = head) != null && (s = h.next) != null &&
                        s.prev == head && (st = s.thread) != null ||
                        (
                                (h = head) != null && (s = h.next) != null &&
                                        s.prev == head && (st = s.thread) != null
                                )
                )){
            return st;
        }

        /**
         * Head's next field might not have been set yet, or may have
         * been unset after setHead, So we must check to see if tail
         * is actually first node. If not, we continue on, safely
         * traversing from tail back to head to find first,
         * guaranteeing termination
         */
        Node t = tail;
        Thread firstThread = null;
        while(t != null && t != head){
            Thread tt = t.thread;
            if(tt != null){
                firstThread = tt;
            }
            t = t.prev;
        }
        return firstThread;
    }

    /**
     * Returns true if the given thread is currently queued
     *
     * <p>
     *     This implementation traverses the queue to determine
     *     presence of the given thread
     * </p>
     *
     * @param thread the thread
     * @return {@code true} if the given thread is on the queue
     * @throws NullPointerException if the thread is null
     */
    public final boolean isQueued(Thread thread){
        if(thread == null){
            throw new NullPointerException();
        }
        for(Node p = tail; p != null; p = p.prev){
            if(p.thread == thread){
                return true;
            }
        }
        return false;
    }

    /**
     * Return {@code true} if the apparent first queued thread, if one
     * exists, is waiting in exclusive mode. If this method returns
     * {@code true}, and the current thread is attempting to acquire in
     * shared mode (that is, this method is invoked from {@link
     * #tryAcquireShared(int)}) then it is guaranteed that the current thread
     * is not the first queued thread. Used only as a heuristic in
     * ReentrantReadWriteLock
     */
    public final boolean apparentlyFirstQueuedIsExclusive(){
        Node h, s;
        return (h = head) != null &&
                (s = h.next) != null &&
                !s.isShared()       &&
                s.thread != null;
    }

    /**
     * Quires whether any threads have been waiting to acquire longer
     * than the current thread
     *
     * <p>
     *     An invocation of this method is equivalent to (but may be
     *     more efficient than):
     *     <pre>
     *         {@code getFirstQueuedThread != Thread.currentThread() && hasQueuedThreads()}
     *     </pre>
     *
     *     <p>
     *         Note that because cancellations due to interrupts and
     *         timeouts may occur at any time, a {@code true} return does not
     *         guarantee that some other thread will acquire before the current
     *         thread. Likewise, it is possible for another thread to win a
     *         race to enqueue after this method has returned {@code false}
     *         due to the queue being empty
     *     </p>
     * </p>
     *
     * <p>
     *     This method is designed to be used by a fair synchronizer to
     *     avoid <a href="KAbstractQueuedSynchronizer#barging">barging</a>
     *     Such a synchronizers's {@link #tryAcquire(int)}} method should return
     *     {@code false}, and its {@link #tryAcquireShared(int)} method should
     *     return a negative value, if this method returns {@code true}
     *     (unless this is a reentrant acquire). For example, the {@code
     *     tryAcquire} method for a fair, reentrant, exclusive mode
     *     synchronizer might look like this:
     *
     *     <pre>
     *         {@code
     *         protected boolean tryAcquire(int arg){
     *             if(isHeldExclusively()){
     *                 // A reentrant acquire: increment hold count
     *                 return true;
     *             }else if (hasQueuedPredecessors()){
     *                 return false;
     *             }else{
     *                 // try to acquire normally
     *             }
     *         }
     *         }
     *     </pre>
     * </p>
     *
     * @return {@code true} if there is a queued thread preceding the
     *          current thread, and {@code false} if the current thread
     *          is at the head of the queue or the queue is empty
     */
    public final boolean hasQueuedPredecessors(){
        /**
         * The correctness of this depends on head being initialized
         * before tail and on head next being accurate if the current
         * thread is first in queue
         */
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t &&
                ((s = h.next) == null || s.thread != Thread.currentThread());
    }


    /********************************************* Instrumentation and monitoring methods **************************/

    /**
     * Returns an estimate of the number of threads waiting to
     * acquire. The value is only an estimate because the number of
     * threads may change dynamically while this method traverses
     * internal data structures. This method is designed for use in
     * monitoring system state, not for synchronization
     * control
     *
     * @return the estimated number of threads waiting to acquire
     */
    public final int getQueueLength(){
        int n = 0;
        for(Node p = tail; p != null; p = p.prev){
            if(p.thread != null){
                ++n;
            }
        }
        return n;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire. Because the actual set of threads may change
     * dynamically while constructing this result, the returned
     * collection is only a best-effort estimate. The elements of the
     * returned collection are in no particular order. This method is
     * designed to facilitate construction of subclass that provide
     * more extensive monitoring facilities
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getQueuedThreads(){
        ArrayList<Thread> list = new ArrayList<>();
        for(Node p = tail; p != null; p = p.prev){
            Thread t = p.thread;
            if(t != null){
                list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire inexclusive mode. This has the same properties
     * as {@link #getQueuedThreads()} except that it only returns
     * those threads waiting due to an exclusive acquire
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getExclusiveQueuedThreads(){
        ArrayList<Thread> list = new ArrayList<>();
        for(Node p = tail; p != null; p = p.prev){
            if(!p.isShared()){
                Thread t = p.thread;
                if(t != null){
                    list.add(t);
                }
            }
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire in shared mode. This has the same properties
     * as {@link #getQueuedThreads()} except that it only returns
     * those threads waiting due to a shared acquire
     *
     * @return
     */
    public final Collection<Thread> getSharedQueuedThreads(){
        ArrayList<Thread> list = new ArrayList<>();
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

    /**
     * Returns a string identifying this synchronizer, as well as its state
     * The state, in brackets, includes the String {@code "State = "}
     * followed by the current value of {@link #getState()}, and either
     * {@code "nonempty"} or {@code "empty"} depending on whether the
     * queue is empty
     *
     * @return a string identifying this synchronizer, as well as its state
     */
    public String toString(){
        int s = getState();
        String q = hasQueuedThreads() ? "non" : "";
        return super.toString() + "[State = " + s + ", " + q + " empty queue]";
    }


    /*********************** Internal support methods for Conditions ***********************/

    final boolean isOnSyncQueue(Node node){
        if(node.waitStatus == Node.CONDITION || node.prev == null){
            return false;
        }
        if(node.next != null){ // If has successor, it must be on queue
            return true;
        }

        /**
         * node.prev can be non-null, but not yet on queue because
         * the CAS to place it on queue can fail. So we have to
         * traverse from tail to make sure it actually make it. It
         * will always be near the tail in calls to this method, and
         * unless the CAS failed (which is unlikely), it will be
         * there, so we hardly ever traverse much
         */
        return findNodeFromTail(node);
    }

    /**
     * Returns true if node is on sync queue by searching backwards from tail
     * Called only when needed by isOnSyncQueue
     *
     * @param node
     * @return true if parent
     */
    private boolean findNodeFromTail(Node node){
        Node t = tail;
        for(;;){
            if(t == node){
                return true;
            }
            if(t == null){
                return false;
            }
            t = t.prev;
        }
    }

    /**
     * Transfers a node from a condition queue onto sync queue.
     * Returns true if successful
     *
     * @param node the node
     * @return true if successfully transferred (else the node was
     * cancelled before signal)
     */
    final boolean transferForSignal(Node node){
        /**
         * If cannot change waitStatus, the node has been cancelled
         */
        if(!compareAndSetWaitStatus(node, Node.CONDITION, 0)){
            return false;
        }

        /**
         * Splice onto queue and try to set waitStatus of predecessor to
         * indicate that thread is (probably) waiting, If cancelled or
         * attempt to set waitStatus fails, wake up to resync (in which
         * case the waitStatus can be transiently and harmlessly wrong)
         */
        Node p = enq(node);
        int ws = p.waitStatus;
        if(ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL)){
            LockSupport.unpark(node.thread);
        }
        return true;
    }

    /**
     * Transfers node, if necessary, to sync queue after a cancelled wait.
     * Returns true if thread was cancelled before being signalled
     *
     * @param node the node
     * @return true if cancelled before the node was signalled
     */
    final boolean transferAfterCancelledWait(Node node){
        if(compareAndSetWaitStatus(node, Node.CONDITION, 0)){
            enq(node);
            return true;
        }

        /**
         * If we lost out to a signal(), then we can't proceed
         * until it finishes its enq(). Cancelling during an
         * incomplete transfer is both race and transient, so just
         * spin
         */
        while(!isOnSyncQueue(node)){
            Thread.yield();
        }
        return false;
    }

    /**
     * http://blog.csdn.net/pentiumchen/article/details/43802847
     *
     * Invoke release with current state value; returns saved state.
     * Cancels node and throws exception on failure
     *
     * @param node the condition node for this wait
     * @return previous sync state
     */
    final int fullyRelease(Node node){
        boolean failed = true;
        try{
            int savedState = getState();
            if(release(savedState)){
                failed = false;
                return savedState;
            }else{
                throw new IllegalMonitorStateException();
            }
        }finally {
            if(failed){
                node.waitStatus = Node.CANCELLED;
            }
        }
    }

    /******************** Instrumentation methods for conditions ***************/

    /**
     * Queries whether the given ConditionObject
     * uses this synchronizer as its lock
     *
     * @param condition the condition
     * @return {@code  true} if owned
     */
    public final boolean owns(ConditionObject condition){
        return condition.isOwnedBy(this);
    }



    /**
     * Queries whether any threads are waiting on the given condition
     * associated with this synchronizer. Note that because timeouts
     * and interrupts may occur at any time, a {@code true} return
     * does not guarantee that a future {@code signal} will awaken
     * any threads. This method is designed primarily for use in
     * monitoring of the system state.
     *
     * @param condition the condition
     * @return {@code true} if there are any waiting threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *          is not held
     * @throws IllegalArgumentException if the given condition is
     *          not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final boolean hasWaiters(ConditionObject condition){
        if(!owns(condition)){
            throw new IllegalArgumentException();
        }
        return condition.hasWaiters();
    }

    /**
     * Returns an estimate of the number of threads waiting on the
     * given condition associated with this synchronizer. Note that
     * because timeouts and interrupts may occur at any time, the
     * estimate serves only as an upper bound on the actual number of
     * waiters. This method is designed for use in monitoring of the
     * system state, not for synchronization control
     *
     * @param condition the condition
     * @return the estimate number of waiting threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *          is not held
     * @throws IllegalArgumentException if the given condition is
     *          not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final int getWaitQueueLength(ConditionObject condition){
        if(!owns(condition)){
            throw new IllegalArgumentException("Not owner");
        }
        return condition.getWaitQueueLength();
    }

    /**
     * Returns a collection containing those threads that may be
     * waiting on the given condition associated with this
     * synchronizer. Because the actual set of threads may change
     * dynamically while constructing this result, the returned
     * collection is only a best-effort estimate. The elements of the
     * returned collection are in no particular order
     *
     * @param condition the condition
     * @return the collection og threads
     * @throws IllegalMonitorStateException if exclusive synchronization
     *              is not held
     * @throws IllegalArgumentException if the given condition is
     *              not associated with this synchronizer
     * @throws NullPointerException if the condition is null
     */
    public final Collection<Thread> getWaitingThreads(ConditionObject condition){
        if(!owns(condition)){
            throw new IllegalArgumentException("not owner");
        }
        return condition.getWaitingThreads();
    }



    /******************************* ConditionObject ****************************************/
    /**
     * Condition implementation for a {@link KAbstractOwnableSynchronizer}
     * serving as the basis of a {@link "Lock} implementation
     *
     * <p>
     *     Method documentation for this class describes mechanics
     *     not behavioral specifications from the point of view of Lock
     *     and Condition users, Exported versions of this class will in
     *     general need to be accompanied by documentation describling
     *     condition semantics that rely on these of the associated
     *     {@code KAbstractQueuedSynchronizer}
     * </p>
     *
     * <p>
     *     This class is Serializable, but all fields are transient
     *     so descrialized condition have no waiters
     * </p>
     *
     *
     * 注意点:
     *      1. 所有对 Condition 的操作都是在获取锁后才能操作, 所以 Condition 里面的方法 signal await... 等等不需要考虑线程安全的问题, 并且变量不需要 volatile 来进行修饰
     *
     */
    public class ConditionObject implements Condition, java.io.Serializable{
        private static final long serialVersionUID = 1173984872572414699L;

        /** First node of condition queue */
        private transient Node firstWaiter;
        /** Last node of condition queue */
        private transient Node lastWaiter;

        /** Creates a new {@code ConditionObject} instance */
        public ConditionObject(){}

        /************** Internal methods ************************/

        /**
         * Adds a new waiter to wait queue
         * @return
         */
        private Node addConditionWaiter(){
            Node t = lastWaiter;
            // If lastWaiter is cancelled, clean out
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
         * Removes and transfers nodes until hit non-cancelled one or
         * null. Split out from signal in part to encourage compilers
         * to inline the case of no waiters
         * @param first
         */
        private void doSignal(Node first){
            do{
                if((firstWaiter = first.nextWaiter) == null){
                    lastWaiter = null;
                }
                first.nextWaiter = null;
            }while(!transferForSignal(first) && (first = firstWaiter) != null);
        }

        /**
         * Removes and transfers all nodes
         * @param first (non-null) the first node on condition queue
         */
        private void doSignalAll(Node first){
            lastWaiter = firstWaiter = null;
            do{
                Node next = first.nextWaiter;
                first.nextWaiter = null;
                transferForSignal(first);
                first = next;
            }while(first != null);
        }

        /**
         * http://czj4451.iteye.com/blog/1483264
         *
         * Unlinks cancelled waiter nodes from condition queue
         * Called only while holding lock. This is called when
         * cancellation occured during condition wait, and upon
         * insertion of a new waiter when lastWaiter is seen to have
         * been cancelled. This method is needed to avoid garbage
         * retention in the absence of signals. So even though it may
         * require a full traversal, it comes intot play when
         * timeouts or cancellations all nodes rather than stoppping at a
         * particular target to unlink all pointers to garbege nodes
         * without requiring many re-traversals during cancellation
         * storms
         */
        private void unlinkCancelledWaiters(){
            Node t = firstWaiter;
            Node trail = null;
            while(t != null){
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

        /******************** public method ******************************/

        /**
         * Moves the longest-waiting thread, if one exists, from the
         * wait queue for this condition to the wait queue for the
         * owning lock
         *
         * @throws IllegalMonitorStateException if{@link #isHeldExclusively()}
         *          returns {@code false}
         */
        @Override
        public void signal() {
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            Node first = firstWaiter;
            if(first != null){
                doSignal(first);
            }
        }

        /**
         * Moves all threads from the wait queue for this condition to
         * the wait queue for the owning lock
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively()}
         *          return {@code false}
         */
        public final void signalAll(){
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            Node first = firstWaiter;
            if(first != null){
                doSignalAll(first);
            }
        }

        /**
         * Implements uninterruptible condition wait
         * <li>
         *     Save lock state return by {@link #getState()}
         * </li>
         *
         * <li>
         *     Invoke {@link #release(int)} with saved state as argument,
         *     throwing IllegalMonitoringStateException if it fails
         *     Block until signalled
         *     Reacquire by invoking specified version of
         *     {@link #acquire(int)} with saved state as argument
         * </li>
         */
        public final void awaitUninterruptibly(){
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
         * For interruptible waits, we need to track whether to throw
         * InterruptedException, if interrupted while blocked on
         * condition, versus reinterrupt current thread, if
         * interrupted while blocked waiting to re-acquire
         */

        /** Mode meaning to reinterrupt on exit from wait */
        private static final int REINTERRUPT = 1;
        /** Mode meaning to throw InterruptedException on exit from wait */
        private static final int THROW_IE = -1;

        /**
         * Checks for interrupt, returning THROW_IE if interrupted
         * before signalled, REINTERRUPT if after signalled, or
         * 0 if not interrupted
         */
        private int checkInterruptWhileWaiting(Node node){
            return Thread.interrupted() ?
                    (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) : 0;
        }

        /**
         * Throws InterruptedException, reinterrupts current thread, or
         * does nothing, depending on mode
         */
        private void reportInterruptAfterWait(int interrupMode) throws InterruptedException{
            if(interrupMode == THROW_IE){
                throw new InterruptedException();
            }
            else if(interrupMode == REINTERRUPT){
                selfInterrupt();
            }
        }

        /**
         * Implements interruptible condition wait
         *
         * <li>
         *     If current thread is interrupted, throw InterruptedException
         *     Save lock state returned by {@link #getState()}
         *     Invoke {@link #release(int)} with saved state as argument,
         *     throwing IllegalMonitorStateException if it fails
         *     Blocking until signalled or interrupted
         *     Reacquire by invoking specifized version of
         *     {@link #acquire(int)} with saved state as argument.
         *     If interrupted while blocked in step 4, throw InterruptedException
         * </li>
         *
         * @throws InterruptedException
         */
        @Override
        public final void await() throws InterruptedException {
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            while(!isOnSyncQueue(node)){
                LockSupport.park(this);
                if((interruptMode = checkInterruptWhileWaiting(node)) != 0){
                    break;
                }
            }
            if(acquireQueued(node, savedState) && interruptMode != THROW_IE){
                interruptMode = REINTERRUPT;
            }
            if(node.nextWaiter != null){ // clean up if cancelled
                unlinkCancelledWaiters();
            }
            if(interruptMode != 0){
                reportInterruptAfterWait(interruptMode);
            }
        }

        /**
         * Impelemnts timed condition wait
         *
         * <li>
         *     If current thread is interrupted, throw InterruptedException
         *     Save lock state returned by {@link #getState()}
         *     Invoke {@link #release(int)} with saved state as argument,
         *     throwing IllegalMonitorStateException if it fails
         *     Block until aignalled, interrupted, or timed out
         *     Reacquire by invoking specified version of
         *     {@link #acquire(int)} with saved state as argument
         *     If interrupted while blocked in step 4, throw InterruptedException
         * </li>
         */
        @Override
        public final long awaitNanos(long nanosTimeout) throws InterruptedException {
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            final long deadline = System.nanoTime() + nanosTimeout;
            int interruptMode = 0;
            while(!isOnSyncQueue(node)){
                if(nanosTimeout <= 0L){
                    transferAfterCancelledWait(node);
                    break;
                }
                if(nanosTimeout >= spinForTimeoutThreshold){
                    LockSupport.parkNanos(this, nanosTimeout);
                }
                if((interruptMode = checkInterruptWhileWaiting(node)) != 0){
                    break;
                }
                nanosTimeout = deadline - System.nanoTime();
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
            return deadline - System.nanoTime();
        }

        /**
         * Implements absolute timed condition wait
         * <li>
         *     If current thread is interrupted, throw InterruptedException
         *     Save lock state returned by {@link #getState()}
         *     Invoke {@link #release(int)} with saved state as argument,
         *     throwing IllegalMonitorStateException if it fails
         *     Block until signalled, interrupted, or timed out
         *     Reacquire by invoking specialized version of
         *     {@link #acquire(int)} with saved state as argument
         *     if interrupted while blocked in step 4, throw InterruptedException
         *     If timeed out while blocked in step 4, return false, else true
         * </li>
         */
        @Override
        public boolean awaitUntil(Date deadline) throws InterruptedException {
            long abstime = deadline.getTime();
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean timeout = false;
            int interruptMode = 0;
            while(!isOnSyncQueue(node)){
                if(System.currentTimeMillis() > abstime){
                    timeout = transferAfterCancelledWait(node);
                    break;
                }
                LockSupport.parkUntil(this, abstime);
                if((interruptMode = checkInterruptWhileWaiting(node)) != 0){
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

            return !timeout;
        }

        /**
         * Implements timed condition wait
         *
         * <li>
         *     If current thread is interrupted, throw InterruptedException
         *
         *     Save lock state returned by {@link #getState()}
         *     Invoke {@link #release(int)} with saved state as argument
         *     throwing IllegalMonitorStateException if it fails.
         *     Block until signalled, interrupted, or time out
         *     Reacquire by invoking specialized version of
         *     {@link #acquire(int)} with saved state as argument
         *     If interrupted while blocked in step 4, throw InterruptedException
         *     If timed out while blocked in step 4, return false, else true
         * </li>
         *
         * @param time
         * @param unit
         * @return
         * @throws InterruptedException
         */
        @Override
        public boolean await(long time, TimeUnit unit) throws InterruptedException {
            long nanosTimeout = unit.toNanos(time);
            if(Thread.interrupted()){
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            final long deadline = System.nanoTime() + nanosTimeout;
            boolean timeout = false;
            int interruptMode = 0;

            while(!isOnSyncQueue(node)){
                if(nanosTimeout <= 0L){
                    timeout = transferAfterCancelledWait(node);
                    break;
                }
                if(nanosTimeout >= spinForTimeoutThreshold){
                    LockSupport.parkNanos(this, nanosTimeout);
                }
                if((interruptMode = checkInterruptWhileWaiting(node)) != 0){
                    break;
                }
                nanosTimeout = deadline - System.nanoTime();
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
            return !timeout;
        }

        /******************************* support for instrumentation ********************/

        /**
         * Returns true if this condition was created by the given
         * synchronization object
         */
        final boolean isOwnedBy(KAbstractOwnableSynchronizer sync){
            return sync == KAbstractQueuedSynchronizer.this;
        }

        /**
         * Quires whether any threads are waiting on this condition
         * Implements {@link KAbstractOwnableSynchronizer#"hasWaiters(ConditionObject)}
         *
         * @return {@code true} if there are any waiting threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively()}
         *          returns {@code false}
         */
        protected final boolean hasWaiters(){
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            for(Node w = firstWaiter; w != null; w = w.nextWaiter ){
                if(w.waitStatus == Node.CONDITION){
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns an estimate of the number of threads waiting on
         * this condition
         * Implements {@link KAbstractOwnableSynchronizer#"getWaitQueueLength()}
         *
         * @return the estimated number of waiting threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively()}
         *          return {@code false}
         */
        protected final int getWaitQueueLength(){
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
         * Returns a collection containing those threads that may be
         * waiting on this Condition
         * Implements {@link KAbstractOwnableSynchronizer#'getWaitingThreads}
         *
         * @return the collection of thread
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively()}
         *          returns {@code false}
         */
        protected final Collection<Thread> getWaitingThreads(){
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            ArrayList<Thread> list = new ArrayList<>();
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


    /**
     * CAS head field. Used only by enq
     */
    private final boolean compareAndSetHead(Node update){
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    /**
     * CAS tail field. Used only by enq
     */
    private final boolean compareAndSetTail(Node expect, Node update){
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    /**
     * CAS waitStatus field of a node
     */
    private static final boolean compareAndSetWaitStatus(Node node,
                                                         int expect,
                                                         int update){
        return unsafe.compareAndSwapObject(node, waitStatusOffset, expect, update);
    }

    /** CAS next field of a node */
    private static final boolean compareAndSetNext(Node node,
                                                   Node expect,
                                                   Node update){
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }

    /**
     * Setup to support copareAndSet. We need to natively implement
     * this here: For the sake of permitting future enhancements, we
     * cannot explicitly subclass AtomicInteger, which would be
     * effecient and useful otherwise. So, as the lesser of evils, we
     * natively implement using hotpot intrinsics API. And while we
     * are at it, we do the same for other CASable fields (which could
     * otherwise be done with atomic field updaters)
     */
    private static final Unsafe unsafe;
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static {
        unsafe = UnSafeClass.getInstance();
        Class<?> k = KAbstractQueuedSynchronizer.class;
        try {
            stateOffset = unsafe.objectFieldOffset(KAbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(k.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(k.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }
}
