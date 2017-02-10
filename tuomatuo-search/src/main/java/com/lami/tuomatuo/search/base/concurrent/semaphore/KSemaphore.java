package com.lami.tuomatuo.search.base.concurrent.semaphore;

import com.lami.tuomatuo.search.base.concurrent.aqs.KAbstractQueuedSynchronizer;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * http://www.cnblogs.com/go2sea/p/5625536.html
 * A counting semaphore. Conceptually, a semaphore maintains a set of
 * permits. Each {@link #acquire()} blocks if necessary until a permit is
 * available, and then takes it. Each {@link #release()} adds a permit,
 * potentially releasing a blocking acquirer.
 *
 * However. no actual permit object are used; the {@code Semaphore} just
 * keeps a count of the number available and acts accordingly
 *
 * <p>
 *     KSemaphores are often used to restrict the number of threads than can
 *     access some (phtsical or logical) resource. For example, here is
 *     a class that uses a semaphore to control access to a pool of items:
 *
 *     <pre>
 *          class Pool {
 *              private static final int MAX_AVAILABLE = 100;
 *              private final Semaphore available = new Semaphore(MAX_AVAILABLE, true);
 *
 *              public Object getItem() throws InterruptedException {
 *                  available.acquire();
 *                  return getNextAvailableItem();
 *              }
 *
 *              public void putItem(Object x){
 *                  if(markAsUnused(x)){
 *                      available.release();
 *                  }
 *              }
 *
 *              // Not a particularly efficient data structure: just for demo
 *
 *              protected Object[] items = ..... // whatever kinds items being managed
 *              protected boolean[] used = new boolean[MAX_AVAILABLE];
 *
 *              protected synchronized Object getNextAvailableItem(){
 *                  for(int i = 0; i < MAX_AVAILABLE; ++i){
 *                      if(!used[i]){
 *                          used[i] = true;
 *                          return items[i];
 *                      }
 *                  }
 *                  return null; // not reached
 *              }
 *
 *              protected synchronized boolean markAsUnused(Object item){
 *                  for(int i = 0; i < MAX_AVAILABLE; ++i){
 *                      if(item == item[i]){
 *                          if(used[i]){
 *                              used[i] = false;
 *                              return true;
 *                          }else{
 *                              return false;
 *                          }
 *                      }
 *                  }
 *
 *                  return false;
 *              }
 *          }
 *     </pre>
 *
 * </p>
 *
 * <p>
 *     Before obtaining an item each thread must acquire a permit from
 *     the semaphore guaranteening that an item is available for use. When
 *     the thread has finished with the item it is returned back to the
 *     pool and a permit is returned to the semaphore, allowing another
 *     thread to acquire that item. Note that no synchronization lock is
 *     held when {@link #acquire()} is called as that would prevent an item
 *     from being returned to the pool. The semaphore encapsulates the
 *     synchronization needed to restrict access to the pool, separately
 *     from any synchronization needed to maintain the consistency of the
 *     pool itself
 * </p>
 *
 * <p>
 *     A semaphore initialized to one, and which is used such that it
 *     only has at most one permit available, can serve as mutual
 *     exclusion lock. This is more commonly known as a <em>binary
 *     semaphore</em>, because it only has two states one permit
 *     available, or zero permits available. When used in this way, the
 *     binary semaphore has the property (unlike many {@link java.util.concurrent.locks.Lock} implementation)
 *     that the lock can be released by a thread other than the lock can be released by a
 *     thread other than the owber (as semaphores have no notion of
 *     ownership). This can be useful in some specified contexts, such
 *     as deadlock recovery
 * </p>
 *
 * <p>
 *     The constructor for this class optionally accepts a
 *     <em>fairness</em> parameter. When set false, this class makes no
 *     guarantees about the order in which threads acquire permits. In
 *     particular, <em>barging</em> is permitted, that is, a thread
 *     invoking {@link #acquire} can be allocated a permit ahead of a
 *     thread that has been waiting - logically the new thread places itself at
 *     the head of the queue of waiting threads. When fairness is set true, the
 *     semaphore guarantees that threads invoking any of the {@link
 *     #acquire()} methods are selected to obtain permits in the order in
 *     which their invocation of those methods was processed
 *     (first-in-first-out) Note that FIFO ordering necessarily
 *     applies to specific internal points of execution within these
 *     methods. So. it is possible for one thread to invoke
 *     {@code acquire} before another, but reach the ordering point after
 *     the other, and similarly upon return from the mentod
 *     Also note that the untimed {@link #tryAcquire(int)} methods do not
 *     honor the fairness setting, but will take any permits that are
 *     available
 * </p>
 *
 * <p>
 *     Generally, semaphores used to control resource access should be
 *     initialized as fair, to ensure that no thread is starved out from
 *     accessing a resource, When using semaphore for other kinds of
 *     synchronization control, the throughput advantages of non-fair
 *     ordering often outweigh fairness considerations
 * </p>
 *
 * <p>
 *     This class also provides convenience methods to {@link
 *     #acquire()} and {@link #release()} multiple
 *     permits at a time, Beware of the increased risk of indefinite
 *     postponement when these methods are used without fairness set true
 * </p>
 *
 * <p>
 *     Memory consistency affects: Actions in a thread prior to calling
 *     a "release" method such as {@code release()}
 *     happen-before
 *     action following a successful "acquire" method such as "{@code acquire}"
 *     in another thread;
 * </p>
 *
 * Created by xjk on 1/29/17.
 */
public class KSemaphore  implements Serializable{

    private static final long serialVersionUID = -3222578661600680210L;
    /** All mechanics via KAbstractQueuedSynchronizer subclass */
    private final Sync sync;

    /**
     * Synchronization implementation for semaphore. Uses AQS state
     * to represent permits. Subclassed into fair and nonfair
     * versions
     */
    abstract static class Sync extends KAbstractQueuedSynchronizer{
        private static final long serialVersionUID = 1192457210091910933L;

        Sync(int permits){
            setState(permits);
        }

        final int getPermits(){
            return getState();
        }

        final int nonfairTryAcquireShared(int acquires){
            for(;;){
                int available = getState();
                int remaining = available - acquires;
                if(remaining < 0 ||
                        compareAndSetState(available, remaining)){
                    return remaining;
                }
            }
        }

        protected final boolean tryReleaseShared(int releases){
            for(;;){
                int current = getState();
                int next = current + releases;
                if(next < current){ // overflow
                    throw new Error(" Maximum permit count exceeded");
                }
                if(compareAndSetState(current, next)){
                    return true;
                }
            }
        }

        final void reducePermits(int reductions){
            for(;;){
                int current = getState();
                int next = current - reductions;
                if(next > current){ // underflow
                    throw new Error(" Permit count underflow ");
                }
                if(compareAndSetState(current, next)){
                    return;
                }
            }
        }


        final int drainPermits(){
            for(;;){
                int current = getState();
                if(current == 0 || compareAndSetState(current, 0)){
                    return current;
                }
            }
        }

    }

    /**
     * Nonfair version
     */
    static final class NonfairSync extends Sync{

        private static final long serialVersionUID = -2694183684443567898L;

        NonfairSync(int permits) {
            super(permits);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            return nonfairTryAcquireShared(acquires);
        }
    }

    /**
     * Fair version
     */
    static final class FairSync extends Sync{

        private static final long serialVersionUID = 3245289457313211085L;

        FairSync(int permits) {
            super(permits);
        }

        @Override
        protected int tryAcquireShared(int acquires) {
            for(;;){
                if(hasQueuedPredecessors()){
                    return -1;
                }
                int available = getState();
                int remaining = available - acquires;
                if(remaining < 0 ||
                        compareAndSetState(available, remaining)){
                    return remaining;
                }
            }
        }
    }

    /**
     * Creates a {@code KSemaphore} with the given number of
     * permits and nonfair fairness setting
     *
     * @param permits the initial number of permits available
     *                This value may be negative, in which case releases
     *                must occur before any acquires will be granted
     */
    public KSemaphore(int permits){
        sync = new NonfairSync(permits);
    }

    /**
     * Creates a {@code KSemaphore} with the given number of
     * permits and the given fairness setting.
     *
     * @param permits the initial number of permits available
     *                This value may be negative, in which case releases
     *                must occur before any acquires will be granted
     * @param fair {@code true} if this ksemaphore will granted.
     *                         first-in-first-out granting of permits under contention
     *                         else {@code false}
     */
    public KSemaphore(int permits, boolean fair){
        sync = fair ? new FairSync(permits) : new NonfairSync(permits);
    }

    /**
     * Acquires a permit from this ksemaphore, blocking until one is
     * available, ot the thread is {@link Thread#interrupt() interrupted}
     *
     * <p>
     *     Acquires a permit, if one is available and returns immediately.
     *     reducing the number of available permits by one
     * </p>
     *
     * <li>
     *     If no permit is available then the current thread becomes
     *     disabled for thread scheduling purposes and lies dormant until
     *     one of two thing happens:
     * </li>
     *
     * <li>
     *     Some other thread invokes the {@link #"release} method for this
     *     ksemaphore and the current thread is next to be assigned a permit; or
     * </li>
     *
     * <li>
     *     Some other thread {@link Thread#interrupt() interrupts}
     *     the current thread
     * </li>
     *
     * <p>
     *     If the current thread:
     *     has its interrupted status set on entry to this method; or
     *     is {@link Thread#interrupt() interrupted} while waiting
     *     for a permit,
     *     then {@link InterruptedException} is thrown anf the current thread's
     *     interrupted status is cleared
     * </p>
     *
     * @throws InterruptedException if the current thread is interrupted
     */
    public void acquire() throws InterruptedException{
        sync.acquireSharedInterruptibly(1);
    }


    /**
     * Acquires a permit from this ksemaphore,blocking until one is
     * available.
     *
     * <p>
     *     Acquires a permit, if one is available and returns immediately,
     *     reducing the number of available permits by one.
     * </p>
     *
     * <p>
     *     If no permit is available then the current thread becomes
     *     disabled for thread scheduling purposes and lies dormant until
     *     some other thread invokes the {@link #"release} method for this
     *     ksemaphore and the current thread is next to be assigned a permit
     * </p>
     *
     *
     * <p>
     *      If the current thread is {@link Thread#interrupt() interrupted}
     *      while waiting for a permit then it will continue to wait, but the
     *      time at which the thread is assigned a permit may change compared to
     *      the time it would have received the permit had no interruption
     *      occured. When the thread does return from this method its interrupt
     *      status will be set.
     * </p>
     */
    public void acquireUninterruptibly(){
        sync.acquireShared(1);
    }

    /**
     * Acquires a permit from this ksemaphore, only if one is available at the
     * time of invocation
     *
     * <p>
     *     Acquires a permit, if one is available and returns immediately
     *     with the value {@code true},
     *     reducing the number of available permits by one.
     * </p>
     *
     * <p>
     *     If no permit is available then this method will return
     *     immediately with the value {@code false}
     * </p>
     *
     * <p>
     *     Even when this ksemaphore has been set to use a
     *     fair ordering policy, a call to {@code tryAcquire()} <em>will</em>
     *     immediately acquire a permit if one is available, whether or not
     *     other threads are currently waiting.
     *     This barging behavior can be useful in certain
     *     circumstances, even though it breaks fairness, If you wnt to honor
     *     the fairness setting then use
     *     {@code #tryAcquire}
     *     which is almost equivalent (it also detects interruption)
     * </p>
     *
     * @return {@code true} if a permit was acquired and {@code false} otherwise
     */
    public boolean tryAcquire(){
        return sync.nonfairTryAcquireShared(1) >= 0;
    }


    /**
     * Acquires a permit from this ksemaphore, if one becomes available
     * within the given waiting time and the current thread has not
     * been {@link Thread#interrupt() interrupted}.
     *
     * <p>
     *     Acquires a permit, if one is available and returns immediately,
     *     with the value {@code true},
     *     reducing the number of available permits by one.
     * </p>
     *
     * <p>
     *     If no permit is available then the current thread becomes
     *     disabled for thread scheduling purposes and lies dormant until
     *     one of three things happen:
     *     <li>
     *         Some other thread invokes the {@link #"release} method for this
     *         ksemaphore and the current thread is next to be assigned a permit; or
     *         Some other thread {@link Thread#interrupt() interrupts}
     *         the current thread; or
     *         The specified waiting time elapses
     *     </li>
     * </p>
     *
     * <p>
     *     If a permit is acquired then the value {@code true} is returned.
     * </p>
     *
     * <p>
     *     If the current thread:
     *     has its interrupted status set on entry to this method; or
     *     is {@link Thread#interrupt() interrupted} while waiting
     *     to acquire a permit
     *     then {@link InterruptedException} is thrown and the current thread's
     *     interrupted status is cleared
     * </p>
     *
     * <p>
     *     If the specified waiting time elapses then the value {@code false}
     *     is returned. If the time is less than or equal to zero, the method
     *     will not wait at all.
     * </p>
     *
     * @param timeout the maximum time to wait for a permit
     * @param unit the time unit of the {@code timeout} argument
     * @return {@code true} if a permit was acquired and {@code false}
     *              if the waiting time elapsed before a permit was acquired
     * @throws InterruptedException if the current thread is interrupted
     */
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException{
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }


    /**
     * Releases a permit, returning it to the ksemaphore
     *
     * <p>
     *     Releases a permit, increasing the number of available permits by
     *     one. If any threads are trying to acquire a permit, then one is
     *     selected and given the permit that was just released. That thread
     *     is (re)enabled for threadscheduling purposes.
     * </p>
     *
     * <p>
     *     There is no requirement that a thread that releases a permit must
     *     have acquired that permit by calling {@link #acquire()}
     *     Correct usage of a ksemaphore is established by programming convention
     *     in the application
     * </p>
     */
    public void release(){
        sync.releaseShared(1);
    }

    /**
     * Acquires the given number of permits from this ksemaphore
     * blocking until all are available
     * or the thread is {@link Thread#interrupt() interrupt}
     *
     *
     * <p>
     *     Acquires the given number of permits, if they are available,
     *     and returns immediately, reducing the number of available permits
     *     by the given amount.
     * </p>
     *
     * <p>
     *     If insufficient permits are available then the current thread becomes
     *     disabled for thread scheduling purposes and lies dormant until
     *     one of two things happens:
     * </p>
     *
     * <li>
     *     Some other thread invokes one of the {@link #release() release}
     *     methods for this ksemaphore, the current thread is next to be assigned
     *     permits and the number of available permits satisfies this request; or
     *     Some other thread {@code Thread#interrupt}
     *     the current thread
     * </li>
     *
     * If the current thread:
     * has its interrupted status set on entry to this method; or
     * is {@link Thread#interrupt() interrupted} while waiting
     * for a permit,
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * Any permits that were to be assigned to this thread are instead
     * assigned to other threads trying to acquire permits, as if
     * permits had been make available by a call to {@link #release()}
     *
     * @param permits the number of permits to acquire
     * @throws InterruptedException if the current thread is interrupted
     */
    public void acquire(int permits) throws InterruptedException{
        if(permits < 0){
            throw new IllegalArgumentException();
        }
        sync.acquireSharedInterruptibly(permits);
    }


    /**
     * Acquires the given number of permits from this ksemaphore,
     * blocking until all are available
     *
     * <p>
     *     Acquires the given number of permit, if they are available,
     *     and returns immediately, reducing the number of available permits
     *     by the given amount.
     * </p>
     *
     * <p>
     *     If insufficient permits are available then the current thread becomes
     *     disabled for thread scheduling purposes and lies dormant until
     *     some other thread invokes one of the {@link #release() release}
     *     methods for this ksemaphore, the current thread is next to be assigned
     *     permits and the number of available permits satisfies this request
     * </p>
     *
     * <p>
     *     If the current thread is {@link Thread#interrupt() interrupted}
     *     while waiting for permits then it will contine to wait and its
     *     position in the queue is not affected. When the thread does return
     *     from this method its interrupt status will be set
     * </p>
     *
     * @param permits
     */
    public void acquireUninterruptibly(int permits){
        if(permits < 0) throw new IllegalArgumentException();
        sync.acquireShared(permits);
    }


    /**
     * Acquires the given number of permits from this ksemaphore, only
     * if all are available at the time of invocation.
     *
     * <p>
     *     Acquires the given number of permits, if they are available, and
     *     returns immediately, with the value {@code true}
     *     reducing the number of available permits by the given amount
     * </p>
     *
     * <p>
     *     If insufficient permits are available then this method will return
     *     immediately with value {@code false} and the number of available
     *     permits is unchanged
     * </p>
     *
     * <p>
     *     Even when this semaphore has been set to use a fair ordering
     *     polic, a call to {@code tryAcquire} <em>will</em>
     *     immediately acquire a permit if one is available, whether or
     *     not other threads are currently waiting. This
     *     barging behavior can be useful in certain
     *     circumstances, even though it breaks fairness. If you want to
     *     honor the fairness setting, then use {@code tryAcquire}
     *     which is almost equivalent (it also detects interruption)
     * </p>
     *
     * @param permits the number of permit to acquire
     * @return {@code true} if the permits were acquired and
     *          {@code false} otherwise
     */
    public boolean tryAcquire(int permits){
        if(permits < 0) throw new IllegalArgumentException();
        return sync.nonfairTryAcquireShared(permits) >= 0;
    }


    /**
     * Acquires the given number of permits from this ksemaphore, if all
     * become available within the given waiting time and the current
     * thread has not been {@link Thread#interrupt() interrupted}.
     *
     * <p>
     *     Acquires the given number of permits, if they are available and
     *     returns immediately, with the value {@code true},
     *     reducing the number of available permits by the given amount.
     * </p>
     *
     * <p>
     *     If insufficient permits are available then
     *     the current thread becomes disabled for thread scheduling
     *     purposes and lies dormant until one of three things happens:
     * </p>
     *
     * <li>
     *     Some other thread invokes one of the {@link #release}
     *     methods for this ksemaphore, the current thread is next to be assigned
     *     permits and the number of available permits satisfies this request; or
     *     Some other thread {@link Thread#interrupt()}
     *     the current thread; or
     *     The specified waiting time elapse
     * </li>
     *
     * <p>
     *     If the permits are acquired then value {@code true} is returned
     * </p>
     *
     * <p>
     *     If the current thread:
     *     has its interrupted status set on entry to this method; or
     *     is {@link Thread#interrupt() interrupted} while waiting
     *     to acquire the permits
     *     then {@link Thread#interrupt() interrupted} while waiting
     *     to acquire the permits
     * </p>
     *
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     * Any permits that were to be assigned to this thread, are instead
     * assigned to other threads trying to acquire permits, as if
     * the permits had been make available by a call to {@link #release()}
     *
     * <p>
     *     If the specified waiting time elapses then the value {@code false}
     *     is returned, If time time is less than or equal to zero, the method
     *     will not wait at all. Any permit that were to be assigned to this
     *     thread, are instead assigned to other threads trying to acquire
     *     permits, as if the permits had been made available by a call to
     *     {@link #release()}
     * </p>
     *
     * @param permits the number of permit to acquire
     * @param timout the maximum time to wait for the permits
     * @param unit the time unit of the {@code timeout} argument
     * @return {@code true} if all permits were acquired and {@code false}
     *              if the waiting time elapses before all permits were acquired
     * @throws InterruptedException if the current tread is interrupted
     */
    public boolean tryAcquire(int permits, long timout, TimeUnit unit) throws InterruptedException{
        if(permits < 0) throw new IllegalArgumentException();
        return sync.tryAcquireSharedNanos(permits, unit.toNanos(timout));
    }


    /**
     * Releases the given number of permits, returning them to the ksemaphore.
     *
     * <p>
     *     Releases the given number of permits, increasing the number of
     *     available permits by that amount.
     *     If any threads are trying to acquire permits, then one
     *     is selected and given the permits that were just rleased.
     *     If the number of available permits satisfies that thread's request
     *     then that thread is (reenabled) for thread scheduling purposes;
     *     Otherwise the thread will wait until sufficient permits are available
     *     If there are still permits available
     *     after this thread's request has been satisfied, then those permits
     *     are assigned in turn to other thread's trying to acquire permits
     * </p>
     *
     * <p>
     *     There is no requirement that a thread that releases a permit must
     *     have acquired that permit by calling {@link KSemaphore#acquire()}.
     *     Correct usage of a ksemaphore is established by programming convention
     *     in the application
     * </p>
     *
     * @param permits the number of permits to release
     * @throws IllegalArgumentException if {@code permits} is negative
     */
    public void release(int permits){
        if(permits < 0) throw new IllegalArgumentException();
        sync.releaseShared(permits);
    }


    /**
     * Returns the current number of permits available in this ksemaphore.
     * <p>
     *     This method is typically used for debugging and testing purposes
     * </p>
     *
     * @return the number of permits available in this ksemaphore
     */
    public int availablePermits(){
        return sync.getPermits();
    }

    /**
     * Acquires and returns all permits that are immediately available
     *
     * @return the number of permits acquired
     */
    public int drainPermits(){
        return sync.drainPermits();
    }

    /**
     * Shrinks the number of available permits by the indicated
     * reduction. This method can be useful in subclasses that use
     * ksemaphore to track resources that become unavailable. This
     * method differs from {@code acquire} in that it does not block
     * waiting for permits to become available
     *
     * @param reduction
     */
    protected void reducePermits(int reduction){
       if(reduction < 0) throw new IllegalArgumentException();
        sync.reducePermits(reduction);
    }

    /**
     * Returns {@code true} if this ksemaphore has fairness set true
     * @return {@code true} if this ksemaphore has fairness set true
     */
    public boolean isFair(){
        return sync instanceof FairSync;
    }


    /**
     * Queries whether any threads are waiting to acquire. Note that
     * because cancellations may occur at any time, a {@code true}
     * return does not guarantee that any other thread will ever
     * acquire. This method is designed primarily for use in
     * monitoring of the system state.
     *
     * @return {@code true} if there may be other threads waiting to
     *          acquire the lock
     */
    public final boolean hasQueuedThreads(){
        return sync.hasQueuedThreads();
    }


    /**
     * Returns an estimate of the number of threads waiting to acquire.
     * The value is only an estimate because the number of threads may
     * change dynamically while this method traverses internal data
     * structures. This method is designed for use in monitoring of the
     * system state, not for synchronization control.
     *
     * @return the estimated number of threads waiting for this lock
     */
    public final int getQueueLength(){
        return sync.getQueueLength();
    }


    /**
     * Returns a collection containing threads that may be waiting to acquire
     * Because the actual set of threads may change dynamically while
     * constructing this result, the returned collection is only a best-effort
     * estimate. The elements of the returned collection are in no particular
     * order. This method is designed to facilitate construction of
     * subclasses that provide more extensive monitoring facilities
     *
     * @return the collection of threads
     */
    protected Collection<Thread> getQueueThreads(){
        return sync.getQueuedThreads();
    }

    /**
     * Returns a string identifying this ksemaphore, as well as its state.
     *  The state, in brackets, includes the String {@code " Permits = "}
     *  followed by the number of permits
     *
     * @return s tring identifying this ksemaphore, as well as its state
     */
    public String toString(){
        return super.toString() + " [ Permits = " + sync.getPermits() + " ]";
    }

}
