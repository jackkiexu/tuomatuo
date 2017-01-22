package com.lami.tuomatuo.search.base.concurrent.aqs;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.concurrent.locks.LockSupport;

/**
 * Provides a framework for implementing blocking locks locks and related
 * synchronizers (semaphores, event, etc) that rely on
 * first-in-first-out (FIFO) wait queues. This class sidesigned to
 * be a useful basis for most kinds of synchronizers that rely on a
 * single atomic(@code int) value to represent state. Subclasses
 * must define the protected methods that change this state, and which
 * define what that state means in terms of this object being acquired
 * or released. Given these, the other methods in this class carry
 * out all queuing and blocking mechanics. Subclasses can maintain
 * other state fields, but only the atomically updated {@code int}
 * value manipulated using methods {@link #getState}, {@link #setState}
 * and {@link #compareAndSetState} is tracked with repect
 * to synchronization
 *
 * <p>
 *     Subclasses should be defined as non-public internal helper
 *     classes that are used to implement the synchronization properties
 *     of their enclosing class, Class
 *     {@code KAbstractQueueSynchronizer} does not implement any
 *     synchronization interface. Instead it defines methods such as
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
 *     ebentually restores this object to its previous acquired state. No
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
 *     useful to kepp track of the thread
 *     owning an eclusive synchronizer. You are encouraged to use them
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
 * (Shared mode is similar but may invole cascading signals)
 *
 *  <p id="barging">
 *      Bacause checks in acquire are invoke brefore
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
 *     Throughtput and scalability are generally hightest for the
 *     default barging (also known as <em>greedy</em>,
 *     <em>renouncement</em>, and <em>convoy-avoidance</em>) strategy.
 *     While this is not guaranteed to be fair or starvation-free, earlier
 *     queued threads are allowed to recontend before later queued
 *     threads, and each recontention has an unbiased chance to succeed
 *     against incoming threads, Also, while acquires do not
 *     spin; in the usual sence, they may perform multiple
 *     invocations of {@code tryAcquire} interspersed with other
 *     computation before blocking. This gives most of the benefits of
 *     spins when exclusive sunchronization is ony briefly held, without
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
    private volatile int state;

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
    private void doReleasedShared(){
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
                doReleasedShared();
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


    protected boolean tryAcquire(int arg){
        throw new UnsupportedOperationException();
    }

    protected boolean tryRelease(int arg){
        throw new UnsupportedOperationException();
    }

    protected int tryAcquireShared(int arg){
        throw new UnsupportedOperationException();
    }

    protected boolean tryReleaseShared(int arg){
        throw new UnsupportedOperationException();
    }

    protected boolean isHeldExclusively(){
        throw new UnsupportedOperationException();
    }

    public final void acquire(int arg){
        if(!tryAcquire(arg)&&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg)){
            selfInterrupt();
        }
    }

    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        if(!tryAcquire(arg)){
            doAcquireInterruptibly(arg);
        }
    }

    public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException{
        if(Thread.interrupted()){
            throw new InterruptedException();
        }
        return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
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
            stateOffset = unsafe.objectFieldOffset(k.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(k.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(k.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(k.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(k.getDeclaredField("next"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }
}
