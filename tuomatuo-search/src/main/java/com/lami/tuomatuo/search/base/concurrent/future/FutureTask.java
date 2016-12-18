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
 * {@link #runAndReset})
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
     *
     *  这几种状态比较重要, 是 FutureTask 中 state 的状态转变的几种情况
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
     *
     * report 方法 根据state值 返回值或抛出异常
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
        // 调用 Executors.callable 将 runnable 转为 callable (对象适配器模式)
        this.callable = Executors.callable(runnable, result);
        this.state = NEW; // ensure visibility of callable
    }

    // CANCELLED = 4, 大于 CANCELLED 的还有 INTERRUPTING 和 INTERRUPTED
    public boolean isCancelled() { return state >= CANCELLED; }

    /**
     * 这里的 isDone 其实指的是 任务有没有开始执行, 所以只要判断 state 是否为 NEW就可以
     * @return
     */
    @Override
    public boolean isDone() {
        return state != NEW;
    }

    /**
     *  注意点
     *  1. 一旦任务的status != NEW 或 进行cas操作失败, 则cancel操作失败
     *     (说明只能对状态是 NEW 的 task的进行cancel, 其他状态的任务进行cancel会返回false,不终止丢到线程池中的任务)
     *  2. cancel 是改变 Thread的中断状态, 调用 Thread.interrupt() 会对 LockSupport.park() 产生影响 (建议自己写个 demo 实践一下)
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
                        // 设置 当前的状态为中断
                        // 若 Thread 的状态位是 interrupt, 并且 对当前线程调用了 LockSupport.park(),
                        // 这线程会抛出 java.lang.InterruptedException: sleep interrupted 异常
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
     * 整个 finishCompletion 操作比较简单, 释放等待队列中的节点
     *
     */
    private void finishCompletion(){
        // assert state > COMPLETING
        // for 循环中第一次进行loop时 if 中执行的cas操作失败, 而第二次if中的cas操作成功
        for(WaitNode q; (q = waiters) != null;){
            if(unsafe.compareAndSwapObject(this, waitersOffset, q, null)){
                for(;;){
                    Thread t = q.thread;
                    if(t != null){
                        q.thread = null;
                        // 唤醒线程
                        LockSupport.unpark(t);
                    }
                    // 将 p.next 赋值给 next 为下次检查做准备
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
    /**
     * 对 result 进行赋值, cas 操作, 没什么可说的
     * @param v
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
        // 进行 cas 操作 更新 state 值, 将 Throwable 赋值给 result
        if(unsafe.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)){
            outcome = t;
            // 直接 cas 操作赋值 state
            unsafe.putOrderedInt(this, stateOffset, EXCEPTIONAL); // final state
            finishCompletion();
        }
    }

    /**
     * run 方法比较简单
     */
    @Override
    public void run() {
        // 判断 state 是否是new, 防止并发重复执行
        if(state != NEW ||
                !unsafe.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread())){
            return;
        }

        try {
            Callable<V> c = callable;
            if(c != null && state == NEW){
                V result ;
                boolean ran;
                try{ // 调用call方法执行计算
                    result = c.call();
                    ran = true;
                }catch (Throwable ex){
                    result = null;
                    ran = false;
                    // 执行中抛异常, 更新state状态, 释放等待的线程(调用finishCompletion)
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
     * computation encounters(遇到) an exception or is cancelled.
     * This is designed for use with tasks that intrinsically(本质) execute more than once
     *
     * @return {@code true} if successfully run and reset
     */
    protected boolean runAndReset(){
        // 进行 cas 操作更新 state 值
        // 这一步操作(cas更新state)其实就能阻止 runAndReset 并发的执行
        if(state != NEW || !unsafe.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread())){
            return false;
        }
        boolean ran = false;
        int s = state;

        try{
            Callable<V> c = callable;
            if(c != null && s == NEW){
                try{
                    // 调用 call 执行方法
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
     *      a. 中断, 退出自旋, 在线程队列中移除对应的节点
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
     * 这个 removeWaiter 个人认为是最搞人的, 尤其在多线程环境中, 同时进行节点的删除
     * Tries to unlinked a time-out
     * @param node
     */
    private void  removeWaiter(WaitNode node, long i){
        logger.info("removeWaiter node"  + node +", i: "+ i +" begin");
        if(node != null){
            node.thread = null; // 将移除的节点的thread＝null, 为移除做标示

            retry:
            for(;;){ // restart on removeWaiter race
                for(WaitNode pred = null, q = waiters, s; q != null; q = s){
                    logger.info("q : " + q +", i:"+i);
                    s = q.next;
                    // 通过 thread 判断当前 q 是否是需要移除的 q节点
                    if(q.thread != null){
                        pred = q;
                        logger.info("q : " + q +", i:"+i);
                    }
                    // 何时执行到这个if条件 ?
                    // hehe 只有第一步不满足时, 也就是q.thread=null (p就是应该移除的节点)
                    else if(pred != null){
                        logger.info("q : " + q +", i:"+i);
                        pred.next = s; // 将前一个节点的 next 指向当前节点的 next 节点
                        // pred.thread == null 这种情况是在多线程进行并发 removeWaiter 时产生的
                        // 而此时真好移除节点 node 和 pred, 所以loop跳到retry, 在进行一次
                        if(pred.thread == null){ // check for race
                            continue retry;
                        }
                    }
                    // 这一步何时操作呢?
                    // 想想 若p是头节点
                    else if(!unsafe.compareAndSwapObject(this, waitersOffset, q, s)){
                        logger.info("q : " + q +", i:"+i);
                        continue retry; // 这一步还是 cheak for race
                    }
                }
                break ;
            }
            logger.info("removeWaiter node"  + node +", i: "+ i +" end");
        }
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        // get 里面的操作没几步, 主要还是在 awaitDone 里面
        int s = state;
        if(s <= COMPLETING){
            s = awaitDone(false, 0L);
        }
        return report(s);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // get(timeout, unit) 也很简单, 主要还是在 awaitDone里面
        if(unit == null){
            throw new NullPointerException();
        }
        int s = state;
        // 判断state状态是否 <= Completing, 调用awaitDone进行旋转
        if(s <= COMPLETING && (s = awaitDone(true, unit.toNanos(timeout))) <= COMPLETING){
            throw new TimeoutException();
        }
        // 根据state的值进行返回结果或抛出异常
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
            // unsafe 类的初始化(通过反射获取)
            // 以后通过 unsafe 类来进行本类的数据的赋值
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
