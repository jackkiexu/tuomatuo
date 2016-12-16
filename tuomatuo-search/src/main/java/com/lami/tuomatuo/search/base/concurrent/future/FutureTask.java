package com.lami.tuomatuo.search.base.concurrent.future;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * A cancellable asynchronous computation. This class provides a base
 * implementation of {@link java.util.concurrent.Future}, with methods to start an cancel
 * a computation, query to see if the computation is complete, and
 * retrieve the result of the computation. The result can only be
 * retrieved when the computation has completed; the {@code get}
 * methods will block if the computation has not yet completed. Once
 * the computation has completed. The conputation cannot be restarted
 * or cancelled (unless the computation is invoked using
 * {@link #runAndSet})
 *
 * <p>
 *     A {@code FutureTask} can be used to wrap a {@link Callable} or
 *     {@link Runnable} object. Because {@code FutureTask} implements
 *     {@code Runnable}, a {@code FutureTask} can be submitted to an
 *     {@link java.util.concurrent.Executor} for execution
 * </p>
 *
 * <p>
 *     In addition to serving as a standalone class. this class provides
 *     {@code protected} functionality that may be usefully when creating
 *     customized task classes
 * </p>
 * Created by xujiankang on 2016/12/15.
 */
public class FutureTask<V> implements RunnableFuture<V> {

    /**
     * Revision note : This differs from previous versions of this
     * class that relied on AbstractQueuedSynchrounizer. maily to
     * avoid surprising users about retaining interrupt status during
     * cancellation races. Sync control in the current design relies on
     * a "state" field updated via CAS to track completion, along
     * with a simple Treiber stack to hold waiting threads
     *
     * Style note: As usual, we bypass overhead of using
     * AtomicXFieldUpdaters and instead directly use Unsafe intrinsics
     */

    /**
     * The run state of this task, initially NEW. The run state
     * transitions to terminal state only in methods set,
     * setException, and cancel. During completion, state may take on
     * transient values of COMPLETING (while outcome is being set) or
     * INTERRUPTING (only while interrupting the runner to statisfy a
     * cancel(true)). Transitions from these intermediate to final
     * states use cheaper ordered/lazy writes because values unique
     * and cannot be further modified
     *
     * Possible state's transitions
     * NEW -> COMPLETING -> NORMAL
     * NEW -> COMPLETING -> EXCEPTIONAL
     * NEW -> CANCELLED
     * NEW -> INTERRUPTING -> INETRRUPTED
     */

    private volatile int state;
    private static final int NEW             = 0;
    private static final int COMPLETING     = 1;
    private static final int NORMAL          = 2;
    private static final int EXCEPTIONAL    = 3;
    private static final int CANCELLED      = 4;
    private static final int INTERRUPTING   = 5;
    private static final int INTERRUPTED      = 6;

    /** the underlying callable; nulled out after running */
    private Callable<V> callable;
    /** The result to return or exception to throw from get() */
    private Object outcome; // non-volatile, protected by state reads/writes
    /** The thread running the callable; CASed during run() */
    private volatile Thread runner;
    /** Treiber stack of waiting thread */
    private volatile WaitNode waiters;

    /**
     * Return result or throws exception for completed task
     * @param s
     * @return
     * @throws ExecutionException
     */
    private V report(int s) throws ExecutionException{
        Object x = outcome;
        if(s == NORMAL){
            return (V)x;
        }
        if(s >= CANCELLED){
            throw new CancellationException();
        }
        throw new ExecutionException((Throwable)x);
    }

    /**
     * Create a {@code FutureTask} that will, upon running, execute the
     * given {@code Callable}
     *
     * @param callable the callable task
     *
     */
    public FutureTask(Callable<V> callable) {
        if(callable == null) throw new NullPointerException();
        this.callable = callable;
        this.state = NEW; // ensure visibility of callable
    }

    /**
     * Create a {@code FutureTask} that will. upon running, execute the
     * given {@code Runnable}, and arrange that {@code get} will return the
     * given result on successful completion
     *
     * @param runnable the runnable task
     * @param result    the result to return on successful completion. If
     *                  you don't need a particular result, consider using
     *                  construction of the form
     *                  {@code Future<?> f = new FutureTask<Void>(runnable, null)}
     *                  @throws  NullPointerException if the runnable is null
     */
    public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW; // ensure visibility of callable
    }

    public boolean isCancelled() { return state >= CANCELLED; }

    @Override
    public boolean isDone() {
        return state != NEW;
    }

    /**
     *  注意点
     *      1. 一旦任务的status != NEW 或 进行cas操作失败, 则cancel操作失败 (说明只能对状态是 NEW 的 task的进行cancel, 其他状态的任务进行cancel会返回false,不终止丢到线程池中的任务)
     * @param mayInterruptIfRunning
     * @return
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if(!(state == NEW &&
            unsafe.compareAndSwapInt(this, stateOffset, NEW, mayInterruptIfRunning ? INTERRUPTING : CANCELLED ))){
            return false;
        }

        try{ // in case call to interrupt throws exception
            if(mayInterruptIfRunning){
                try{
                    Thread t = runner;
                    if(t != null){
                        t.interrupt();
                    }
                }finally {
                    unsafe.putOrderedInt(this, stateOffset, INTERRUPTED);
                }
            }
        }finally {
            finishCompletion();
        }
        return true;
    }

    /**
     * Removes and signals all waiting threads, invoke done(), and
     * null out callable
     */
    private void finishCompletion(){
        // assert state > COMPLETING
        for(WaitNode q; (q = waiters) != null;){
            if(unsafe.compareAndSwapObject(this, waitersOffset, q, null)){
                for(;;){
                    Thread t = q.thread;
                    if(t != null){
                        q.thread = null;
                        LockSupport.unpark(t);
                    }
                    WaitNode next = q.next;
                    if(next == null){
                        break;
                    }
                    q.next = null; // unlink to help gc
                    q = next;
                }
                break;
            }
        }

        done();
        callable =  null;
    }

    /**
     * Protected method invoked when this task transitions to state
     * {@code isDone} (whether normally or via cancellation). The
     * default implementation does nothing, Subclass may override
     * this method to invoke completion callbacks or perform
     * bookkeeping. Note that you can query status inside the
     * implementation of this method to determine whether this task
     * has been cancelled
     */
    protected void done(){}

    /**
     * Sets the result of this future to given value unless
     * this future has already been set or has been cancelled
     *
     * <p>
     *     This method is invoked internally by the {@link #run()} method
     *     upon successful completion of the compution
     * </p>
     * @param v the value
     */
    protected void set(V v){
        if(unsafe.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)){
            outcome = v;
            unsafe.putOrderedInt(this, stateOffset, NORMAL); // final state
            finishCompletion();
        }
    }

    /**
     * Cause this future to report an {@link ExecutionException}
     * with the given throwable as its cause, unless this future has
     * already been set or has been cancelled
     *
     * <p>
     *     This method is invoked internally by the {@link #run()} method
     *     upon failure of the computation
     * </p>
     *
     * @param t the cause of failure
     */
    protected void setException(Throwable t){
        if(unsafe.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)){
            outcome = t;
            unsafe.putOrderedInt(this, stateOffset, EXCEPTIONAL); // final state
            finishCompletion();
        }
    }

    @Override
    public void run() {
        if(state != NEW ||
                !unsafe.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread())){
            return;
        }

        try {
            Callable<V> c = callable;
            if(c != null && state == NEW){
                V result ;
                boolean ran;
                try{
                    result = c.call();
                    ran = true;
                }catch (Throwable ex){
                    result = null;
                    ran = false;
                    setException(ex);
                }
                if(ran){
                    set(result);
                }
            }
        }finally {
             // runner must be non-null until state is settled to prevent concurrent calls to run()
            runner = null;
            // state must be re-read after nulling runner to prevent leaked interrupts
            int s = state;
            if(s >= INTERRUPTING){
                handlePossibleCancellationInterrupt(s);
            }
        }
    }

    /**
     * Ensures that any interrupt from a possible cancel(true) is only
     * delivered to a task while in run or runAndReset
     * @param s
     */
    private void handlePossibleCancellationInterrupt(int s){
        // It is possible for our interrupter to stall before getting a chance to interrupt us. Let's spin-wait patiently
        if(s == INTERRUPTING){
            while(state == INTERRUPTING){
                Thread.yield();
            }
        }

        // assert state = INTERRUPTED
        /**
         * We want to clear any interrupt we may have received from
         * cancel(true). However, it is permissible to use interrupts
         * as an independent mechanism for a task to communicate with
         * its caller, and there is no way to clear only the cancellation interrupt
         *
         * Thread.interrupted
         */
    }

    /**
     * Executes the computation without setting its result, and then
     * resets this future to initial state, failing to do so if the
     * computation encounters an exception or is cancelled. This is designed for use with tasks that intrinsically execute more
     * than once
     * @return {@code true} if successfully run and reset
     */
    protected boolean runAndReset(){
        if(state != NEW || !unsafe.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread())){
            return false;
        }
        boolean ran = false;
        int s = state;

        try{
            Callable<V> c = callable;
            if(c != null && s == NEW){
                try{
                    c.call(); // don't set result
                    ran = true;
                }catch (Throwable ex){
                    setException(ex);
                }
            }
        }finally {
            // runner must be non-null until state is settled to
            // prevent concurrent calls to run()
            runner = null;
            // state must be re-read after nulling runner to prevent
            // leaked interrupts
            s = state;
            if(s >= INTERRUPTING){
                handlePossibleCancellationInterrupt(s);
            }
        }

        return ran && s == NEW;
    }

    /**
     * Awaits completion or aborts on interrupt or timeout
     *
     * @param timed true if use timed waits
     * @param nanos time to waits, if timed
     * @return state upon completion
     */
    private int awaitDone(boolean timed, long nanos) throws InterruptedException{
        final long deadline = timed ? System.nanoTime() + nanos : 0L;
        WaitNode q = null;
        boolean queued = false;
        for(;;){
            if(Thread.interrupted()){
                removeWaiter(q);
            }
        }
    }

    /**
     * Tries to unlinked a time-out
     * @param node
     */
    private void removeWaiter(WaitNode node){
        if(node != null){
            node.thread = null;
            retry:
            for(;;){ // restart on removeWaiter race
                for(WaitNode pred = null, q = waiters, s; q != null; q = s){
                    s = q.next;
                    if(q.thread != null){
                        pred = q;
                    }else if(pred != null){
                        pred.next = s;
                        if(pred.thread == null){ // check for race
                            continue retry;
                        }
                    }else if(!unsafe.compareAndSwapObject(this, waitersOffset, q, s)){
                        continue retry;
                    }
                    break ;
                }
            }
        }
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        int s = state;
        if(s <= COMPLETING){
            s = awaitDone(false, 0L);
        }
        return report(s);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if(unit == null){
            throw new NullPointerException();
        }
        int s = state;
        if(s <= COMPLETING && (s = awaitDone(true, unit.toNanos(timeout))) <= COMPLETING){
            throw new TimeoutException();
        }
        return report(s);
    }


    static final class WaitNode{
        volatile Thread thread;
        volatile WaitNode next;

        public WaitNode() {
            thread = Thread.currentThread();
        }
    }

    // Unsafe mechanics
    private static Unsafe unsafe;
    private static long stateOffset;
    private static long runnerOffset;
    private static long waitersOffset;

    static {
        try {
            unsafe = UnSafeClass.getInstance();
            Class<?> k = FutureTask.class;
            stateOffset = unsafe.objectFieldOffset((k.getDeclaredField("state")));
            runnerOffset = unsafe.objectFieldOffset(k.getDeclaredField("runner"));
            waitersOffset = unsafe.objectFieldOffset(k.getDeclaredField("waiters"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
        }

    }

}
