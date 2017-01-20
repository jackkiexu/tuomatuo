package com.lami.tuomatuo.search.base.concurrent.aqs;

import java.io.Serializable;

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
 *     {@link ReadWriteLock}, Subclass that support only exclusive or
 *     only shared modes need not define the methods supporting the unused mode
 * </p>
 *
 * <p>
 *     This class defines a nested {@link com.lami.tuomatuo.search.base.concurrent.spinlock.MyAbstractQueuedSynchronizer.ConditionObject} class that
 *     can be use as a {@link Condition} implementation by subclasses
 *     supporting exclusive mode for which method {@link #isHeldExclusively}
 *     reports whether synchronization is exclusively
 *     held with respect to the current thread, method {@link #release}
 *     invoked with the current {@link #getState} value fully releases
 *     this object, and {@link #acquire}, given this saved state value,
 *     ebentually restores this object to its previous acquired state. No
 *     {@code KAbstractQueueSynchronizer} method otherwise creates such a
 *     condition, so if this constraint cannot be met, do not use it. The
 *     behavior of {@link ConditionObject} depends of course on the
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
 *      to return {@code false} if {@link #hasQueuedPredecessors} (a method
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
 *     "fast-path" checks, possibly prechecking {@link #hasContended}
 *     and/or {@link #hasQueuedThreads} to only do so if the synchronizer
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
    }

}
