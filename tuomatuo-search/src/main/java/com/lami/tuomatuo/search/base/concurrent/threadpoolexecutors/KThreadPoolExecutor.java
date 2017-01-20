package com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An {@link ExecutorService} that executes each submitted task usng
 * one of possibly several pooled thread, normally configured
 * using {@link Executors} factory methods
 *
 * <p>
 *     Thread pools address two different problems: they usually
 *     provide improved performance when executing large numbers of
 *     asynchronous tasks, due to reduced per-task invocation overhead,
 *     and they provide a means of bounding and managing the resources,
 *     including hreads, consumed when executing a collection of tasks
 *     Each {@code KThreadPoolExecutor} also maintains some basic
 *     statistics, such as the number of completed tasks
 * </p>
 *
 * <p>
 *     To be useful, across a wide range of contexts, thisclass
 *     provides many adjustable parameters and extensibility
 *     hooks. However, programmers are urged to use the more convenient
 *     {@link Executors} factory method {@link Executors#newCachedThreadPool()}
 *     (unbounded thread pool, with automatic thread reclamation), {@link Executors#newFixedThreadPool(int)}
 *     (fixed size thread pool) and {@link Executors#newSingleThreadExecutor()}
 *     (single background thread), that preconfigure setting for the most common usage
 *     scenarios. Otherwise, use the following guide when manually
 *     configuring and tuning this class:
 * </p>
 *
 * <dt>Core and maximum pool sizes</dt>
 *
 * <dd>
 *     A {@code KThreadPoolExecutor} will automatically adjust the
 *     pool size (see {@link #getPoolSize})
 *     according to the boundd set by
 *     corePoolSize (see {@link #getCorePoolSize}) and
 *     maximumPoolSize (see {@link #getMaimumPoolSize}).
 * </dd>
 *
 * When a new task is submitted in method {@link #execute(Runnable)},
 * and fewer than corePoolSize threads are running, a new thread is
 * created to handle the request, even if other worker threads are
 * idle. If there are more than corePoolSize but less than
 * maximumPoolSize threads running, a new thread will be created only
 * if the queue is full. By setting corePoolSize and maximumPoolSize
 * the same, you create a fixed-size thread pool, By setting
 * maximumPoolSize to an exxentially unbounded value such as {@code
 * Integer.Max_VALUE}, you allow he pool to accommodate an arbitrary
 * number of concurrent tasks. Most typically, core and maximum pool
 * sizes are set only upon construction, but many also be changed
 * dynamically using {@link #setCorePoolSize} and {@link #setMaximumPoolSize}
 *
 * <dt>On-demand construction</dt>
 *
 * <dd>
 *     By default, even core threads are initially created and
 *     started only when new tasks arrive, but can be overridden
 *     dynamically using method {@link #prestartCoreThread} or
 *     {@link #prestartAllCoreThreads}. You probably want to prestart threads if
 *     you construct the pool with a non-empty queue.
 * </dd>
 *
 * <dt>Creating new threads</dt>
 *
 * <dd>
 *     New threads are created using a {@link ThreadFactory}, If not
 *     otherwise specified, a {@link Executors#defaultThreadFactory()} is
 *     used. that creates threads to all be in the same {@link ThreadGroup}
 *     and with the same {@code NORM_PRIORITY} priority and
 *     non-daemon status. By supplying a different ThreadFactory, you can
 *     alter the thread's name, thread group, priority, daemon status,
 *     etc. If a {@code ThreadFactory} fails to create a thread when asked
 *     by returning null from {@code newThread}, the executor will
 *     continue, but might not be able to execute any tasks. Threads
 *     should possess the "modifyThread" {@code RuntimePermisson}, If
 *     worker threads or other threads using the pool do not posses this
 *     permission, service may be degraded: configuration changes may not
 *     task effect in a timely manner, and a shutdown pool may remain in a
 *     state in which termination is possible but nor completed.
  * </dd>
 *
 * <dt>Keep-alive times</dt>
 *
 * <dd>
 *      If the pool currently has more than corePoolSize threads,
 *      excess threads will be terminated if they have been idle for more
 *      than the keepAliveTime (see {@link #getKeepAliveTime(TimeUnit)}).
 *      This provides a means of reducing resource consumption when the
 *      pool is not being actively used. If the pool becomes more active
 *      later,new threads will be constructed. This parameter can also be
 *      changed dynamically using method {@code #setKeepAliveTime(long, TimeUnit)}
 *      . Using a value of (@code Long.MAX_VALUE) {@link
 *      TimeUnit#NANOSECONDS} effectively disables idle threads from ever
 *      terminating prior to shut down. By default, the keep-alive policy
 *      applies only when there are more than corePoolSize threads. But
 *      method {@link #allowCoreThreadTimeOut(boolean)} can be used to
 *      apply this time-out policy to core threads as well, so long as the
 *      keepAliveTime value is non-zero.
 * </dd>
 *
 * <dt>Queuing</dt>
 * <dd>
 *     Any {@link BlockingQueue} may be used to transfer and hold
 *     submitted tasks. The use of this queue interacts with pool sizing:
 * </dd>
 *
 * <li>
 *     If fewer than corePoolSize threads are running, the Executor
 *     always prefers adding a new thread rather than queuing.
 * </li>
 *
 * <li>
 *     If corePoolSize or more threads are running, the Executor
 *     always prefers queuing a request rather than adding a new
 *     thread.
 * </li>
 *
 * <li>
 *     If a request cannot be queued, a new thread is created unless
 *     this would excedd maximumPoolSize, in which case, the task will be
 *     rejected.
 * </li>
 *
 *
 *
 * There are three general strategies for queuing:
 * <li>
 *     <em>Direct handoff.</em>
 *     A good default choice for a work
 *     queue is a {@link com.lami.tuomatuo.search.base.concurrent.synchronousqueue.KSynchronousQueue} that hands off tasks to threads
 *     without otherwise holding them. Here, an attempt to queue a task
 *     will fail if no threads are immediately available to run it, so a
 *     new thread will be constructed. This policy avoids lockups when
 *     handling sets of requests that might have internal dependencies.
 *     Direct handoffs generally require unbounded maximumPoolSizes to
 *     avoid rejection of new submitted task. This in turn admits the
 *     possibility od unbounded thread growth when commands continue to
 *     arrive on average faster than they can be processed.
 * </li>
 *
 * <li>
 *     <em>Unbounded queues.</em>
 *     Using an unbounded queue (for example a {@link LinkedBlockingQueue})
 *     without a predefined capacity)
 *     will cause new tasks to wait in the queue when all
 *     corePoolSize threads are busy. Thus, no more than corePoolSize
 *     threads will ever be created. (And the value of the maximumPoolSize
 *     therefore doesn't have any effect.) This may be appropriate when
 *     each task is completely independent of others, so tasks cannot
 *     affect each other execution; for example, in a web page server.
 *     while this style of queuing can be useful in smoothing out
 *     transient bursts of requests, it adits the possibility of
 *     unbounded work queue growth when commands continue to arrive on
 *     average faster than they can be processed
 * </li>
 *
 * <li>
 *     <em>Bounded queues</em>
 *     A bounded queue (for example, an
 *     {@link ArrayBlockingQueue}) helps prevent resource exhaustion when
 *     used with finite maximumPoolSizes, but can be more exhaustion when
 *     tune and control. Queue sizes and maximum pool sizes may be traded
 *     off for each other: Using large queues and small pools minimizes
 *     CPU usage, OS resources, and context-switching overhead, but can
 *     lead to artificially low throughput. If tass frequently block (for
 *     example if they are I/O bound), a system may be able to schedule
 *     time for more threads than you otherwise allow. Use of small queues
 *     generally requires larger pool sizes, which keeps CUPs busier but
 *     may encounter unacceptable scheduling overhead, which also
 *     decreases throughput.
 * </li>
 *
 * <dt>Rejected tasks</dt>
 *
 * <dd>
 *     New tasks submitted in method {@link #excute(Runnable)} will be
 *     <em>rejected</em>
 *     when the Executor has been shut down, and also when
 *     the Executor uses finite bounds for both maximum threads and work queue
 *     capacity, and is saturated. In either case, the {@code execute} method
 *     invokes the {@link java.util.concurrent.RejectedExecutionHandler}#rejectedExecution
 *     method of its {@link java.util.concurrent.RejectedExecutionHandler}. Four predefined handler
 *     policies are provided:
 * </dd>
 *
 * <ol>
 *     <li>
 *         In the default {@link KThreadPoolExecutor.AbortPolicy}, the handler throws a runtime
 *         {@link RejectedExecutionException} upon
 *         rejection
 *     </li>
 *     <li>
 *         In {@link KThreadPoolExecutor.CallerRunsPolicy}, the thread
 *         that invokes {@code execute} itself runs the task. This provides a
 *         simple feedback control mechanism that will slow down the rate that
 *         new tasks are submitted.
 *     </li>
 *     <li>
 *         In {@link KThreadPoolExecutor.DiscardOldestPolicy}, if the
 *         executor is not shut down, the task at the head of the work queue
 *         is dropped, and then execution is retried (which can fail again,
 *         causing this to be repeated.)
 *     </li>
 *
 *     It is possible  to define and use other kinds of
 *     {@link java.util.concurrent.RejectedExecutionHandler} classes. Doing so requires
 *     some care
 *     especially when policies are designed to work only under particular
 *     capacity or queuin policies
 *
 * </ol>
 *
 * <dt>Hook methods</dt>
 *
 * <dd>
 *     This class provides {@code protected} overridable
 *     {@link #beforeExecute(Thread, Runnable)} and
 *     {@link #afterExecute(Runnable, Throwable)} methods that are called
 *     before and after execution of each task. These can be used to
 *     manipulate the execution environment; for example, reinitializing
 *     ThreadLocals, gathering statistics, or adding long entries.
 *     Additionally. method {@link #terminated} can be overridden to perform
 *     any special processing that needs to be done once the Executor has
 *     fully terminated
 * </dd>
 *
 * <p>
 *     If hook or callback methods throw exceptions, internal worker
 *     threads may in turn fail and abruptly terminate.
 * </p>
 *
 * <dt>Queue maintenance</dt>
 *
 * <dd>
 *     Method {@link #getQueue()} allows access to the work queue
 *     for purposes of monitoring and debugging. Use of this method for
 *     any other purpose is stronglt discouraged. Two supplied methods,
 *     {@link #remove(Runnable)} and {@link #purge} are available to
 *     assist in storage reclamation when large numbers of queued tasks
 *     become cancelled
 * </dd>
 *
 * <dt>Finalization</dt>
 *
 * <dd>
 *     A pool that is no longer referenced in a program
 *     <em>AND</em> has no remaining threads will be {@code shutdown} automatically. If
 *     you would like to ensure that unreferenced pools are reclaimed even
 *     if users forget to call {@link #shutdown}, then you must arrange
 *     that unused threads eventually die, by setting appropriate
 *     keep-alive time, using a lower bound of zero core appropriate
 *     keep-alive time, using a lower bound of zero core threads and/or
 *     setting {@link #allowCoreThreadTimeOut(boolean)}
 * </dd>
 *
 * <p>
 *     <b>Extension example</b>
 *     Most extensions of this class
 *     override one or more of the protected hook methods. For example.
 *     here is a subclass that adds a simple pause/resume feature:
 * </p>
 *
 * class PausableThreadPoolExecutor extends ThreadPoolExecutor{
 *
 *     private boolean isPaused;
 *     private ReentrantLock pauseLock = new ReentrantLock();
 *     private Condition unpaused = pauseLock.newCondition();
 *
 *     public PausableThreadPollExecutor(...) { super(...); }
 *
 *     protected void beforeExecute(Thread t, Runnable r){
 *         super.beforeExecute(t, r);
 *         pauseLock.lock();
 *         try{
 *             while(isPaused)unpaused.await();
 *         }catch(InterruptedException ie){
 *             t.interrupt();
 *         }finally{
 *             pauseLock.unlock();
 *         }
 *     }
 *
 *      public void void pause(){
 *          pauseLock.lock();
 *          try{
 *              isPaused = true;
 *          }finally{
 *              pauseLock.unlock();
 *          }
 *      }
 *
 *      public void resume(){
 *          pauseLock.lock();
 *          try{
 *              isPaused = false;
 *              unpaused.signalAll();
 *          }finally{
 *              pauseLock.unlock();
 *          }
 *      }
 * }
 *
 *
 * Created by xjk on 11/12/16.
 */
public class KThreadPoolExecutor extends AbstractExecutorService {

    /**
     * The main pool control state, ctl, is an atomic interger packing
     * two conceptual fields
     *      workerCount, indicating the effective number of threads
     *      runState, indicating whether running, shutting down etc
     *
     * In order to pack them into one int, we limit workerCount to
     * (2^29) - 1 (about 500 million) threads rather than (2^31) - 1
     * (2 billion) otherwise representable. If this is ever an issue in
     * the future, the variable can be changed to be an AtomicLong
     * and the shift/mask constants below adjusted. But until the need
     * arises, this code is a bit faster and simpler using an int
     *
     * The workerCount is the number of workers that have been
     * permitted to start and not permitted to stop. The value may be
     * transiently different from the actual number of live threads,
     * for example when a ThreadFactory fial to create a thread when
     * asked, and when exiting threads are still performing
     * bookkeeping before terminating. The user-visible pool size is
     * reported as the current size of the wokers set.
     *
     * The runState provides the main lifecycle control, taking on values:
     *
     *  RUNNING:    Accept new tasks and process queued tasks
     *  SHUTDOWN:   Don't accept new tasks, but process queued tasks
     *  STOP:       Don't accept new tasks, don't process queued tasks,
     *              and interrupt in-progress tasks
     *  TIDYING:    All tasks have terminated, workerCount is zero,
     *              the thread transitioning to state TIDYING
     *              will run the terminated() hook method
     *  TERMINATED: terminated() has completed
     *
     *  The numerical order among these values matters, to allow
     *  ordered comparisons. The runState monotomically increase over
     *  time, but need not hit each state. The transitions are :
     *
     *   RUNNING -> SHUTDOWN
     *      On invocation of shutdown(), perhaps implicitly in finalize()
     *   (RUNNING or SHUTDOWN) -> STOP
     *      On invocation of shutdownNow()
     *   SHUTDOWN -> TIDYING
     *      When both queue and pool are empty
     *   TIDYING -> TERMINATED
     *      When the terminated() hook method has completed
     *
     * Thread waiting in awaitTermination() will return when the
     * state reaches TERMINATED.
     *
     * Detecting the transition from SHUTDOWN to TIDYING is less
     * straightforward than you'd like because the queue may become
     * empty after non-empty and vice versa during SHUTDOWN state, but
     * we can only terminate if, after seeing that it is empty, we see
     * that workerCount is 0 (which sometimes entails a reacheck -- see
     * below)
     *
     */
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING      = -1 << COUNT_BITS;
    private static final int SHUTDOWN     = 0 << COUNT_BITS;
    private static final int STOP         = 1 << COUNT_BITS;
    private static final int TIDYING      = 2 << COUNT_BITS;
    private static final int TERMINATED   = 3 << COUNT_BITS;

    // packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }


    /**
     * Bit field accessors that don't require unpacking ctl.
     * These depend on the bit layout and workerCount being never negative
     */
    private static boolean runStateLessThan(int c, int s) { return c < s; }

    private static boolean runStateAtLeast(int c, int s) { return c >= s; }

    private static boolean isRunning(int c) { return c < SHUTDOWN; }

    /** Attempts to CAS-increment the workerCount field of ctl */
    private boolean comparedAndIncrementWorkerCount(int expect) { return ctl.compareAndSet(expect, expect + 1); }

    /** Attempt to CAS-decrement the workerCount field of ctl */
    private boolean compareAndDecrementWorkerCount(int expect) { return ctl.compareAndSet(expect, expect - 1); }

    /**
     * Decrement the workerCount field of ctl. This is called only on
     * abrupt termination of a thread (see processWorkerExit). Other
     * decrement are performed within getTask
     */
    private void decrementWorkerCount(){
        do{}while(!compareAndDecrementWorkerCount(ctl.get()));
    }

    /**
     * The queue used for holding tasks and handing off to worker
     * threads. We do not require that workQueue.poll() returning
     * null necessarily means that workQueue.isEmpty(), so rely
     * solely on isEmpty to see if the queue is empty (which we must
     * do for example when deciding whether to transition from
     * SHUTDOWN to TIDYING). This accommodates special-purpose
     * queues such as DelayQueues for which poll() is allowed to
     * return null even if it may later return non-null when delays
     * expire
     */
    private BlockingQueue<Runnable> workQueue;

    /**
     * Lock held on access to workers set and related bookkeeping
     * While we could use a concurrent set of some sort, it turns out
     * to be generally preferable to use a lock. Among the reasons is
     * that this serializes interruptIdleWorkers, which avoid
     * unnecessary interrupt storms, especially during shutdown.
     * Otherwise exiting threads would concurrently interrupt those
     * that have not yet interrupted. It also simplifies some of the
     * associated statistics bookkeeping of largestPoolSize etc.We
     * also hold mainLock on shutdown and shutdownNow, for the sake of
     * ensuring workers set is stable while separately checking
     * permission to interrupt and actually interrupting
     */
    private final ReentrantLock mainLock = new ReentrantLock();

    /**
     * Set containing all worker threads in pool, Accessed only when
     * holding mainLock
     */
    private final HashSet<Worker> workers = new HashSet<Worker>();

    /**
     * Wait condition to support awaitTermination
     */
    private final Condition termination = mainLock.newCondition();

    /**
     * Tracks largest attained pool size, Accessed only under
     * mainLock
     */
    private int largestPoolSize;

    /**
     * Counter for completed tasks. Updated only on termination of
     * worker threads, Accessed only under mainLock
     */
    private long completedTaskCount;

    /**
     * All user control parameters are declared as volatiles so that
     * ongoing actions are based on freshest values, but without need
     * for locking, since no internal invariants depend on them
     * changing synchronously with respect to other actions
     */
    /**
     * Factory for new theads. All threads are created using this factory
     * (via method addWorker). All callers must be prepared
     * for addWorker to fail, which may refect a system or user's
     * policy limiting the number of threads. Even though it is not
     * treated as an Error, failure to create threads may result in
     * new tasks being rejected or existing ones remaining stuck in the queue
     *
     * we go further and preserve pool invariants even in the face of
     * errors such as OutOgMemoryError. that might be thrown while
     * trying to create threads, Such errors are rather common due to
     * the need to allocate a native stack in Thread start. and users
     * will want to perform clean pool shutdown to clean up. There
     * will likely be enough memory available for the cleanup code to
     * complete without encountering yet another OutMemoryError
     */
    private volatile ThreadFactory threadFactory;

    /** Handler called when saturated or shutdown in execute */
    private volatile RejectedExecutionHandler handler;

    /**
     * Timeout in nanoseconds for idle threads waiting for work
     * Threads use this timeout when there are more than corePoolSize present or if allowCoreThreadTimeOut. Otherwise they wait
     * forever for new work
     */
    private volatile long keepAliveTime;

    /**
     * If false (default), core threads stay alive even when idle.
     * If true, core threads use keepAliveTime to time out waiting
     * for work
     */
    private volatile boolean allowCoreThreadTimeOut;

    /**
     * Core pool size is the minimum number of workers to keep alive
     * (and not allow to time out etc) unless allowCoreThreadTimeOut
     * is set, in which case the minimum is zero
     */
    private volatile int corePoolSize;

    /**
     * Maximum pool size. Note that the actual maximum is internally
     * bounded by CAPACITY
     */
    private volatile int maximumPoolSize;

    /**
     * The default rejected execution handler
     */
    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    /**
     * Permission required for callers of shutdown and shutdownNow
     * We additionally require (checkShutdownAccess) that callers
     * have permission to actually interrupt threads in the woker set
     * (as governed by Thread.interrupt, which relies on
     * ThreadGroup.checkAccess, which in turn relies on
     * SecurityManager,checkAccess). Shutdowns are attempted only if
     * these checks pass
     *
     * All actual invocations of Thread.interrupt (see
     * interruptIdleWorkers and interruptWorkers) ignore
     * SecurityExceptions, meaning that the attempted interrupts
     * silently fail. In the case of shutdown, they should not fail
     * unless the SecurityManager has inconsistent policies, sometimes
     * allowing access to a thread and sometimes not. In such cases,
     * failure to actually interrupt threads may disable or delay full
     * termination. Other uses of interruptIdleWorkers are advisory,
     * and failure to actually interrupt will merely delay response to
     * configuration changes so is not handled exceptionaly
     */
    private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");

    public interface RejectedExecutionHandler {
        void rejectedExecution(Runnable r, AbstractExecutorService executor);
    }

    /**
     * Class Worker mainly maintains interrupt control state for
     * threads running tasks, along with other minor bookkeeping.
     * This class opportunistically extends AbstractQueueSynchronizer
     * to simplify acquiring and releasing a lock surrounding each
     * task execution. This protects against interrupts that are
     * intended to wake up a worker thread waiting for a task from
     * instead interrupting a task being run. We implement a simple
     * non-reentrant mutual exclusion lock rather than use
     *
     */
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable{
        /**
         * This class will never be serialized, but we provide a
         * serialVersionUID to suppress a javac warning
         */
        private static final long serialVersionUID = 6138294804551838833L;

        /** Thread this worker is running in. Null if factory fails */
        Thread thread;
        /** Initial task to run. Possibly null. */
        Runnable firstTask;
        /** Per-thread task counter */
        volatile long completedTasks;

        /**
         * Creates with given first task and thread from ThreadFactory
         * @param firstTask the first task (null if none)
         */
        public Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        /** Delegates main run loop to outer runWorker */
        public void run() {
            runWorker(this);
        }

        /**
         * Lock methods
         *
         * The value 0 represents the unlocked state
         * The value 1 represents the locked state
         */
        protected boolean isHeldExclusively(){
            return getState() != 0;
        }

        protected boolean tryAcquire(int unused){
            if(compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused){
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()          { acquire(1); }
        public boolean tryLock()    { return tryAcquire(1); }
        public void unlock()        { release(1); }
        public boolean isLocked()   { return isHeldExclusively(); }

        void interruptedIfStarted(){
            Thread t;
            if(getState() >= 0 && (t = thread) != null && !t.isInterrupted()){
                try {
                    t.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ThreadFactory getThreadFactory(){
        return threadFactory;
    }

    /**
     * Methods for setting control state
     */

    /**
     * Transitions runState to given target. or leaves it alone if
     * already at least the given target
     *
     * @param targetState the desired state, either SHUTDOWN or STOP
     *                    (but not TIDYING or TERMINATED -- use tryTerminate for that)
     */
    private void advanceRunState(int targetState){
        for(;;){
            int c = ctl.get();
            if(runStateAtLeast(c, targetState) || ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c)))){
                break;
            }
        }
    }

    /**
     * Transitions to TERMINATED state if either (SHUTDOWN and pool
     * and queue empty) or (STOP and pool empty). IF otherwise
     * eligible to terminate but workerCount is nonzero, interrupts an
     * idle worker to ensure that shutdown signals propagate. This
     * method must be called following any action that might make
     * termination possible -- reducing worker count or removing tasks
     * from the queue during shutdown. The method is non-private to
     * allow access from ScheduledThreadPoolExecutor
     */
    final void tryTerminate(){
        for(;;){
            int c = ctl.get();
            if(isRunning(c) || runStateAtLeast(c, TIDYING) || (runStateOf(c) == SHUTDOWN && !workQueue.isEmpty())){
                return;
            }
            if(workerCountOf(c) != 0){
                interruptIdleWorkers(ONLY_ONE);
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();

            try{
                if(ctl.compareAndSet(c, ctlOf(TIDYING, 0))){
                    try{
                        terminated();
                    }finally {
                        ctl.set(ctlOf(TERMINATED, 0));
                        termination.signalAll();
                    }
                    return;
                }
            }finally {
                mainLock.unlock();
            }
            // else retry on failed CAS
        }
    }

    /**
     * Methods for controlling interrupts to worker threads
     */

    /**
     * If there is a security manager, makes sure caller has
     * permission to shut down threads in general (see shutdownPerm).
     * If this passes, additionally makes sure the caller is allowed
     * to interrupted each worker thread. This might not be true even if
     * first check passed. if the SecurityManager treats some threads
     * specially
     */
    private void checkShutdownAccess(){
        SecurityManager security = System.getSecurityManager();
        if(security != null){
            security.checkPermission(shutdownPerm);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try{
                for(Worker w : workers){
                    security.checkAccess(w.thread);
                }
            }finally {
                mainLock.unlock();
            }
        }
    }

    /**
     * Interrupts all threads, even if active. Ignores SecurityExceptions
     * (in which case some threads may remain uninterrupted)
     */
    private void interruptWorker(){
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for(Worker w : workers){
                w.interruptedIfStarted();
            }
        }finally {
            mainLock.unlock();
        }
    }

    /**
     * Interrupts threads that might be waiting for tasks (as
     * indicated by not being locked) so they can check for
     * termination or configuration changes. Ignores
     * SecurityException (in which case some threads may remain
     * uninterrupted)
     *
     * @param onlyOne If true, interrupt at most one worker. This is
     *                called only from tryTerminate when termination is otherwise
     *                enabled but there are still other workers. In this case, at
     *                most one waiting worker is interrupted to propagate shutdown
     *                signals in case all threads are currently waiting.
     *                Interrupting any arbitrary threadensures that newly arriving
     *                workers since shutdown began will also eventually exit
     *                To guarantee eventual termination. it suffices to always
     *                interrupt only one idle worker, but shutdown() interrupts all
     *                idle workers so that redundant workers exit promptly, not
     *                waiting for a straggler task to finish
     *
     */
    private void interruptIdleWorkers(boolean onlyOne){
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try{
            for(Worker w : workers){
                Thread t = w.thread;
                if(!t.isInterrupted() && w.tryLock()){
                    try {
                        t.interrupt();
                    }catch (Exception e){

                    }finally {
                        w.unlock();
                    }
                }

                if(onlyOne) break;
            }
        }finally {
            mainLock.unlock();
        }
    }

    /**
     * Common form of interruptIdleWorkers, to avoid having to
     * remember what the boolean argument means
     */
    private void interruptIdleWorkers(){
        interruptIdleWorkers(false);
    }

    private static final boolean ONLY_ONE = true;

    /**
     * Misc utilities, most of which are also exported to
     * ScheduledThreadPoolExecutor
     */

    /**
     * Invoke the rejected execution handler for the given command
     * Package-protected for use by ScheduledThreadPoolExecutor
     */
    void reject(Runnable command){
        handler.rejectedExecution(command, this);
    }

    /**
     * Performs any further cleanup following run state transition on
     * invocation of shutdown. A no-op here, but used by
     * ScheduledThreadPoolExecutor to cancel delayed tasks
     */
    void onShutdown(){

    }

    /**
     * State check needed by ScheduledThreadPoolExecutor to
     * enable runnng tasks during shutdown
     * @param shutdownOK
     * @return
     */
    boolean isRunnableOrShutdown(boolean shutdownOK){
        int rs = runStateOf(ctl.get());
        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
    }

    /**
     * Drains the task queue into a new list, normally using
     * drainTo. But if the queue is a DelayQueue or any other kind of
     * queue for which poll or drainTo may fail to rmove some
     * elements. it deletes them one by one
     *
     * @return
     */
    private List<Runnable> drainQueue(){
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if(!q.isEmpty()){
            for(Runnable r : q.toArray(new Runnable[0])){
                if(q.remove(r)){
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }

    /**
     * Methods for creating, running and cleaning up after workers
     */

    /**
     * Checks if a new worker can be added with respect to current
     * pol state and the given bound (either core or maximum). if so,
     * the worker count is adjusted accordingly, and, if possible, a
     * new worker is created and started, running firstTask as its
     * first task. This method returns false if the poll is stopped or
     * eligible to shutdown. It also returns false if the thread
     * creation fails, either due to the thread factory returning
     * null, or due to an exception (typically OutOfMemoryError in
     * Thread.start())., we roll back cleanly
     *
     * @param firstTask the task the new thread should run first(or
     *                  null if none). Workers are created with an initial first task
     *                  (in method execute()) to bypass queuing when there are fewer
     *                  than corePoolSize threads (in which case we always start one),
     *                  or when the queue is full (in which case we must bypass queue).
     *                  Initially idle threads are usually created via
     *                  prestartCoreThread or to replace other dying workers
     *
     * @param core if true use corePoolSize as bound, else
     *             maximumPoolSize. (A boolean indicator is used here rather than a
     *             value to ensure reads of fresh values after checking other pool
     *             state)
     * @return true if successful
     */
    private boolean addWorker(Runnable firstTask, boolean core){
        retry:
        for(;;){
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty if necessary
            if(rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())){
                return false;
            }

            for(;;){
                int wc = workerCountOf(c);
                if(wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize)){
                    return false;
                }
                if(comparedAndIncrementWorkerCount(c)){
                    break retry;
                }
                c= ctl.get();
                if(runStateOf(c) != rs){
                    continue  retry;
                } // else CAS failed due to workerCount change; retry inner loop
            }
        }

        boolean workerStated = false;

        boolean workerAdded = false;
        Worker w = null;

        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if(t != null){
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    /** Recheck while holding lock
                     *  Back out on ThreadFactory failure or if
                     *  shut down before lock acquired
                     */
                    int rs = runStateOf(ctl.get());
                    if(rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)){
                        if(t.isAlive()){
                            throw new IllegalThreadStateException();
                        }
                        workers.add(w);
                        int s = workers.size();
                        if(s > largestPoolSize){
                            largestPoolSize = s;
                        }
                        workerAdded = true;
                    }

                }finally {
                    mainLock.lock();
                }
                if(workerAdded){
                    t.start();
                    workerStated = true;
                }
            }
        }finally {
            if(!workerStated){
                addWorkerFailed(w);
            }
        }

        return workerStated;
    }


    /**
     * Rolls back worker thread creation
     * - removes worker from workers, if present
     * - decrement worker count
     * - rechecks for termination, in case the existence of this
     * worker was holding up termination
     */
    private void addWorkerFailed(Worker w){
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if(w != null){
                workers.remove(w);
            }
            decrementWorkerCount();
            tryTerminate();
        }finally {
            mainLock.unlock();
        }
    }

    /**
     * Performs cleanup and bookkeeping for a dying worker. Called
     * only from worker threads. Unless completeAbruptly is set
     * assumes that workerCount has already been adjusted to account
     * for exit. This method removes thread from worker set, and
     * possibly terminates the pool or replaces the worker if either
     * it exited dur to user task exception or if fewer than
     * corePoolSize workers are running or queue is non-empty but
     * there no workers
     *
     * @param w the worker
     * @param completedAbruptly if the worker died due to user exception
     */
    private void processWorkerExit(Worker w, boolean completedAbruptly){
        if(completedAbruptly){ // if abrupt, then workerCount wan't adjusted
            decrementWorkerCount();
        }

        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try{
            completedTaskCount += w.completedTasks;
        }finally {
            mainLock.unlock();
        }

        tryTerminate();

        int c = ctl.get();
        if(runStateLessThan(c, STOP)){
            if(!completedAbruptly){
                int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
                if(min == 0 && !workQueue.isEmpty()){
                    min = 1;
                }
                if(workerCountOf(c) >= min){
                    return; // replacement not needed
                }
            }
            addWorker(null, false);
        }
    }

    /**
     * Perform blocking or timed wait for a task, depending on
     * current configuration settings, or returns null if this worker
     * must exit because of any of:
     * 1. There are more than maximumPoolSize workers (due to a call to
     * setMaximumPoolSize).
     * 2. The pool is stoped
     * 3. The pool is shutdown and the queue is empty.
     * 4. This worker timed out waiting for a task, and time-out
     * workers are subject to termination (that is,
     * {@code allowCoreThreadTimeOut || workerCount > corePoolSize})
     * both before and after the timed wait, and if the queue is
     * non-empty, this worker is not the last thread in the pool.
     *
     * @return task, or null if the worker must exit, in which case
     *          workerCount is decremented
     */
    private Runnable getTask(){
        boolean timeOut = false; // Did the last poll() time out?

        for(;;){
            int c = ctl.get();
            int rs = runStateOf(c);

            // check if queue empty only if necessary
            if(rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())){
                decrementWorkerCount();
                return null;
            }

            int wc = workerCountOf(c);

            // Are workers subject to culling?
            boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

            if((wc > maximumPoolSize || (timed && timeOut))
                    && (wc > 1 || workQueue.isEmpty())){
                if(compareAndDecrementWorkerCount(c)){
                    return null;
                }
                continue;
            }

            try{
                Runnable r = timed ?
                        workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                        workQueue.take();
                if(r != null){
                    return r;
                }
                timeOut = true;
            }catch (InterruptedException retry){

            }
        }
    }

    /**
     * Main worker run loop. Repeatedly gets tasks from queue and
     * executes them, while coping with a number of issue:
     *
     * 1. We may start out with an initial task, in which case we
     * don't need to get the first one. Otherwise, as long as pool is
     * running, we get tasks from getTask. If it returns null then the
     * worker exits due to changged pool state or configuration
     * parameters. Other exits result from exception throws in
     * external code, in which case completeAbruptly holds, in which
     * usually leads processWorkerExit to replace this thread.
     *
     * 2. Before running any task, the lock is acquired to prevent
     * other pool interrupts while task is executing, and then we
     * ensure that unless pool is stopping, this thread does not have
     * its interrupt set.
     *
     * 3. Each task run is preceded by a call to beforeExecute, which
     * might throw an exception, in which case we cause thread to die
     * (break loop with completeAbruptly true) without precessing
     * the task.
     *
     * 4. Assuming beforeExecute completes normally, we run the task,
     * gathering any of its thrown exceptions send to afterExecute.
     * We separately handle RuntimeException, Error (both of which the
     * specs guarantee that we trap) and arbitrary Throwables.
     * Because we cannot rethrow Throwables within Runnable.run, we
     * wrap them within Errors on the way out (to the hthread's
     * UncaughtExceptionHandler). Any thrown exception also
     * conservatively causes thread to die
     *
     * 5. After task.run completes, we call afterExecute, which may
     * also throw an exception, which will also cause thread to
     * die. According to JLS Sec 14.20, this exception is the one that
     * will be in effect even if task.run throws
     *
     * The net effect of the exception mechanics is that afterExecute
     * and the thread's UncaughtExceptionHandler have as accurate
     * information as we can provide about any problems encountered by
     * user code.
     *
     * @param w the worker
     */
    final void runWorker(Worker w){
        Thread wt = Thread.currentThread();
        Runnable task = w.firstTask;
        w.firstTask = null;
        w.unlock(); // allow interrupts
        boolean completedAbruptly = true;
        try{
            while(task != null || (task = getTask()) != null){
                w.lock();;
                /**
                 * If pool is stopping, ensure thread is interrupted;
                 * if not, ensure thread is not interrupted. This
                 * requires a recheck in second case to deal with
                 * shutdownNow race while clearing interrupt
                 */
                if((runStateAtLeast(ctl.get(), STOP )||
                                (Thread.interrupted() &&
                                        runStateAtLeast(ctl.get(), STOP))) &&
                        !wt.isInterrupted()
                        ){
                    wt.interrupt();
                }

                try{
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try{
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    }finally {
                        afterExecute(task, thrown);
                    }

                }finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
            completedAbruptly = false;
        }finally {
            processWorkerExit(w, completedAbruptly);
        }
    }

    /**
     * Creates a new {@code KThreadPoolExecutor} with the given initial
     * parameters and default thread factory and rejected execution handler.
     * It may be be more convenient to use one of the {@link Executors} factory
     * method instead of this general purpose constructor
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *                     if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *                        pool
     * @param keepAliveTime when the number of threads is greater than
     *                      the core. this is the maximum time that excess idle threads
     *                      will wait for new tasks before terminating
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for use for holding tasks before they are
     *                  executed. This queue will hold only the {@code Runnable}
     *                  tasks submitted by the {@code execute} method
     */
    public KThreadPoolExecutor(int corePoolSize,
                               int maximumPoolSize,
                               long keepAliveTime,
                               TimeUnit unit,
                               BlockingQueue<Runnable> workQueue
    ){
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), defaultHandler);
    }

    /**
     * Creates a new {@code KThreadPoolExecutor} with the given initial
     * parameters
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *                     if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of the threads to allow in the
     *                        pool
     * @param keepAliveTime when the number of the threads is greater than
     *                      the core. this is the maximum time that excess idle threads
     *                      will wait for new tasks before terminating
     * @param unit  the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *                  executed. This queue will hold nly the {@code Runnable}
     *                  tasks submitted by the {@code execute} method
     * @param threadFactory the factory to use when the executor
     *                      creates a new thread
     * @param handler the handler to use when execution is blocked
     *                because the thread bounds and queue capacities are reached
     */
    public KThreadPoolExecutor(int corePoolSize,
                               int maximumPoolSize,
                               long keepAliveTime,
                               TimeUnit unit,
                               BlockingQueue<Runnable> workQueue,
                               ThreadFactory threadFactory,
                               RejectedExecutionHandler handler
                               ){
        if(corePoolSize < 0 ||
                maximumPoolSize <= 0 ||
                maximumPoolSize < corePoolSize ||
                keepAliveTime < 0){
            throw new IllegalArgumentException();
        }
        if(workQueue == null || threadFactory == null || handler == null){
            throw new NullPointerException();
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }



    protected void terminated() {}

    public void shutdown() {

    }

    public List<Runnable> shutdownNow() {
        return null;
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void execute(Runnable command) {

    }

    public static class AbortPolicy implements RejectedExecutionHandler{

        public AbortPolicy(){}

        public void rejectedExecution(Runnable r, AbstractExecutorService executor) {
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
        }
    }

    protected void beforeExecute(Thread t, Runnable r) {}

    protected void afterExecute(Runnable r, Throwable t) {}
}