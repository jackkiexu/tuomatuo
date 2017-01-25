package com.lami.tuomatuo.search.base.concurrent.reentrantlock;

import com.lami.tuomatuo.search.base.concurrent.aqs.KAbstractQueuedSynchronizer;
import com.lami.tuomatuo.search.base.concurrent.spinlock.MyAbstractQueuedSynchronizer;

import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A reentrant mutual exlusion {@link "Lock} with the same basic
 * behavior and semantics as the implicit monitor lock accessed using
 * {@code synchronized} methods and statements, but with extended
 * capabilities
 *
 * <p>
 *     A {@code ReentrantLock} is <em>owned</em> by the thread last
 *     successfully locking, but yet unlocking it, A thread invoking
 *     {@code lock} will return, successfully acquiring the lock, when
 *     the lock is not owned by another thread. The menthod will return
 *     immediately if the current thread already owns the lock. This can
 *     be checked using methods {@link #"isHeldByCurrentThread}, and {@link
 *     #"getHoldCount}
 * </p>
 *
 * <p>
 *     The constructor for this class accepts an optional
 *     <em>fairness</em> parameter, When set {@code true}, under
 *     contenttion, locks facor granting access to the longest-waiting
 *     thread. Otherwise this lock does not guarantee any particular
 *     access order. Program using fair locks accessed by many threads
 *     may idsplay lower overall throughput (i.e are slower, often much
 *     slower) than those using the default setting, but have smaller
 *     variances in times to obtain locks and guarantee lack of
 *     srarvation, Note however, that fairness of locks does not guarantee
 *     fairness of thread scheduling, Thus, one of may threads using a
 *     fair lock many obtain it multiple times in succession while other
 *     active threads are not progressing and not currently holding the
 *     lock
 *     Also not that the untimed {@link #"tryLock()} method does not
 *     honor the fairness setting . It will succeed if the lock
 *     is available event if other threads are waiting
 * </p>
 *
 * <p>
 *     It is recommended practice to <em>always</em> immediately
 *     follow a call to {@code lock} with a {@code try} block, most
 *     typically in a before/after construction such as:
 *
 * </p>
 *
 * <pre>
 *     {@code
 *     class X {
 *         private final ReentrantLock lock = new ReentrantLock();
 *
 *         public void m(){
 *             lock.lock(); // block until condition holds
 *             try{
 *                 // ....... method body
 *             }finally {
 *                 lock.unlock();
 *             }
 *         }
 *     }
 *     }
 * </pre>
 *
 * <p>
 *     In addition to implementation the {@link Lock} interface, this
 *     class defines a number of {@code public} and {@code protected}
 *     methods for inspecting the state of the lock. Some of these
 *     methods are only useful for instrumention and monitoring
 * </p>
 *
 * <p>
 *     Serialization of this class behaves in the same way as built-in
 *     locks: a deserialized lock is in the unlocked state, regardless of
 *     its state when serialized
 * </p>
 *
 * <p>
 *     This lock support a maximum of 2147483647 recursive locks by
 *     the same thread, Attempts to exceed this limit result in
 *     {@link Error} throws from locking methods
 * </p>
 *
 * Created by xjk on 12/18/16.
 */
public class KReentrantLock {

    private static final long serialVersionUID = 7373984872572414699L;

    /** Synchronizer providing all implementation mechanics */
    private final Sync sync;

    /**
     * Base of synchronization control for this lock. Subclassed
     * into fair and nonfair version below, Uses AQS state to
     * represent the number of holds on the lock
     */
    abstract static class Sync extends KAbstractQueuedSynchronizer{
        private static final long serialVersionUID = -5179523762034025860L;

        /**
         * Performs {@link :Lock#Lock}. The main reason for subclassing
         * is to allow fast path for nonfair version
         */
        abstract void lock();

        /**
         * Performs non-fair tryLock, tryAcquire is implemented in
         * subclass, but both need nonfair try for tryLock method
         * @param acquires
         * @return
         */
        final boolean nonfairTryAcquire(int acquires){
            final Thread current = Thread.currentThread();
            int c = getState();
            if(c == 0){
                if(compareAndSetState(0, acquires)){
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if(current == getExclusiveOwnerThread()){
                int nextc = c + acquires;
                if(nextc < 0){ // overflow
                    throw new Error("Maximum lock count exceeded");
                }
                setState(nextc);
                return true;
            }
            return false;
        }

        protected final boolean tryRelease(int releases){
            int c = getState() - releases;
            if(Thread.currentThread() != getExclusiveOwnerThread()){
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if(c == 0){
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }

        protected final boolean isHeldExclusively(){
            /**
             * While we must in general read state before owner,
             * we don't need to do so to check if current thread is owner
             */
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final KAbstractQueuedSynchronizer.ConditionObject newCondition(){
            return new ConditionObject();
        }

        /********************** Methods relayed from outer class **************************/
        final Thread getOwner(){
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        final int getHoldCount(){
            return isHeldExclusively()? getState() : 0;
        }

        final boolean isLocked(){
            return getState() != 0;
        }

        /**
         * Reconsititues the instance from a stream (that is, desrializes it)
         */
        private void readObject(ObjectInputStream s) throws Exception{
            s.defaultReadObject();
            setState(0);
        }
    }

    /**
     * Sync object for non-fair locks
     */
    static final class NonfairSync extends Sync{
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * Perform lock. Try immediate barge, backing up to normal
         * acquire on failure
         */
        @Override
        void lock() {
            if(compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
            }else{
                acquire(1);
            }
        }

        protected final boolean tryAcquire(int acquires){
            return nonfairTryAcquire(acquires);
        }
    }

    /**
     * Sync object for fair locks
     */
    static final class FairSync extends Sync {

        private static final long serialVersionUID = -3000897897090466540L;

        @Override
        final  void lock() {
            acquire(1);
        }

        /**
         * Fair version of tryAcquire. Don't grant access unless
         * recursive call or no waiters or is first
         */
        protected final boolean tryAcquire(int acquires){
            final Thread current = Thread.currentThread();
            int c = getState();
            if(c == 0){
                if(!hasQueuedPredecessors() && compareAndSetState(0, acquires)){
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if(current == getExclusiveOwnerThread()){
                int nextc = c + acquires;
                if(nextc < 0){
                    throw new Error("Maximum lock count exceeded");
                }
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    /**
     * Creates an instance of {@code KReentrantLock}
     * This is equivalent to using {@code KReentrantLock(false)}
     */
    public KReentrantLock() {
        sync = new NonfairSync();
    }

    /**
     * Creates an instance of {@code KReentrantLock} with the
     * given fairness policy
     *
     * @param fair {@code true} if this lock should use a fair ordering policy
     */
    public KReentrantLock(boolean fair){
        sync = fair ? new FairSync() : new NonfairSync();
    }

    /**
     * Acquire the lock
     *
     * <p>
     *     Acquire the lock if it is not held by another thread and returns
     *     immediately, setting the lock hold count to one
     * </p>
     *
     * <p>
     *     If the current thread already holds the lock then the hold
     *     count is incremented by one and the method returns immediately
     * </p>
     *
     * <p>
     *     If the lock is held by another thread than the
     *     current thread become disabled for thread scheduling
     *     purpose and lies dormant until the lock has been acquired,
     *     at which time the lock hold count is set to one.
     * </p>
     *
     */
    public void lock(){
        sync.lock();
    }


    /**
     * Acquires the lock unless the current thread is
     * {@link Thread#interrupt() interrupted}
     *
     * <p>
     *     Acquires the lock if it is not held by another thread and returns
     *     immediately, setting the lock hold count to one
     * </p>
     *
     * <p>
     *     If the current thread already holds this lock then the hold count
     *     is incremented by one and the method returns immediately
     * </p>
     *
     * <p>
     *     If the lock is held by another thread then the
     *     current thread becomes disabled for thread scheduling
     *     purpose and lies dorment until one of two thing happens:
     * </p>
     *
     * <li>
     *     The lock is acquired bvy the current thread then the lock hold
     *     count is set to one
     * </li>
     *
     * <p>
     *     If the current thread has its interrupted status set on entry to this method; or
     *     is {@link Thread#interrupt()} interrupted while acquiring
     *     the lock
     *     then {@link InterruptedException} is thrown and the current thread's
     *     interrupted status is cancelled
     * </p>
     *
     * <p>
     *      In this implementation, as this method is an explicit
     *      interruption point, preference is given to responding to the
     *      interrupt over normal or reentrant acquisition of the lock
     * </p>
     *
     * @throws InterruptedException if the current thread is interrupted
     */
    public void lockInterruptibly() throws InterruptedException{
        sync.acquireInterruptibly(1);
    }

    /**
     * Acquire the lock only if it is not held by another thread and
     * of invocation
     *
     * <p>
     *     Acquires the lock if it is not held by another thread and
     *     returns immediately with the value {@code true}, setting the
     *     lock hold count to one. Even when this lock has been set to use a
     *     fair ordering policy, a call to {@code tryLock()} <em>will</em>
     *     immediately acquire the lock if it available, whether or not
     *     other threads are currentlyt waiting for the lock
     *     This barging bahavior can be useful in certain
     *     circumstances, event though it breaks fairness, If you want to honor
     *     the fairness setting for this lock, then use
     *     {@link #"tryLock(timeout, TimeUnit)} tryLock(0, TimeUnit.SECONDS)
     *     which is almost equivalent (it also detected interruption)
     * </p>
     *
     * <p>
     *     If the current thread already holds this lock then the hold
     *     count is incremented by one and the method returns {@code true}
     * </p>
     *
     * <p>
     *     If the lock is held by another thread then this method will return
     *     immediately with the value {@code false}
     * </p>
     *
     * @return {@code true} if the lock was free and was acquired by the
     *                  current thread, or the lock was already held by the current
     *                  thread; and {@code false} otherwise
     */
    public boolean tryLock(){
        return sync.nonfairTryAcquire(1);
    }

    /**
     * Acquires the lock if it is not held by another thread within the given
     * waiting time and the current thread has not been
     * {@link Thread#interrupt()} interrupted
     *
     * <p>
     *     Acquires the lock if it is not hel by another thread and returns
     *     immediately with the value {@code true}, setting the lock hold count
     *     to one. if this lock has been set to use a fair ordering policy then
     *     an available lock <em>will not</em> be acquired if any other threads
     *     are waiting for the lock. This is contrast to the {@link #tryLock(long, TimeUnit)}
     *     method. If you want a timed {@code tryLock} that does permit barging on
     *     a fair lock then combine the time and un-timed forms together
     * </p>
     *
     * <pre>
     *     {@code
     *     if(lock.tryLock() || lock.tryLock(timeout, unit)){
     *         // ......
     *     }
     *     }
     * </pre>
     *
     * <p>
     *     If the current thread
     *     already holds this lock then the hold count is incremented by one and
     *     the method returns {@code true}
     * </p>
     *
     * <p>
     *     If the lock is held by another thread then the
     *     current thread becomes disabled for thread scheduling
     *     purposes and lies dormat until one of three things happens
     *
     *     The lock is acquired by the current thread; or
     *     Some other thread {@link Thread#interrupt()} interrupts
     *     the current thread or
     *     The specified waiting time elapses
     * </p>
     *
     * <p>
     *     If the lock is acquired then the value {@code true} is returned and
     *     the lock hold count is set to one
     * </p>
     *
     * <p>
     *      If the current thread has its interrupted status set on entry to this method: or
     *      is {@link Thread#interrupt()} interrupt while acquiring the lock
     *      then {@link InterruptedException} is throwns and the current thread's
     *      interrupted status is cleared
     * </p>
     *
     * <p>
     *     If the specified waiting time elapses then the value {@code false}
     *     is returned, If the time is less than or equal to zero, the method
     *     will not wait at all
     * </p>
     *
     * <p>
     *     In this implementation, as this method is an explicit
     *     interruption point, preference is giben to responding to the
     *     interrupt over normal or reentrant acquisition of the lock, and
     *     over reporting the elapse of the waiting time
     * </p>
     *
     * @param timeout the time to wait for the lock
     * @param unit the time unit of the timeout argument
     * @return {@code true} if the lock was free was acquired by the
     *              current thread, or the lock was already held by the current
     *              thread; and {@code false} if the waiting time elapsed before
     *              the lock could be acquired
     * @throws InterruptedException if the current thread is interrupted
     * @throws NullPointerException if the time unit is null
     */
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException{
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    /**
     * Attempts to release this lock
     *
     * <p>
     *     If the current thread is the holder of this lock then hold
     *     count is decremented, If the hold count is now zero then the lock
     *     its released, If the current thread is not the holder of this
     *     lock then {@link IllegalMonitorStateException} is thrown.
     * </p>
     *
     * @throws  IllegalMonitorStateException if the current thread does not
     *          hold this lock
     *
     */
    public void unlock(){
        sync.release(1);
    }

    /**
     * Returns a {@link Condition} instance for use with this
     * {@link "lock} instance
     *
     * <p>
     *     The returned {@link Condition} instance supports the same
     *     usages as do the {@link Object} monitor methods {@link Object#wait()},
     *     {@link Object#notify()} notify, and {@link Object#notifyAll()} when used with the built-in
     *     monitor lock
     * </p>
     *
     * <li>
     *     If  this lock is not held when any of the {@link Condition}
     *     {@link Condition#await()} waiting or {@link Condition#signal()} signalling methods
     *     are called, then an {@link IllegalMonitorStateException}
     * </li>
     *
     * <li>
     *     When the condition {@link Condition#await()} waiting
     *     methods are called the lock is released and, before they
     *     return, the lock reacquired and the lock hold count restored
     *     to what it was when the method was called
     * </li>
     *
     * <li>
     *     If a thread is {@link Thread#interrupt()} interrupted
     *     while waiting then the wait will terminate, an {@link InterruptedException}
     *     will be
     * </li>
     *
     * @return
     */
    public Condition newCondition(){
        return sync.newCondition();
    }
}
