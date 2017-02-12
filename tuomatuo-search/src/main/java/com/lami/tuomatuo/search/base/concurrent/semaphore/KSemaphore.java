package com.lami.tuomatuo.search.base.concurrent.semaphore;

import com.lami.tuomatuo.search.base.concurrent.aqs.KAbstractQueuedSynchronizer;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * http://www.cnblogs.com/go2sea/p/5625536.html
 *
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
    /** AQS 的子类主要定义获取释放 lock */
    abstract static class Sync extends KAbstractQueuedSynchronizer{
        private static final long serialVersionUID = 1192457210091910933L;

        /**
         * 指定 permit 初始化 Semaphore
         */
        Sync(int permits){
            setState(permits);
        }

        /**
         * 返回剩余 permit
         */
        final int getPermits(){
            return getState();
        }

        /**
         * 获取 permit
         */
        final int nonfairTryAcquireShared(int acquires){
            for(;;){
                int available = getState();
                int remaining = available - acquires; // 判断获取 acquires 的剩余 permit 数目
                if(remaining < 0 ||
                        compareAndSetState(available, remaining)){ // cas改变 state
                    return remaining;
                }
            }
        }

        /**
         * 释放 lock
         */
        protected final boolean tryReleaseShared(int releases){
            for(;;){
                int current = getState();
                int next = current + releases;
                if(next < current){ // overflow
                    throw new Error(" Maximum permit count exceeded");
                }
                if(compareAndSetState(current, next)){  // cas改变 state
                    return true;
                }
            }
        }

        final void reducePermits(int reductions){ // 减少 permits
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

        /** 将 permit 置为 0 */
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
    /** 非公平版本获取 permit */
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
    /** 公平版本获取 permit */
    static final class FairSync extends Sync{

        private static final long serialVersionUID = 3245289457313211085L;

        FairSync(int permits) {
            super(permits);
        }

        /**
         * 公平版本获取 permit 主要看是否由前继节点
         */
        @Override
        protected int tryAcquireShared(int acquires) {
            for(;;){
                if(hasQueuedPredecessors()){ // 1. 判断是否Sync Queue 里面由前几节点
                    return -1;
                }
                int available = getState();
                int remaining = available - acquires;
                if(remaining < 0 ||
                        compareAndSetState(available, remaining)){ // 2. cas 改变state
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
    /**
     * 使用非公平版本构件 Semaphore
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
    /**
     * 指定版本构件 Semaphore
     */
    public KSemaphore(int permits, boolean fair){
        sync = fair ? new FairSync(permits) : new NonfairSync(permits);
    }


    /**
     * 调用 acquireSharedInterruptibly 响应中断的方式获取 permit
     */
    public void acquire() throws InterruptedException{
        sync.acquireSharedInterruptibly(1);
    }


    /**
     * 调用 acquireUninterruptibly 非响应中断的方式获取 permit
     */
    public void acquireUninterruptibly(){
        sync.acquireShared(1);
    }


    /**
     * 尝试获取 permit
     */
    public boolean tryAcquire(){
        return sync.nonfairTryAcquireShared(1) >= 0;
    }


    /**
     * 尝试的获取 permit, 支持超时与中断
     */
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException{
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    /**
     * 释放 permit
     */
    public void release(){
        sync.releaseShared(1);
    }


    /**
     * 支持中断的获取permit
     */
    public void acquire(int permits) throws InterruptedException{
        if(permits < 0){
            throw new IllegalArgumentException();
        }
        sync.acquireSharedInterruptibly(permits);
    }


    /**
     * 不响应中断的获取 permit
     */
    public void acquireUninterruptibly(int permits){
        if(permits < 0) throw new IllegalArgumentException();
        sync.acquireShared(permits);
    }


    /**
     * 尝试获取 permit
     */
    public boolean tryAcquire(int permits){
        if(permits < 0) throw new IllegalArgumentException();
        return sync.nonfairTryAcquireShared(permits) >= 0;
    }


    /**
     * 尝试 支持超时机制, 支持中断 的获取 permit
     */
    public boolean tryAcquire(int permits, long timout, TimeUnit unit) throws InterruptedException{
        if(permits < 0) throw new IllegalArgumentException();
        return sync.tryAcquireSharedNanos(permits, unit.toNanos(timout));
    }

    /**
     * 释放 permit
     */
    public void release(int permits){
        if(permits < 0) throw new IllegalArgumentException();
        sync.releaseShared(permits);
    }

    /**
     * 返回可用的 permit
     */
    public int availablePermits(){
        return sync.getPermits();
    }


    /**
     * 消耗光 permit
     */
    public int drainPermits(){
        return sync.drainPermits();
    }


    /**
     * 减少 reduction 个permit
     */
    protected void reducePermits(int reduction){
       if(reduction < 0) throw new IllegalArgumentException();
        sync.reducePermits(reduction);
    }


    /**
     * 判断是否是公平版本
     */
    public boolean isFair(){
        return sync instanceof FairSync;
    }


    /**
     * 返回 AQS 中 Sync Queue 里面的等待线程
     */
    public final boolean hasQueuedThreads(){
        return sync.hasQueuedThreads();
    }

    /**
     * 返回 AQS 中 Sync Queue 里面的等待线程长度
     */
    public final int getQueueLength(){
        return sync.getQueueLength();
    }


    /**
     * 返回 AQS 中 Sync Queue 里面的等待线程
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
