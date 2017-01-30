package com.lami.tuomatuo.search.base.concurrent.countdownlatch;

import com.lami.tuomatuo.search.base.concurrent.aqs.KAbstractQueuedSynchronizer;

import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 1/28/17.
 */
public class KCountDownLatch {

    /**
     * Synchronization control For KCountDownLatch
     * Use AQS state to represent count
     */
    private static final class Sync extends KAbstractQueuedSynchronizer{
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count){
            setState(count);
        }

        int getCount(){
            return getState();
        }

        protected int tryAcquireShared(int acquires){
            return (getState() == 0)? 1 : -1;
        }

        protected boolean tryReleaseShared(int release){
            // Decrement count; signal when transition to zero
            for(;;){
                int c = getState();
                if(c == 0){
                    return false;
                }
                int nextc = c - 1;
                if(compareAndSetState(c, nextc)){
                    return nextc == 0;
                }
            }
        }
    }


    private final Sync sync;

    /**
     * Construct a {@code KCountDownLatch} initilized with the given count
     *
     * @param count the number of times {@link #"countDown} must be invoked
     *              before threads can pass through {@link #"await}
     * @throws IllegalArgumentException if {@code count} is negative
     */
    public KCountDownLatch(int count){
        if(count < 0) throw new IllegalArgumentException(" count < 0 ");
        this.sync = new Sync(count);
    }

    /**
     * Cause the current thread to wit until the latch has counted down to
     * zero, unless the thread is {@link Thread#interrupt() interrupted}.
     *
     * <p>
     *     If the current count is zero then this method returns immediately
     * </p>
     *
     * <p>
     *     If the current count is greater than zero then the current
     *     thread becomes disabled for thread scheduling purposes and lies
     *     dormant until one of two things happen:
     *
     *     <li>
     *         The count reaches zero due to invocations of the
     *         {@link #"countDown} method; or
     *     </li>
     *     <li>
     *         Some other thread {@link Thread#interrupt() interrupts}
     *         the current thread;
     *     </li>
     * </p>
     *
     * <p>
     *     If the current thread:
     *     has its interrupted status set on entry to this method; or
     *     is {@link Thread#interrupt() interrupted} while waiting,
     *     then {@link InterruptedException} is thrown and the current thread's
     *     interrupted status is cleared
     * </p>
     *
     * @throws InterruptedException if the current thread is interrupted
     *          while waiting
     */
    public void await() throws InterruptedException{
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is {@link Thread#interrupt() interrupted},
     * or the specified waiting time elapses.
     *
     * <p>
     *     If the current count is greater than zero then the current
     *     thread becomes disabled for thread scheduling purposes and lies
     *     dormant until one of three things happen:
     *     <li>The count reaches zero due to invocations of the</li>
     *     {@link #"countDown} method; or
     *     <li>Some other thread {@link Thread#interrupt() interrupts}</li>
     *     the currentthread; or
     *     <li>The specified waiting time elapses</li>
     * </p>
     *
     * <p>
     *     If the count reaches zero then method returns with the
     *     value {@code true}
     * </p>
     *
     * <p>
     *     If the current thread:
     *     has its interrupted status set on entry to this method; or
     *     is {@link Thread#interrupt() interrupted} while waiting,
     *     then {@link Thread#interrupt() interrupted} is thrown and the current thread's
     *     interrupted status is cleared.
     * </p>
     *
     * <p>
     *     If the specified waiting time elapses then the value {@code false}
     *     is returned. If the time is less than or equal to zero, the method
     *     will not wait at all.
     * </p>
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the {@code timeout} argument
     * @return {@code true} if the count reached zero and {@code false}
     *          if the waiting time elapsed before the count reached zero
     * @throws InterruptedException if the current thread is interrupted
     *          while waiting
     */
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException{
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero
     *
     * <p>
     *     If the current count is greater than zero then it is decremented.
     *     If the new count is zero then all waiting threads are re-enabled for
     *     thread scheduling purposes.
     * </p>
     *
     * <p>
     *     If the current equals zero then nothing happens
     * </p>
     */
    public void countDown(){
        sync.releaseShared(1);
    }

    /**
     * Returns the current count.
     * <p>
     *      This method is typically used for debugging and testing purposes.
     * </p>
     *
     * @return the current count
     */
    public long getCount(){
        return sync.getCount();
    }

    /**
     * Returns a string identifying this latch, as well as its state.
     * The state, in brackets, includes the String {@code " Count = "}
     * followed by the current count.
     *
     * @return a string identifying this latch, as well as its state
     */
    @Override
    public String toString() {
        return super.toString() + " [ Count = " + sync.getCount() + " ] ";
    }
}
