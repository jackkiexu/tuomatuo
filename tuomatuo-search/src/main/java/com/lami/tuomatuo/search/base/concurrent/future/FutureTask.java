package com.lami.tuomatuo.search.base.concurrent.future;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import org.apache.log4j.Logger;
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

    private static final Logger logger = Logger.getLogger(FutureTask.class);

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
     * 调用 awaitDone 进行线程的自旋
     * 自旋一般调用步骤
     *  1) 若支持线程中断, 判断当前的线程是否中断
     *      a. 中断, 退出自旋
     *      b. 进行下面的步骤
     *  2) 将当前的线程构造成一个 WaiterNode 节点, 加入到当前对象的队列里面 (进行 cas 操作)
     *  3) 判断当前的调用是否设置阻塞超时时间
     *      a. 有 超时时间, 调用 LockSupport.parkNanos; 阻塞结束后, 再次进行 自旋 , 还是到同一个if, 但 nanos = 0L, 删除链表中对应的 WaiterdNode, 返回 state值
     *      b. 没 超时时间, 调用 LockSupport.park
     *
     * @param timed true if use timed waits
     * @param nanos time to waits, if timed
     * @return state upon completion
     */
    private int awaitDone(boolean timed, long nanos) throws InterruptedException{
        // default timed = false, nanos = 0, so deadline = 0
        final long deadline = timed ? System.nanoTime() + nanos : 0L;
        WaitNode q = null;
        boolean queued = false;
        for(;;){
            // Thread.interrupted 判断当前的线程是否中断(调用两次会清楚对应的状态位)
            // Thread.interrupt 将当前的线程设置成中断状态
            if(Thread.interrupted()){
                removeWaiter(q, Thread.currentThread().getId());
                throw new InterruptedException();
            }

            int s = state;
            /** 1. s = NORMAL, 说明程序执行成功, 直接获取对应的 V
             */
            if(s > COMPLETING){
                if(q != null){
                    q.thread = null;
                }
                return s;
            }
            // s = COMPLETING ; 看了全部的代码说明整个任务在处理的中间状态, s紧接着会进行改变
            // s 变成 NORMAL 或 EXCEPTION
            // 所以调用 yield 让线程状态变更, 重新进行CPU时间片竞争, 并且进行下次循环
            else if(s == COMPLETING){ // cannot time out yet
                Thread.yield();
            }
            // 当程序调用 get 方法时, 一定会调用一次下面的方法, 对 q 进行赋值
            else if(q == null){
                q = new WaitNode();
            }
            // 判断有没将当前的线程构造成一个节点, 赋值到对象对应的属性里面
            // 第一次 waiters 一定是 null 的, 进行赋值的是一个以 q 为首节点的链表
            else if(!queued){
                queued = unsafe.compareAndSwapObject(this, waitersOffset, q.next = waiters, q);
            }
            // 调用默认的 get()时, timed = false, 所以不执行这一步
            else if(timed){
                // 进行阻塞时间的判断, 第二次循环时, nanos = 0L, 直接 removeWaiter 返回现在 FutureTask 的 state
                nanos = deadline - System.nanoTime();
                if(nanos <= 0L){
                    removeWaiter(q, Thread.currentThread().getId());
                    return state;
                }
                LockSupport.parkNanos(this, nanos);
            }
            // 进行线程的阻塞
            else{
                LockSupport.park(this);
            }
        }
    }

    /**
     * Tries to unlinked a time-out
     * @param node
     */
    private synchronized void  removeWaiter(WaitNode node, long i){
        logger.info("removeWaiter node"  + node +", i: "+ i +" begin");
        if(node != null){
            node.thread = null;

            retry:
            for(;;){ // restart on removeWaiter race
                for(WaitNode pred = null, q = waiters, s; q != null; q = s){
                    logger.info("q : " + q +", i:"+i);
                    s = q.next;
                    if(q.thread != null){ // 通过 thread 判断当前 q 是否是需要移除的 q节点
                        pred = q;
                        logger.info("q : " + q +", i:"+i);
                    }else if(pred != null){
                        logger.info("q : " + q +", i:"+i);
                        pred.next = s; // 将前一个节点的 next 指向当前节点的 next 节点
                        if(pred.thread == null){ // check for race
                            continue retry;
                        }
                    }else if(!unsafe.compareAndSwapObject(this, waitersOffset, q, s)){
                        logger.info("q : " + q +", i:"+i);
                        continue retry;
                    }
                    logger.info("q : " + q +", i:"+i);
                }
                logger.info("q : i:"+i);
                break ;
            }
            logger.info("removeWaiter node"  + node +", i: "+ i +" end");
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
        volatile long threadId;
        volatile Thread thread;
        volatile WaitNode next;

        public WaitNode() {
            thread = Thread.currentThread();
            threadId = Thread.currentThread().getId();
        }

        @Override
        public String toString() {
            return "WaitNode{" +
                    "threadId=" + threadId +
                    ", thread=" + thread +
                    '}';
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
