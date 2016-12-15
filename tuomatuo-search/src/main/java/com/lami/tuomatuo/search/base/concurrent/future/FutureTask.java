package com.lami.tuomatuo.search.base.concurrent.future;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.util.concurrent.*;

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


    @Override
    public void run() {

    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
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
