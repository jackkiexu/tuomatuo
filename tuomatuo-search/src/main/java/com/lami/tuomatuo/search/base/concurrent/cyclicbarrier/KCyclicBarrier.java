package com.lami.tuomatuo.search.base.concurrent.cyclicbarrier;

import io.netty.util.Timeout;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xjk on 1/29/17.
 */
public class KCyclicBarrier {

    /**
     * Each use of barrier is represented as a generation instance.
     * The generation chenges whenever the barrier is tripped, or
     * is reset. There can be many generations associated with threads
     * using the barrier - due to the non-deterministic way the lock
     * can be active at a time (the one to which {@code count} applies)
     * and all the rest are either broken or tripped
     * There need not be an active generation if there has been a break
     * but no subsequent reset
     */
    private static class Generation{
        boolean broken = false;
    }

    /** The lock for guarding barrier entry */
    private final ReentrantLock lock = new ReentrantLock();
    /** Condition to wait on until tripped */
    private final Condition trip = lock.newCondition();
    /** The number of parties */
    private final int parties;
    /** The command to run when tripped */
    private final Runnable barrierCommand;
    /** The current generation */
    private Generation generation = new Generation();

    /**
     * Number of parties still waiting, Counts down from parties to 0
     * on each generation. It is reset to parties on each new
     * generation or when broken.
     */
    private int count;

    /**
     * Updates state on barrier trip and wakes up everyone.
     * Called only while holding lock.
     */
    private void nextGeneration(){
        // signal completion of last generation
        trip.signalAll();
        // set up next generation
        count = parties;
        generation = new Generation();
    }


    /**
     * Sets current barrier generation as broken and wakes up everyone
     * Called only while holding lock
     */
    private void breakBarrier(){
        generation.broken = true;
        count = parties;
        trip.signalAll();
    }


    /**
     * Main barrier code, covering the various policies
     */
    private int dowait(boolean timed, long nanos)throws InterruptedException, BrokenBarrierException, TimeoutException{
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            final Generation g = generation;

            if(g.broken){
                throw new BrokenBarrierException();
            }

            if(Thread.interrupted()){
                breakBarrier();
                throw new InterruptedException();
            }

            int index = --count;
            if(index == 0){ // triped
                boolean ranAction = false;
                try{
                    final Runnable command = barrierCommand;
                    if(command != null){
                        command.run();
                    }
                    ranAction = true;
                    nextGeneration();
                    return 0;
                }finally {
                    if(!ranAction){
                        breakBarrier();
                    }
                }
            }

            // loop until tripped, broken, interrupted, or timed out
            for(;;){
                try{
                    if(!timed){
                        trip.await();
                    }else if(nanos > 0L){
                        nanos = trip.awaitNanos(nanos);
                    }
                }catch (Exception e){
                    if(g == generation && !g.broken){
                        breakBarrier();
                        throw e;
                    }else{
                        /**
                         * We're about to finish waiting even if we had not
                         * been interrupted, so this interrupt is deemed to
                         * "belong" to subsequent execution
                         */
                        Thread.currentThread().interrupt();
                    }
                }



                if(g.broken){
                    throw new BrokenBarrierException();
                }

                if(g != generation){
                    return index;
                }

                if(timed && nanos <= 0L){
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        }finally {
            lock.unlock();
        }
    }


    /**
     * Creates a new {@code KCyclicBarrier} that will trip when the
     * given number of parties (threads) are waiting upon it, and which
     * will execute the given barrier action when the barrier is trriped.
     *  performed by the last thread entering the barrier
     *
     * @param parties the number of threads that must invoke {@code #await}
     * @param barrierCommand the command to execute when the barrier is
     *                       tripped, or {@code null} if there is no action
     * @throws IllegalArgumentException if {@code parties} is less than 1
     */
    public KCyclicBarrier(int parties, Runnable barrierCommand) {
        if(parties <= 0) throw new IllegalArgumentException();
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierCommand;
    }


    /**
     * Creates a new {@code KCyclicBarrier} that will trip then the
     * given number of parties (threads) are waiting upon it, and
     * does not perform a predefined action when the barrier is tripped
     *
     * @param parties the number of threads that must invoke {@link #"awiat}
     *                before the barrier is tripped
     * @throws IllegalArgumentException if {@code parties} is less than 1
     */
    public KCyclicBarrier(int parties){
        this(parties, null);
    }

    /**
     * Returns the number of parties required to trip this barrier
     *
     * @return the number of partis required to trip this barrier
     */
    public int getParties(){
        return parties;
    }


    /**
     * Waits until all {@link #getParties() parties} have invoked
     * {@code await} on this barrier
     *
     * <p>
     *     If the current thread is not the last to arrive then it is
     *     disabled for thread scheduling purposes and lies dormant until
     *     one of the following things happens:
     *     <li>
     *         The last thread arrives; or
     *         Some other thread {@link Thread#interrupt() interrupts}
     *         the current thread; or
     *         Some other thread {@link Thread#interrupt() interrupts}
     *         one of the other waiting threads; or
     *         Some other thread times out while waiting for barrier; or
     *         Some other thread invokes {@link #"reset} on this barrier.
     *     </li>
     * </p>
     *
     * <p>
     *      If the current thread:
     *      has its interrupted status set on entry to this method; or
     *      is {@link Thread#interrupt() interrupted} while waiting
     *      then {@link InterruptedException} is thrown and current thread's
     *      interrupted status is cleared
     * </p>
     *
     * <p>
     *     If any current thread is the last thread to arrive, and a
     *     non-null barrier action was supplied in the constructor, then the
     *     current thread runs the action before allowing the other threads to
     *     continue.
     *     If an exception occurs during the barrier action then that exception
     *     will be propagated in the current thread and the barrier is placed in
     *     the broken state.
     * </p>
     *
     * @return the arrival index of the current thread, where index
     *          {@code getParties() -1} indicates the first
     *          to arrive and zero indicates the last to arrive
     * @throws InterruptedException ig the current thread was interrupted while waiting
     * @throws BrokenBarrierException if <em>another</em> thread was
     *                  interrupted or timed out while the current thread was
     *                  waiting, or the barrier was reset, or the barrier was
     *                  broken when {@code await} was called, or the barrier
     *                  action (if present) failed due to an exception
     */
    public int await() throws InterruptedException, BrokenBarrierException{
        try{
            return dowait(false, 0L);
        }catch (TimeoutException toe){
            throw new Error(toe); // cannot happen
        }
    }


    public int await(long timeout, TimeUnit unit) throws Exception{
        return dowait(true, unit.toNanos(timeout));
    }


    public boolean isBroken(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return generation.broken;
        }finally {
            lock.unlock();
        }
    }


    public void reset(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            breakBarrier();  // break the current generation
            nextGeneration(); // start a new generation
        }finally {
            lock.unlock();
        }
    }


    public int getNumberWaiting(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return parties - count;
        }finally {
            lock.unlock();
        }
    }
}
