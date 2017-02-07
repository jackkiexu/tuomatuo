package com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock;

import com.lami.tuomatuo.search.base.concurrent.aqs.KAbstractQueuedSynchronizer;
import com.lami.tuomatuo.search.base.concurrent.spinlock.MyAbstractQueuedSynchronizer;
import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * http://blog.csdn.net/yuhongye111/article/details/39055531
 * http://brokendreams.iteye.com/blog/2250866
 *
 *
 *
 * An implementation of {@link java.util.concurrent.locks.ReadWriteLock} supporting similar
 * semantics to {@link java.util.concurrent.locks.ReentrantLock}
 *
 * <li>Acquisition order</li>
 *
 * <p>
 *     This class does not impose a reader or writer preference
 *     ordering for lock access. However, it does support an optional
 *     <em>fairness</em> policy
 * </p>
 *
 * <dt>
 *     <b>Non-fair mode (default)</b>
 *     When constructed as non-fair (the default), the order of entry
 *     to the read and write lock is unspecified, subject to reentrancy
 *     constraints. A nonfair lock that is continuously contended may
 *     indefinitely postpone one or more reader or writer threads, but
 *     will normally have higher throughput than a fair lock
 * </dt>
 *
 * <dt>Fair mode</dt>
 * <dd>
 *     When constructed as fair, threads contend for entry using an
 *     approximately arrival-order policy. When the currently held lock
 *     is released, either the longest-waiting single writer thread will
 *     be assigned the write lock, or if there is a group of reader hreads
 *     waiting longer than all waiting writer threads, that group will be
 *     assigned the read lock.
 * </dd>
 *
 * <p>
 *     A thread that tries to acquire a fair read lock (non-reentrantly)
 *     will block if either the write lock is held, or there is a waiting
 *     writer thread. The thread will not acquire the read lock until
 *     after the oldest currently waiting writer thread has acquired and
 *     released the write lock. Of course, if a waiting writer abandons
 *     its wait, leaving one or more reader threads as the longest waiters
 *     in the queue with write lock free , the those readers will be
 *     assigned the read lock.
 * </p>
 *
 * <p>
 *     A thread that tries to acquire a fair write lock (non-reentrantly)
 *     will block unless both the read lock and write lock are free (which
 *     implies there are no waiting threads). (Note that non-blocking
 *     {@link "ReadLock#tryLock()}) and {@link "WriteLock#tryLock()} methods
 *     do not honor this fair setting and will immediately acquire the lock
 *     if it possible, regardless of waiting threads
 * </p>
 *
 * <p>
 *     This lock allows both readers and writers to reacquire read or
 *     write locks in the style of a {@link java.util.concurrent.locks.ReentrantLock}. Non-reentrant
 *     readers are not allowed until all write locks held by the writing
 *     thread have been released.
 * </p>
 *
 * <p>
 *     Additionally, a writer can acquire the read lock, but not
 *     vice-versa. Among other applications, reentrancy can be useful
 *     when write locks are held during calls or callbacks to method that
 *     perform reads under read locks. If a read tries to acquire the
 *     write lock it will never succeed.
 * </p>
 *
 * <p>
 *     Lock downgrading
 *     Reentrancy also allows downgrading from the write lock to a read lock,
 *     by acquiring the write lock, then the read lock and then releasing the
 *     write lock. However, upgrading from a read lock to the write lock is
 *     <b>not</b> possible
 * </p>
 *
 * <p>
 *     Interruption of lock acquisition
 *     The read lock and write lock both support interruption during lock
 *     acquisition
 * </p>
 *
 * <p>
 *     {@link java.util.concurrent.locks.Condition} support
 *     The write lock provides a {@link "Condition} implementation that
 *     behaves in the same way, with respect to the write lock, as the
 *     {@link "Condition} implementation provided by
 *     {@link "ReentrantLock#newCondition} does for {@link java.util.concurrent.locks.ReentrantLock}
 *     This {@link "Condition} can, of course, only be used with the write lock
 * </p>
 *
 * <p>
 *     The read lock does not support a {@link "Condition} and
 *     {@code readLocks().newCondition} throws
 *     {@code UnsupportedOperationException}
 * </p>
 *
 * <p>
 *     Instrumentation
 *     This class supports methods to determine whether locks
 *     are held or contended. These methods are designed for monitoring
 *     system state, not for synchronization control
 * </p>
 *
 * <p>
 *     Serialization of this class behaves in the same way as built-in
 *     locks: a deserialized lock is in the unlocked state, regardless of
 *     its state when serialized
 * </p>
 *
 * <pre>
 *     {@code
 *          class CacheData {
 *              Object data;
 *              volatile boolean cacheValid;
 *              final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
 *
 *              void processCacheDate(){
 *                  rwl.readLock().lock();
 *                  if(!cacheValid){
 *                      // Must release read lock before acquiring write lock
 *                      rwl.readLock().unlock();
 *                      rwl.writeLock().lock();
 *                      try{
 *                          // Recheck state because another thread might have
 *                          // acquired write lock and changed state before we did
 *                          if(!cacheValid){
 *                              data = ...
 *                              cacheValid = true;
 *                          }
 *                          // Downgrade by acquiring read lock before releasing write lock
 *                          rwl.readLock().lock();
 *                      }finally{
 *                          rwl.writeLock().unlock(); // Unlock write, still hold read
 *                      }
 *                  }
 *
 *                  try{
 *                      use(data);
 *                  }finally{
 *                      rwl.readLock().unlock();
 *                  }
 *              }
 *
 *          }
 *     }
 * </pre>
 *
 * ReentrantReadWriteLocks can be used to improve concurrency in some
 * uses of some kinds of Collections. This is typically worthwhile
 * only when the collections are expected to be large, accessed by
 * more reader threads than writer threads, and entail operations with
 * overhead that outweighs synchronization overhead. For example, here
 * is a class using a TreeMap that is expected to be large and
 * concurrently accessed
 *
 * <pre>
 *     {@code
 *          class RWDictionary{
 *              private final Map<String, Data> m = new TreeMap<String, Data>();
 *              private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
 *              private final Lock r = rwl.readLock();
 *              private final Lock w = rwl.writeLock();
 *
 *              public Data get(String key){
 *                  r.lock();
 *                  try{
 *                      return m.keySet().toArray();
 *                  }finally{
 *                      r.unlock();
 *                  }
 *              }
 *
 *              public String[] allKeys(){
 *                  r.lock();
 *                  try{ return m.keySet().toArray();
 *                  }finally{
 *                      r.unlock();
 *                  }
 *              }
 *
 *              public Data put(String key, Data value){
 *                  w.lock();
 *                  try{ return m.put(key, value);
 *                  }finally{
 *                      w.unlock();
 *                  }
 *              }
 *
 *              public void clear(){
 *                  w.lock();
 *                  try{ m.clear();
 *                  }finally{
 *                  w.unlock();
 *                  }
 *              }
 *
 *          }
 *     }
 * </pre>
 *
 * <p>
 *     This lock supports a maximum of 65535 recursive write locks
 *     and 65535 read locks. Attempts to exceed these limits result in
 *     {@link Error} throws from locking methods
 * </p>
 *
 * Created by xujiankang on 2017/1/25.
 */
public class KReentrantReadWriteLock implements ReadWriteLock, Serializable{
    private static final long serialVersionUID = -6992448646407690164L;

    /** Inner class providing readlock */
    /** 内部类 readLock */
    private final KReentrantReadWriteLock.ReadLock readerLock;
    /** Inner class providing writelock */
    /** 内部类 writeLock */
    private final KReentrantReadWriteLock.WriteLock writerLock;
    /** Performs all synchronization mechanics */
    /** sync 继承 aqs 实现基本的 tryAcquire tryRelease */
    final Sync sync;

    /**
     * Creates a new {@code KReentrantReadWriteLock} with
     * default (nonfair) ordering properties
     * 用 nonfair 来构建 read/WriteLock (这里的 nonfair 指的是当进行获取 lock 时 若 aqs的syn queue 里面是否有 Node 节点而决定所采取的的策略)
     */
    public KReentrantReadWriteLock(){
        this(false);
    }

    /**
     *  构建 ReentrantReadLock
     */
    public KReentrantReadWriteLock(boolean fair){
        sync = fair ? new FairSync() : new NonfairSync();
        readerLock = new ReadLock(this);
        writerLock = new WriteLock(this);
    }

    @Override
    public Lock readLock() {
        return readerLock;
    }

    @Override
    public Lock writeLock() {
        return writerLock;
    }

    /**
     * Synchronization implementation for ReentrantReadWriteLock
     * Subclassed into fair and nonfair versions
     */
    abstract static class Sync extends MyAbstractQueuedSynchronizer {
        private static final long serialVersionUID = 6317671515068378041L;

        /**
         * Read vs write count extraction constants and functions.
         * Lock state is logically divided into two unsigned shorts:
         * The lower one representing the exclusive(writer) lock hold count,
         * and the upper the shared (reader) hold count.
         */

        /**
         * ReentrantReadWriteLock 这里使用 AQS里面的 state的高低16位来记录 read /write 获取的次数(PS: writeLock 是排他的 exclusive, readLock 是共享的 sahred, )
         * 记录的操作都是通过 CAS 操作(有竞争发生)
         *
         *  特点:
         *      1) 同一个线程可以拥有 writeLock 与 readLock (但必须先获取 writeLock 再获取 readLock, 反过来进行获取会导致死锁)
         *      2) writeLock 与 readLock 是互斥的(就像 Mysql 的 X S 锁)
         *      3) 在因 先获取 readLock 然后再进行获取 writeLock 而导致 死锁时, 本线程一直卡住在对应获取 writeLock 的代码上(因为 readLock 与 writeLock 是互斥的, 在获取 writeLock 时监测到现在有线程获取 readLock , 锁一会一直在 aqs 的 sync queue 里面进行等待), 而此时
         *          其他的线程想获取 writeLock 也会一直 block, 而若获取 readLock 若这个线程以前获取过 readLock, 则还能继续 重入 (reentrant), 而没有获取 readLock 的线程因为 aqs syn queue 里面有获取 writeLock 的 Node 节点存在会存放在 aqs syn queue 队列里面 一直 block
         */

        /** 对 32 位的 int 进行分割 (对半 16) */
        static final int SHARED_SHIFT   = 16;
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT); // 000000000 00000001 00000000 00000000
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1; // 000000000 00000000 11111111 11111111
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1; // 000000000 00000000 11111111 11111111

        /** Returns the number of shared holds represented in count */
        /** 计算 readLock 的获取次数(包含 reentrant 的次数) */
        static int sharedCount(int c)       { return c >>> SHARED_SHIFT; } // 将字节向右移动 16位, 只剩下 原来的 高 16 位
        /** Returns the number of exclusive holds represented in count */
        /** 计算 writeLock 的获取的次数(包括 reentrant的次数) */
        static int exclusiveCount(int c)    { return c & EXCLUSIVE_MASK; } // 与 EXCLUSIVE_MASK 与一下

        /**
         * A counter for per-thread read hold counts
         * Maintained as a ThreadLocal; cached in cachedHoldCounter
         */
        /**
         * 几乎每个获取 readLock 的线程都会含有一个 HoldCounter 用来记录 线程 id 与 获取 readLock 的次数 ( writeLock 的获取是由 state 的低16位 及 aqs中的exclusiveOwnerThread 来进行记录)
         * 这里有个注意点 第一次获取 readLock 的线程使用 firstReader, firstReaderHoldCount 来进行记录
         * (PS: 不对, 我们想一下为什么不 统一用 HoldCounter 来进行记录呢? 原因: 所用的 HoldCounter 都是放在 ThreadLocal 里面, 而很多有些场景中只有一个线程获取 readLock 与 writeLock , 这种情况还用 ThreadLocal 的话那就有点浪费(ThreadLocal.get() 比直接 通过 reference 来获取数据相对来说耗性能))
         */
        static final class HoldCounter {
            int count = 0; // 重复获取 readLock/writeLock 的次数
            // Use id, not reference, to avoid garbage retention
            final long tid = getThreadId(Thread.currentThread()); // 线程 id
        }

        /**
         * ThreadLocal subclass, Easiest to explicitly define for sake
         * of deserialization mechanics
         */
        /** 简单的自定义的 ThreadLocal 来用进行记录  readLock 获取的次数  */
        static final class ThreadLocalHoldCounter extends ThreadLocal<HoldCounter>{
            @Override
            protected HoldCounter initialValue() {
                return new HoldCounter();
            }
        }

        /**
         * The number of reentrant read locks held by current thread.
         * Initialized only in constructor and readObject
         * Removed whenever a thread's read hold count drops to 0
         */
        /**
         *  readLock 获取记录容器 ThreadLocal(ThreadLocal 的使用过程中当 HoldCounter.count == 0 时要进行 remove , 不然很有可能导致 内存的泄露)
         */
        private transient ThreadLocalHoldCounter readHolds;

        /**
         * The hold count of the last thread to successfully acquire
         * readLock. This saves ThreadLocal lookup in the common case
         * where the next thread to release is the last one to
         * acquire. This is non-volatile since it is just used
         * as heuristic, and would be great for threads to cache.
         *
         * <p>
         *     Can outlive the Thread for which it is caching the read
         *     hold count, but avoids garbage retention by not retaining a
         *     reference to the Thread
         * </p>
         *
         * <p>
         *     Accessed via a benign data race; relies on the memory
         *     model's final field and out-of-thin-air guarantees.
         * </p>
         */
        /**
         * 最后一次获取 readLock 的 HoldCounter 的缓存
         * (PS: 还是上面的问题 有了 readHolds 为什么还需要 cachedHoldCounter呢? 大非常大的场景中, 这次进行release readLock的线程就是上次 acquire 的线程, 这样直接通过cachedHoldCounter来进行获取, 节省了通过 readHolds 的 lookup 的过程)
         */
        private transient HoldCounter cachedHoldCounter;

        /**
         * firstReader is the first thread to have acquired the read lock.
         * firstReaderHoldCount is firstReader's hold count.
         *
         * <p>More precisely, firstReader is the unique thread that last
         * changed the shared count from 0 to 1, and has not released the
         * read lock since then; null if there is no such thread.
         * </p>
         *
         * <p>
         *     Cannot cause garbage retention unless the thread terminated
         *     without relinquishing its read locks, since tryReleaseShared
         *     sets it to null.
         * </p>
         *
         * <p>
         *     http://stackoverflow.com/questions/21243858/out-of-thin-air-safety
         *     https://shipilev.net/blog/2014/jmm-pragmatics/#_java_memory_model
         *
         *     Accessed via a benign data race; relies on the memory
         *     model's out-of-thin-air guarantees for references.
         * </p>
         *
         * <p>
         *     This allows tracking of read holds for uncontended read
         *     locks to be very cheap
         * </p>
         */
        /**
         * 下面两个是用来进行记录 第一次获取 readLock 的线程的信息
         * 准确的说是第一次获取 readLock 并且 没有 release 的线程, 一旦线程进行 release readLock, 则 firstReader会被置位 null
         */
        private transient Thread firstReader = null;
        private transient int    firstReaderHoldCount;

        /**
         * Syn 的初始化
         */
        Sync(){
            readHolds = new ThreadLocalHoldCounter();
            setState(getState()); // ensures visibility of readHolds
        }

        /**
         * Acquires and releases use the same code for fair and
         * nonfair locks, but differ in whether/how they allow barging
         * when queues are non-empty
         */

        /**
         * Returns true if the current thread, when trying to acquire
         * the read lock, and otherwise eligible to do so, should block
         * because of policy for overtaking other waiting threads.
         */

        /**
         * ReentrantReadWriteLock 的公不公平就是由 readerShouldBlock writerShouldBlock 来进行决定的
         * 公平: readerShouldBlock writerShouldBlock 都会检测 aqs sync queue 里面是否有Node节点来决定获取
         * 非公平:
         *      readerShouldBlock 根据 aqs sync queue 里面 head.next 的节点是否是获取 writeLock 来决定
         *      writerShouldBlock 直接返回 false
         */

        /**
         * 当线程进行获取 readLock 时的策略(这个策略依赖于 aqs 中 sync queue 里面的Node存在的情况来定),
         * @return
         */
        abstract boolean readerShouldBlock();

        /**
         * Returns true if the current thread, when trying to acquire
         * the write lock, and otherwise eligible to do so, should block
         * because of policy for overtaking other waiting threads.
         */
        /**
         * 当线程进行获取 readLock 时的策略(这个策略依赖于 aqs 中 sync queue 里面的Node存在的情况来定)
         * @return
         */
        abstract boolean writerShouldBlock();

        /**
         * Note that tryRelease and tryAcquire can be called by
         * Conditions. So it is possible that their arguments contain
         * both read and write holds that are all released during a
         * condition wait and re-established in tryAcquire
         */
        /**
         * 在进行 release 锁 时, 调用子类的方法 tryRelease(主要是增对 aqs 的 state 的一下赋值操作) (PS: 这个操作只有exclusive的lock才会调用到)
         * @param releases
         * @return
         */
        protected final boolean tryRelease(int releases){
            if(!isHeldExclusively()){                           // 1 监测当前的线程进行释放锁的线程是否是获取独占锁的线程
                throw new IllegalMonitorStateException();
            }
            int nextc = getState() - releases;                 // 2. 进行 state 的释放操作
            boolean free = exclusiveCount(nextc) == 0;        // 3. 判断 exclusive lock 是否释放完(因为这里支持 lock 的 reentrant)
            if(free){                                          // 4. 锁释放掉后 清除掉 独占锁 exclusiveOwnerThread 的标志
                setExclusiveOwnerThread(null);
            }
            setState(nextc);                                   // 5. 直接修改 state 的值 (PS: 这里没有竞争的出现, 因为调用 tryRelease方法的都是独占锁, 互斥, 所以没有 readLock 的获取, 相反 readLock 对 state 的修改就需要 CAS 操作)
            return free;
        }

        /**
         * AQS 中 排他获取锁 模板方法acquire里面的策略方法  tryAcquire 的实现
         * @param acquires
         * @return
         */
        protected final boolean tryAcquire(int acquires){
            /**
             * Walkthrough:
             * 1. If read count nonzero or write count nonzero
             *      and owner is a different thread, fail
             * 2. If count would saturate, fail. (This can only
             *      happen if count is already nonzero.)
             * 3. Otherwise this thread is eligible for lock if
             *      it is either a reentrant acquire or
             *      queue policy allows it. If so, update state
             *      and set owner.
             */
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);                      // 1. 获取现在writeLock 的获取的次数
            if(c != 0){
                // Note: if c != 0 and w == 0 then shared count != 0
                if(w == 0 || current != getExclusiveOwnerThread()){  // 2. 并发的情况来了, 这里有两种情况 (1) c != 0 &&  w == 0 -> 说明现在只有读锁的存在, 则直接 return, return后一般就是进入 aqs 的 sync queue 里面进行等待获取 (2) c != 0 && w != 0 && current != getExclusiveOwnerThread() 压根就是其他的线程获取 read/writeLock, 读锁是排他的, 所以这里也直接 return -> 进入 aqs 的 sync queue 队列
                    return false;
                }
                if(w + exclusiveCount(acquires) > MAX_COUNT){      // 3. 计算是否获取writeLock的次数 饱和了(saturate)
                    throw new Error("Maximum lock count exceeded");
                }
                // Reentrant acquire
                setState(c + acquires);                             // 4. 进行 state值得修改 (这里也不需要 CAS 为什么? 读锁是排他的, 没有其他线程和他竞争修改)
                return true;
            }
            if(writerShouldBlock() || !compareAndSetState(c, c + acquires)){  // 5. 代码运行到这里 (c == 0) 这时可能代码刚刚到这边时, 就有可能其他的线程获取读锁, 所以 c == 0 不一定了, 所以需要再次调用 writerShouldBlock查看, 并且用 CAS 来进行 state 值得更改
                return false;
            }
            setExclusiveOwnerThread(current);                       //  6. 设置 exclusiveOwnerThread writeLock 获取成功
            return true;
        }

        /**
         *  AQS 里面 releaseShared 的实现
         * @param unused
         * @return
         */
        protected final boolean tryReleaseShared(int unused){
            Thread current = Thread.currentThread();
            if(firstReader == current){                      // 1. 判断现在进行 release 的线程是否是 firstReader
                // assert firstReaderHoldCount > 0
                if(firstReaderHoldCount == 1){             // 2. 只获取一次 readLock 直接置空 firstReader
                    firstReader = null;
                }else{
                    firstReaderHoldCount--;                // 3. 将 firstReaderHoldCount 减 1
                }
            }else{
                HoldCounter rh = cachedHoldCounter;        // 4. 先通过 cachedHoldCounter 来取值
                if(rh == null || rh.tid != getThreadId(current)){  // 5. cachedHoldCounter 代表的是上次获取 readLock 的线程, 若这次进行 release 的线程不是, 再通过 readHolds 进行 lookup 查找
                    rh = readHolds.get();
                }
                int count = rh.count;
                if(count <= 1){
                    readHolds.remove();                     // 6. count <= 1 时要进行 ThreadLocal 的 remove , 不然容易内存泄露
                    if(count <= 0){
                        throw unmatchedUnlockException();   // 7. 并发多次释放就有可能出现
                    }
                }
                --rh.count;                                // 9. HoldCounter.count 减 1
            }

            for(;;){                                       // 10. 这里是一个 loop CAS 操作, 因为可能其他的线程此刻也在进行 release操作
                int c = getState();
                int nextc = c - SHARED_UNIT;             // 11. 这里是 readLock 的减 1, 也就是 aqs里面state的高 16 上进行 减 1, 所以 减 SHARED_UNIT
                if(compareAndSetState(c, nextc)){
                    /**
                     * Releasing the read lock has no effect on readers,
                     * but it may allow waiting writers to proceed if
                     * both read and write locks are now free
                     */
                    return nextc == 0;                   // 12. 返回值是判断 是否还有 readLock 没有释放完, 当释放完了会进行 后继节点的 唤醒( readLock 在进行获取成功时也进行传播式的唤醒后继的 获取 readLock 的节点)
                }
            }
        }

        private IllegalMonitorStateException unmatchedUnlockException(){
            return new IllegalMonitorStateException(
                    "attempt to unlock read lock, not locked by current thread"
            );
        }

        /**
         * AQS 中 acquireShared 的子方法
         * 主要是进行改变 aqs 的state的值进行获取 readLock
         * @param unused
         * @return
         */
        protected final int tryAcquireShared(int unused){
            /**
             * Walkthrough:
             *
             * 1. If write lock held by another thread, fail;
             * 2. Otherwise, this thread is eligible for
             *      lock wrt state, so ask if it should block
             *      because of queue policy, If not, try
             *      to grant by CASing state and updating count.
             *      Note that step does not check for reentrant
             *      acquires, which is postponed to full version
             *      to avoid having to check hold count in
             *      the more typical non-reentrant case.
             * 3. If step 2 fails either because thread
             *      apparently not eligible or CAS fails or count
             *      saturated, chain to version with full retry loop.
             */
            Thread current = Thread.currentThread();
            int c = getState();
            if(exclusiveCount(c) != 0 && getExclusiveOwnerThread() != current){         // 1. 判断是否有其他的线程获取了 writeLock, 有的话直接返回 -1 进行 aqs的 sync queue 里面
                return  -1;
            }
            int r = sharedCount(c);                                                    // 2. 获取 readLock的获取次数
            if(!readerShouldBlock() &&
                    r < MAX_COUNT &&
                    compareAndSetState(c, c + SHARED_UNIT)){                         // 3. if 中的判断主要是 readLock获取的策略, 及 操作 CAS 更改 state 值是否OK
                if(r == 0){                                                           //  4. r == 0 没有线程获取 readLock 直接对 firstReader firstReaderHoldCount 进行初始化
                    firstReader = current;
                    firstReaderHoldCount = 1;
                }else if(firstReader == current){                                  // 5. 第一个获取 readLock 的是 current 线程, 直接计数器加 1
                    firstReaderHoldCount++;
                }else{
                    HoldCounter rh = cachedHoldCounter;
                    if(rh == null || rh.tid != getThreadId(current)){               // 6. 还是上面的逻辑, 先从 cachedHoldCounter, 数据不对的话, 再从readHolds拿数据
                        cachedHoldCounter = rh = readHolds.get();
                    }else if(rh.count == 0){                                       // 7. 为什么要 count == 0 时进行 ThreadLocal.set? 因为上面 tryReleaseShared方法 中当 count == 0 时, 进行了ThreadLocal.remove
                        readHolds.set(rh);
                    }
                    rh.count++;                                                    // 8. 统一的 count++
                }
                return 1;
            }
            return fullTryAcquireShared(current);                                 // 9.代码调用 fullTryAcquireShared 大体情况是 aqs 的 sync queue 里面有其他的节点 或 sync queue 的 head.next 是个获取 writeLock 的节点, 或 CAS 操作 state 失败
        }

        /**
         * Full version of acquire for reads, that handles CAS misses
         * and reentrant reads not dealt with in tryAcquireShared.
         */
        /**
         *  fullTryAcquireShared 这个方法其实是 tryAcquireShared 的冗余(redundant)方法, 主要补足 readerShouldBlock 导致的获取等待 和 CAS 修改 AQS 中 state 值失败进行的修补工作
         */
        final int fullTryAcquireShared(Thread current){
            /**
             * This code is part redundant with that in
             * tryAcquireShared but is simpler overall by not
             * complicating tryAcquireShared with interactions between
             * retries and lazily reading hold counts
             */
            HoldCounter rh = null;
            for(;;){
                int c= getState();
                if(exclusiveCount(c) != 0){
                    if(getExclusiveOwnerThread() != current)                    // 1. 若此刻 有其他的线程获取了 writeLock 则直接进行 return 到 aqs 的 sync queue 里面
                        return -1;
                    // else we hold the exclusive lock; blocking here
                    // would cause deadlock
                }else if(readerShouldBlock()){                                 // 2. 判断 获取 readLock 的策略
                    // Make sure we're not acquiring read lock reentrantly
                    if(firstReader == current){                              // 3. 若是 readLock 的 重入获取, 则直接进行下面的 CAS 操作
                        // assert firstReaderHoldCount > 0
                    }else{
                        if(rh == null){
                            rh = cachedHoldCounter;
                            if(rh == null || rh.tid != getThreadId(current)){
                                rh = readHolds.get();
                                if(rh.count == 0){
                                    readHolds.remove();                       // 4. 若 rh.count == 0 进行 ThreadLocal.remove
                                }
                            }
                        }
                        if(rh.count == 0){                                    // 5.  count != 0 则说明这次是 readLock 获取锁的 重入(reentrant), 所以即使出现死锁, 以前获取过 readLock 的线程还是能继续 获取 readLock
                            return -1;                                        // 6. 进行到这一步只有 当 aqs sync queue 里面有 获取 readLock 的node 或 head.next 是获取 writeLock 的节点
                        }
                    }
                }

                if(sharedCount(c) == MAX_COUNT){                            // 7. 是否获取 锁溢出
                    throw new Error("Maximum lock count exceeded");
                }
                if(compareAndSetState(c, c + SHARED_UNIT)){                // 8.  CAS 可能会失败, 但没事, 我们这边外围有个 for loop 来进行保证 操作一定进行
                    if(sharedCount(c) == 0){                                //  9. r == 0 没有线程获取 readLock 直接对 firstReader firstReaderHoldCount 进行初始化
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    }else if(firstReader == current){                    // 10. 第一个获取 readLock 的是 current 线程, 直接计数器加 1
                        firstReaderHoldCount++;
                    }else{
                        if(rh == null){
                            rh = cachedHoldCounter;
                        }
                        if(rh == null || rh.tid != getThreadId(current)){
                            rh = readHolds.get();                       // 11. 还是上面的逻辑, 先从 cachedHoldCounter, 数据不对的话, 再从readHolds拿数据
                        }else if(rh.count == 0){
                            readHolds.set(rh);                          // 12. 为什么要 count == 0 时进行 ThreadLocal.set? 因为上面 tryReleaseShared方法 中当 count == 0 时, 进行了ThreadLocal.remove
                        }
                        rh.count++;
                        cachedHoldCounter = rh; // cache for release   // 13. 获取成功
                    }
                    return 1;
                }

            }
        }

        /**
         * Performs tryLock for write, enabling barging in both modes.
         * This is identical in effect to tryAcquire expect for lack
         * of calls to writerShouldBlock
         */
        /**
         * 尝试性的获取 writeLock 失败的话也无所谓
         * @return
         */
        final boolean tryWriteLock(){
            Thread current = Thread.currentThread();
            int c = getState();
            if(c != 0){
                int w = exclusiveCount(c);                              // 1. 获取现在writeLock 的获取的次数
                if(w == 0 || current != getExclusiveOwnerThread()){     // 2. 判断是否是其他的线程获取了 writeLock
                    return false;
                }
                if(w == MAX_COUNT){                                    // 3. 获取锁是否 溢出
                    throw new Error("Maximum lock count exceeded");
                }
            }

            if(!compareAndSetState(c, c + 1)){                         // 4. 这里有竞争, cas 操作失败也无所谓
                return false;
            }
            setExclusiveOwnerThread(current);                          // 5. 设置 当前的 exclusiveOwnerThread
            return true;
        }

        /**
         * Perform tryLock for read, enabling barging in both modes.
         * This is identical in effect to tryAcquireShared except for
         * lack of calls to readerShouldBlock
         */
        /**
         *  尝试性的获取一下 readLock
         */
        final boolean tryReadLock(){
            Thread current = Thread.currentThread();
            for(;;){
               int c = getState();
                if(exclusiveCount(c) != 0 &&
                        getExclusiveOwnerThread() != current){      // 1. 若当前有其他的线程获取 writeLock 直接 return
                    return false;
                }
                int r = sharedCount(c);                            // 2. 获取 readLock 的次数
                if(r == MAX_COUNT){
                    throw new Error("Maximum lock count exceeded");
                }
                if(compareAndSetState(c, c + SHARED_UNIT)){     // 3. CAS 设置 state
                    if(r == 0){                                  //  4. r == 0 没有线程获取 readLock 直接对 firstReader firstReaderHoldCount 进行初始化
                        firstReader = current;
                        firstReaderHoldCount = 1;
                    }else if(firstReader == current){          // 5. 第一个获取 readLock 的是 current 线程, 直接计数器加 1
                        firstReaderHoldCount++;
                    }else{
                        HoldCounter rh = cachedHoldCounter;
                        if(rh == null || rh.tid != getThreadId(current)){  // 6. 还是上面的逻辑, 先从 cachedHoldCounter, 数据不对的话, 再从readHolds拿数据
                            cachedHoldCounter = rh = readHolds.get();
                        }else if(rh.count == 0){
                            readHolds.set(rh);                  // 7. 为什么要 count == 0 时进行 ThreadLocal.set? 因为上面 tryReleaseShared方法 中当 count == 0 时, 进行了ThreadLocal.remove
                        }
                        rh.count++;
                    }
                    return true;
                }
            }
        }

        /** 判断当前线程是否 是 writeLock 的获取者 */
        protected final boolean isHeldExclusively(){
            /**
             * While we must in general read state before owner,
             * we don't need to do so to check if current thread is owner
             */
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        /** 创建一个 condition, condition 只用于 独占场景 */
        // Methods relayed to outer class
        final ConditionObject newCondition(){
            return new ConditionObject();
        }

        /** 判断当前线程是否 是 writeLock 的获取者 */
        final Thread getOwner(){
            // Must read state before owner to ensure memory consistency
            return ((exclusiveCount(getState()) == 0 )?
                    null :
                    getExclusiveOwnerThread());
        }

        /** 获取 readLock 的获取次数 */
        final int getReadLockCount(){
            return sharedCount(getState());
        }

        /** 判断 writeLock 是否被获取 */
        final boolean isWriteLocked(){
            return exclusiveCount(getState()) != 0;
        }

        /** 获取 writeLock 的获取次数 */
        final int getWriteHoldCount(){
            return isHeldExclusively()?exclusiveCount(getState()) : 0;
        }

        /** 获取当前线程获取 readLock 的次数 */
        final int getReadHoldCount(){
            if(getReadLockCount() == 0){
                return 0;
            }

            Thread current = Thread.currentThread();
            if(firstReader == current){
                return firstReaderHoldCount;
            }

            HoldCounter rh = cachedHoldCounter;
            if(rh != null && rh.tid == getThreadId(current)){
                return rh.count;
            }

            int count = readHolds.get().count;
            if(count == 0) readHolds.remove();
            return count;
        }

        /** 序列化的恢复操作 */
        // Reconstitutes the instance from a stream (that is, deserializes it).
        private void readObject(ObjectInputStream s) throws Exception{
            s.defaultReadObject();
            readHolds = new ThreadLocalHoldCounter();
            setState(0); // reset to unlocked state
        }

        final int getCount(){
            return getState();
        }
    }

    /**
     * Nonfair version of Sync
     * 非公平版本 sync
     */
    static final class NonfairSync extends Sync{
        private static final long serialVersionUID = -8159625535654395037L;
        @Override
        boolean readerShouldBlock() {
            /**
             * As a heuristic(启发性的) to avoid indefinite(不确定, 无限) writer starvation,
             * block if the thread that momentarily appears to be head
             * of queue, if one exists, is a waiting writer. This is
             * only a probabilistic effect since a new reader will not
             * block if there is a waiting writer behind other enabled
             * reader that have not yet drained from the queue
             */
            /** readLock 的获取主要看 aqs sync queue 队列里面的 head.next 是否是获取 读锁的 */
            return apparentlyFirstQueuedIsExclusive();
        }

        @Override
        boolean writerShouldBlock() { // 获取 writeLock 的话 直接获取
            return false; // writers can always barge
        }
    }

    /**
     * Fair version of Sync
     * 公平版的 sync
     */
    static final class FairSync extends Sync{
        private static final long serialVersionUID = -2274990926593161451L;

        /**
         * readerShouldBlock writerShouldBlock 都是看 aqs sync queue 里面是否有节点
         */
        @Override
        boolean readerShouldBlock() {
            return hasQueuedPredecessors();
        }

        @Override
        boolean writerShouldBlock() {
            return hasQueuedPredecessors();
        }
    }

    /**
     * The lock returned by method {@link KReentrantReadWriteLock}
     * 读锁
     */
    public static class ReadLock implements Lock, Serializable{
        private static final long serialVersionUID = -5992448646407690164L;

        private final Sync sync;

        /**
         * Constructor for use by subclasses
         *
         * @param lock the outer lock object
         * @throws NullPointerException if the lock is null
         */
        protected ReadLock(KReentrantReadWriteLock lock){
            sync = lock.sync;
        }

        /**
         * Acquires the read lock.
         *
         * <p>
         *     Acquires the read lock if the write lock is not held by
         *     another thread and returns immediately
         * </p>
         *
         * <p>
         *     If the write lock is held by another thread then
         *     the current thread becomes disabled for thread scheduling
         *     purposes and lies dormant until the read lock has been acquired
         * </p>
         */
        /**
         * 所得获取都是调用 aqs 中 acquireShared
         */
        public void lock() {
            sync.acquireShared(1);
        }

        /**
         * Acquires the read lock unless the current thread is
         * {@link Thread#interrupt() interrupted}
         *
         * <p>
         *     Acquires the read lock if the write lock is not held
         *     by another thread and returns immediately
         * </p>
         *
         * <p>
         *     If the write lock is held by another thread then the
         *     current thread becomes disabled for thread scheduling
         *     purpose and lies dormant until one two things happens:
         *
         *     <li>
         *         The read lock is acquired by the current thread; or
         *         Some other thread {@link Thread#interrupt() interrupts}
         *         the current thread
         *     </li>
         *     <li>
         *         If the current thread has its interrupted status set on entry
         *         to this method; or is {@link Thread#interrupt() interrupted} while
         *         acquiring the read lock, then {@link InterruptedException} is thrown
         *         and the current
         *         thread's interrupted status is cleared.
         *     </li>
         * </p>
         *
         * <p>
         *     In this implementation, as this method is an explicit
         *     interruption point, preference(偏爱) is given to responding to
         *     the interrupt over normal or reentrant acquisition(获得) of the
         *     lock.
         * </p>
         *
         * @throws InterruptedException if the current thread is interrupted
         */
        /**
         *  支持中断的获取锁
         */
        public void lockInterruptibly() throws InterruptedException{
            sync.acquireSharedInterruptibly(1);
        }

        /**
         * Acquires the read lock only if the write lock is not held by
         * another thread at the time of invocation
         *
         * <p>
         *     Acquires the read lock if the write lock is not held by
         *     another thread and returns immediately with the value
         *     {@code true}. Even when this lock has been set to use a
         *     fair ordering policy. a call to {@code tryLock()}
         *     <em>will</em> immediately acquire the read lock if it is
         *     available, whether or not other threads are currently
         *     waiting for the read lock. This barging behavior
         *     can be useful in cretain circumstances, even though it
         *     breaks fairness. If you want to honor the fairnexx setting
         *     for this lock, then use {@link #"tryLock(long TimeUnit)}
         *     tryLock(0, TimeUnit.SECONDS) which is almost equivalent
         *     (it also detects interruption)
         * </p>
         *
         * <p>
         *     If the write lock is held by another thread then
         *     this method will return immediately with the value
         *     {@code false}
         * </p>
         *
         * @return {@code true} if the read lock was acquired
         */
        /**
         *  尝试获取锁
         */
        public boolean tryLock(){
            return sync.tryReadLock();
        }

        /**
         * Acquires the read lock if the write lock is not held by
         * another thread within the given waiting time and the
         * current thread has not veen {@link Thread#interrupt() interrupted}
         *
         * <p>
         *     Acquires the read lock if the write lock is not held by
         *     another thread and returns immediately with the value
         *     {@code true}. If this lock has been set to use a fair
         *     ordering policy then an available lock <em>will not</em> be
         *     acquired if any other threads are waiting for the
         *     lock. This is in contrast to the {@link #tryLock()}
         *     method. If you wait a timed {@code tryLock} that does
         *     permit barging on a fair lock then combine the timed and
         *     un-timed forms together:
         *
         *     <pre>
         *         {@code
         *          if(lock.tryLock() ||
         *              lock.tryLock()timeout, unit)
         *              ...
         *         }
         *     </pre>
         * </p>
         *
         * <p>
         *     If the write lock is held by another thread then the
         *     current thread becomes disabled for thread scheduling
         *     purposes and lies dormant until one of three things happen:
         *
         *     <li>
         *         The read lock is acquired by the current thread; or
         *         Some other thread {@link Thread#interrupt() interrupts}
         *         the current thread; or
         *         The specified waiting time elapses.
         *     </li>
         * </p>
         *
         * <p>
         *     If the read lock is acquired then the value {@code true} is
         *     returned
         * </p>
         *
         * <p>
         *     If the current thread:
         *     has its interrupted status set on entry to this method; or
         *     is {@link InterruptedException} is thrown and the
         *     current's thread's interrupted status is cleared.
         * </p>
         *
         * <p>
         *     If the specified waiting time elapses then the value
         *     {@code false} is returned. If the time is less than or
         *     equal to zero, the method will not wait at all.
         * </p>
         *
         * <p>
         *     In this implementation, as this method is an explicit
         *     interruption point, preference is given to responding to
         *     the interrupt over normal or reentrant acquisition of the
         *     lock, and over reporting the elapse of the waiting time.
         * </p>
         *
         * @param timeout the time to wait for the read lock
         * @param unit the time unit of the timeout argument
         * @return {@code true} if the read lock was acquired
         * @throws InterruptedException if the current thread is interrupted
         * @throws NullPointerException if the time unit is null
         */
        /**
         *  支持中断与 timeout 的获取 writeLock
         */
        public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException{
            return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
        }

        /**
         * Attempts to release this lock
         *
         * <p>
         *     If the number of readers is nw zero then the lock
         *     is made available for write lock attempts
         * </p>
         */
        /**
         * 释放 readLock
         */
        public void unlock(){
            sync.releaseShared(1);
        }

        /**
         * Throws {@code UnsupportedOperationException} because
         * {@code ReadLocks} do not support conditions
         *
         * @throws UnsupportedOperationException
         */
        /**
         *  创建一个 condition
         */
        public Condition newCondition(){
            throw new UnsupportedOperationException();
        }

        /**
         * Returns a string identifying this lock, as well as its lock state.
         * The state, in brackets, includes the String {@code "Read locks = "}
         * followed by the number of held read locks
         *
         * @return a string identifying this lock, as well as its lock state
         */
        public String toString(){
            int r = sync.getReadLockCount();
            return super.toString() + " [Read locks = " + r + "]";
        }
    }

    /**
     * The lock returned by method {@link KReentrantReadWriteLock}
     */
    /**
     * 写锁
     */
    public static class WriteLock implements Lock, Serializable{
        private static final long serialVersionUID = -4992448646407690164L;

        private final Sync sync;

        /**
         * Constructor for use by subclasses
         *
         * @param lock the outer lock object
         * @throws NullPointerException if the lock is null
         */
        protected WriteLock(KReentrantReadWriteLock lock){
            sync = lock.sync;
        }

        /**
         * Acquires the write lock
         *
         * <p>
         *     Acquires the write lock if neither the read nor write lock
         *     are held by another thread
         *     and returns immediately, setting the write lock hold count to
         *     one.
         * </p>
         *
         * <p>
         *     If the lock is held by another thread then the current
         *     thread becomes disabled for thread scheduling purposes and
         *     lies dormant until the write lock has been acquired, at which
         *     time the write lock hold count is set to one.
         * </p>
         */
        /**
         * 调用 aqs 的 acquire 来获取 锁
         */
        public void lock(){
            sync.acquire(1);
        }

        /**
         * Acquires the write lock unless the current thread is
         * {@link Thread#interrupt() interrupted}.
         *
         * <p>
         *     Acquires the write lock if neither the read nor write lock
         *     are held by another thread
         *     and returns immediately, setting the write lock hold count to
         *     one
         * </p>
         *
         * <p>
         *     If the current thread already holds this lock then the
         *     hold count is incremented by onr and the method returns
         *     immediately
         * </p>
         *
         * <p>
         *     If the lock is held by another thread then the current
         *     thread becomes disabled for thread scheduling purposes and
         *     lies dormant until one of two things happens:
         *
         *     <li>
         *         The write lock is acquired by the current thread; or
         *         Some other thread {@link Thread#interrupt() interrupts}
         *         the current thread
         *     </li>
         * </p>
         *
         * <p>
         *     If the write lock is acquired by the current thread then the
         *     lock hold count is set to one.
         * </p>
         *
         * <p>
         *     If the current thread:
         *     has its interrupted status set on entry to this method;
         *     or
         *     is {@link Thread#interrupt() interrupted} while
         *     acquiring the write lock,
         *     then {@link InterruptedException} is thrown and the current
         *     thread's interrupted status is cleared
         * </p>
         *
         * <p>
         *     In this implementation, as this method is an explicit
         *     interruption point, preference is given to responding to
         *     the interrupt over normal or reentrant acquisition of the
         *     lock.
         * </p>
         *
         * @throws InterruptedException if the current thread is interrupted
         */
        /**
         *  支持中断方式的获取 writeLock
         */
        public void lockInterruptibly() throws InterruptedException{
            sync.acquireInterruptibly(1);
        }

        /**
         * Acquires the write lock only if it is not held by another thread
         * at the time of invocation
         *
         * <p>
         *     Acquires the write lock if neither the read nor write lock
         *     are held by another thread
         *     and returns immediately with the value {@code true},
         *     setting the write lock hold count to one. Even when this lock has
         *     been set to use a fair ordering policy, a call to
         *     {@code tryLock} <em>will</em> immediately acquire the
         *     lock if it available, whether or not other threads are
         *     currently waiting for the write lock. This barging
         *     behavior can be useful in certain circumstances, even
         *     though it breaks fairness, If you want to honor the
         *     fairness setting for this lock, then use {@link #tryLock(long, TimeUnit)}
         *     tryLock(0, TimeUnit.SECONDS), which is almost equivalent
         *     (it also detects interruption)
         * </p>
         *
         * <p>
         *     If the current thread already holds this lock then the
         *     hold count is incremented by one the method returns
         *     {@code true}
         * </p>
         *
         * <p>
         *     If the lock is held by another thread then this method
         *     will return immediately with value {@code false}
         * </p>
         *
         * @return {@code true} if the lock was free and was acquired
         * by the current thread, or the write lock was already held
         * by the current thread; and {@code false} otherwise
         */
        /**
         *  尝试性的获取锁
         */
        public boolean tryLock(){
            return sync.tryWriteLock();
        }

        /**
         * Acqiure the write lock if it is not held by another thread
         * within the given waiting time and the current thread has
         * not been {@link Thread#interrupt() interrupted}.
         *
         * <p>
         *     Acquires the write lock if neither the read nor write lock
         *     are held by another thread
         *     and returns immediately with the value {@code true},
         *     setting the write lock hold count to one, If this lock has been
         *     set to use a fair ordering policy then an available lock
         *     <em>will not</em> be acquired if any other threads are
         *     waiting for the write lock. This is in contrast to the {@link
         *     #tryLock()} method. If you want a timed {@code tryLock}
         *     that does permit barging on a fair lock then combine the
         *     timed and un-timed forms together:
         * </p>
         *
         * <pre>
         *     {@code
         *      if(lock.tryLock() ||
         *          lock.tryLock(timeout, unit)){
         *          ...
         *     }
         * </pre>
         *
         * <p>
         *     If the current thread already holds this lock then the
         *     hold count is incremented by one and the menthod returns
         *     {@code true}
         * </p>
         *
         * <p>
         *     If the lock is held by another thread then the current
         *     thread becomes disabled for thread scheduling purposes and
         *     lies dormant until one of three things happens:
         *
         *     <li>
         *         The write lock is acquired by the current thread; or
         *         Some other thread {@link Thread#interrupt() interrupts}
         *         the current thread; or
         *     </li>
         *     <li>
         *         The specified waiting time elapses
         *     </li>
         * </p>
         *
         * <p>
         *     If the write lock is acquired then the value {@code true} is
         *     returned and the write lock hold count is set to one.
         * </p>
         *
         * <p> If the current thread:
         *
         * <li>has its interrupted status set on entry to this method:</li>
         * or
         * <li>is {@link Thread#interrupt() interrupted} while</li>
         * acquiring the write lock
         *
         * then {@link InterruptedException} is thrown and the current
         * thread's interrupted status is cleared
         *
         * <p> In this implementation, as this method is an explicit
         * interruption point, preference is given to responding to
         * the interrupt over narmal or reentrant acquisition of the
         * lock, and over reporting the elapse of the waiting time
         *
         *
         * @param timeout the time to wait for the write lock
         * @param unit the time unit of the timeout argument
         * @return  {@code true} if the lock was free and was acquired
         *          by the current thread, or the write lock was already held by the
         *          current thread; and {@code false} if the waiting time
         *          elapsed before the lock could be acquired
         *
         * @throws InterruptedException if the current thread is interrupted
         * @throws NullPointerException if the time unit is null
         */
        /**
         * 支持中断 timeout 方式获取锁
         */
        public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException{
            return sync.tryAcquireNanos(1, unit.toNanos(timeout));
        }

        /**
         * Attempts to release this lock
         *
         * <p>
         *     If the current thread is the holder of this lock then
         *     the hold count is decremented. If the hold count is now
         *     zero then the lock is released. If the current thread is
         *     not the holder of this lock then {@link IllegalMonitorStateException}
         *     is thrown
         * </p>
         *
         * @throws IllegalMonitorStateException if the current thread does not
         *          hold this lock
         */
        /**
         * 释放锁
         */
        public void unlock(){
            sync.release(1);
        }

        /**
         * Returns a {@link Condition} instance for use with this
         * {@link Lock} instance.
         *
         * <p>
         *      The returned {@link Condition} instance supports the same
         *      usages as do the {@link Object} monitor methods ({@link
         *      Object#wait() wait} {@link Object#notify()}), and {@link
         *      Object#notifyAll()} when used with the built-in
         *      monitor lock
         * </p>
         *
         * <li>
         *     If this write lock is not held when any {@link Condition}
         *     method is called then an {@link IllegalMonitorStateException}
         *     is thrown. (Read locks are
         *     held independently of write locks, so are not checked or
         *     affected. However it is essentially always an error to
         *     invoke a condition waiting method when the current thread
         *     has also acquired read locks, since other threads that
         *     could unblock it will not be able to acquire the write
         *     lock.)
         * </li>
         *
         * <li>
         *     When the condition {@link Condition#await() waiting}
         *     methods are called the write lock is released and, before
         *     they return, the write lock is reacquired and the lock hold
         *     count restored to what it was the method was called.
         * </li>
         *
         * <li>
         *     If a thread is {@link Thread#interrupt() interrupted} while
         *     waiting then the wait will terminate, an {@link
         *     InterruptedException} will be thrown, and the thread's
         *     interrupted status will be cleared.
         * </li>
         *
         * <li>
         *     Waiting threads are signalled in FIFO order.
         * </li>
         *
         * <li>
         *     The ordering of lock reacquisition for threads returning
         *     from waiting methods is the same as for threads initially
         *     acquiring the lock, which is in the default case not specified,
         *     but for <em>fair</em> locks favors those threads that have been
         *     waiting the longest
         * </li>
         *
         *
         * @return the Condition object
         */
        /**
         *  new 一个 condition
         */
        public Condition newCondition(){
            return sync.newCondition();
        }

        /**
         * Returns a string identifying this lock, as well as its lock
         * state. The state, in brackets includes either the String
         * {@code "Unlocked"} or the String {@code "Locked by"}
         * followed by the {@link Thread#getName()} of the owning thread.
         *
         * @return a string identifying this lock, as well as its lock state
         */
        public String toString(){
            Thread o = sync.getOwner();
            return super.toString() + ((o == null) ?
                    "[Unlocked]" : "[Locked by thread " + o.getName() + "]");
        }

        /**
         * Queries if this write lock is held by the current thread.
         * Identical in effect to {@link KReentrantReadWriteLock#"isWriteLockedByCurrentThread}
         *
         * @return {@code true} if the current thread holds this lock and
         *          {@code false } otherwise
         */
        /**
         *  判断当前的 writeLock 是否被 本线程 占用
         */
        public boolean isHeldByCurrentThread(){
            return sync.isHeldExclusively();
        }

        /**
         * Queries the number of holds on this write lock by the current
         * thread. A thread has a hold on a lock for each lock action
         * that is not matched by an unlock action. Identical in effect
         * to {@link KReentrantReadWriteLock#"getWriteHoldCount}
         *
         * @return the number of holds on this lock by the current thread,
         *          or zero if this lock is not held by the current thread
         */
        /**
         *  获取 writeLock 的获取次数
         */
        public int getHoldCount(){
            return sync.getWriteHoldCount();
        }

    }

    /***************** Instrumentation and status *******************/

    /**
     * Returns {@code true} if this lock has fairness set true
     *
     * @return {@code true} if this lock has fairness set true
     */
    /**
     * ReentrantReadWriteLock 是否是公平的
     */
    public final boolean isFair(){
        return sync instanceof FairSync;
    }

    /**
     * Returns the thread that currently owns the write lock, or
     * {@code null} if not owned. When this methods is called by a
     * thread that is not the owner, the return value reflects a
     * best-effort approximation of current lock status. For example,
     * the owner may be momentarily {@code null} even if there are
     * threads trying to acquire the lock but have not yet done so.
     * This method is designed to facilitate construction of
     * subclasses that provide more extensive lock monitoring
     * facilities.
     *
     * @return the owner, or {@code null} if not owned
     */
    /**
     *  判断当前线程是否获取writeLock
     */
    protected Thread getOwner(){
        return sync.getOwner();
    }

    /**
     * Queries the number of read locks held for this lock. This
     * method is designed for use in monitoring system state, not for
     * synchronization control
     *
     * @return the number of read locks held
     */
    /**
     * readLock 的获取次数
     */
    public int getReadLockCount(){
        return sync.getReadLockCount();
    }

    /**
     * Queries if the write lock is held b any thread. This method is
     * designed for use in monitoring system state, not for
     * synchronization control.
     *
     * @return {@code true} if any thread holds the write lock and
     *          {@code false} otherwise
     */
    /**
     *  writeLock 是否被获取了
     */
    public boolean isWriteLocked(){
        return sync.isWriteLocked();
    }

    /**
     * Queries if the write lock is held by the current thread.
     *
     * @return {@code true} if the current thread holds the write lock and
     *      {@code false} otherwise
     */
    /**
     * 当前线程是否获取 writeLock
     */
    public boolean isWriteLockedByCurrentThread(){
        return sync.isHeldExclusively();
    }

    /**
     * Queries the number of reentrant write holds on this lock by the
     * current thread. A writer thread has a hold on a lock for
     * each lock action that is not matched by an unlock action
     *
     * @return the number of holds on the write lock by the current thread
     *          or zero if the write lock is not held by the current thread
     */
    /**
     * writeLock 的获取次数
     */
    public int getWriteHoldCount(){
        return sync.getWriteHoldCount();
    }

    /**
     * Queries the number of reentrant read holds on this lock by the
     * current thread. A reader thread has a hold on a lock for
     * each lock action that is not matched by an unlock action
     *
     * @return the number of holds o the read lock by the current thread,
     *      or zero if the read lock is not held by the current thread
     */
    /**
     * readLock 的获取次数
     */
    public int getReadHoldCount(){
        return sync.getReadHoldCount();
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire the write lock. Because the actual set of threads may
     * change dynamically while constructing this result, the returned
     * collection is only a best-effort estimate. The elements of the
     * returned collection are in no particular order. This method is
     * designed to facilitate construction of subclasses that provide
     * more extensive lock monitoring facilities
     *
     * @return the collection of threads
     */
    /**
     * aqs sync queue 里面获取 writeLock 的线程
     */
    protected Collection<Thread> getQueuedWriterThreads(){
        return sync.getExclusiveQueuedThreads();
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire the read lock. Because the actual set of threads may
     * change dynamically while constructing this result, the returned
     * collection is only a best-effort estimate. The elements of the
     * returned collection are in no particular order. This method is
     * designed to facilitate construction of subclass that provide
     * more extensive lock monitoring facilities
     * @return
     */
    /**
     * aqs sync queue 里面获取 readLock 的线程
     */
    protected Collection<Thread> getQueuedReaderThreads(){
        return sync.getSharedQueuedThreads();
    }

    /**
     * Queries whether any threads are waiting to acquire the read or
     * write lck. Note that because cancellations may occur at any
     * time, a {@code true} return does not guarantee that any other
     * thread will ever acquire a lock. This method is designed
     * primarily for use in monitoring of the system state.
     *
     * @return {@code true} if there may be other threads waiting to
     *          acquire the lock
     */
    /**
     * aqs sync queue里面是否有 node
     */
    public final boolean hasQueuedThreads(){
        return sync.hasQueuedThreads();
    }

    /**
     * Queries whether the given thread is waiting to acquire either
     * the read or write lock. Note that because cancellations may
     * occur at any time , a {@code true} return does not guarantee
     * that this thread will ever acquire a lock. This method is
     * designed primarily for use in monitoring of the system state.
     *
     * @param thread the thread
     * @return {@code true} if the given thread is queued waiting for this lock
     * @throws NullPointerException if the thread is null
     */
    /**
     * 当前线程是否在 aqs sync queue 里面
     */
    public final boolean hasQueuedThread(Thread thread){
        return sync.isQueued(thread);
    }


    /**
     * Returns an estimate of the number of threads waiting to acquire
     * either the read or write lock.  The value is only an estimate
     * because the number of threads may change dynamically while this
     * method traverses internal data structures. This method is
     * designed for use in monitoring of the system state, not for
     * synchronization control.
     *
     * @return the estimated numberof threads waiting for this lock
     */
    /**
     * aqs sync queue 长度
     */
    public final int getQueueLength(){
        return sync.getQueueLength();
    }

    /**
     * Returns a collection containing threads that may be waiting to
     * acquire either the read or write lock. Because the actual set
     * of threads may change dynamically while constructing this
     * result, the returned collection is only a best-effort estimate.
     * The elements of the returned collection are in no particular
     * order. This method is designed to facilitate construction of
     * subclass that provides more extensive monitoring facilities.
     *
     * @return the collection of threads
     */
    /**
     * aqs sync queue 里面的线程
     */
    protected Collection<Thread> getQueuedThreads(){
        return sync.getQueuedThreads();
    }

    /**
     * Queries whether any threads are waiting on the given condition
     * associated with the write lock. Note that because timeouts and
     * interrupts may occur at any time, a {@code true} return does
     * not guarantee that a future {@code signal} will awaken any
     * threads. This method is designed primarily for use in
     * monitoring of the system state.
     *
     * @param condition the condition
     * @return {@code true} if there are any waiting threads
     * @throws IllegalMonitorStateException if this lock is not held
     * @throws IllegalArgumentException if the given condition is
     *          not associated with this lock
     * @throws NullPointerException if the condition is null
     */
    /**
     * 是否有线程在 condition 里面进行等待获取 writeLock
     */
    public boolean hasWaiters(Condition condition){
        if(condition == null){
            throw new NullPointerException();
        }
        if(!(condition instanceof KAbstractQueuedSynchronizer.ConditionObject)){
            throw new IllegalArgumentException("not owner");
        }

        return sync.hasWaiters((MyAbstractQueuedSynchronizer.ConditionObject)condition);
    }


    /**
     * Returns an estimate of the number of threads waiting on the
     * given condition associated with the write lock. Note that because
     * timeouts and interrupts may occur at any time, the estimate
     * serves only as an upper bound on the actual number of waiters.
     * This method is designed for use in monitoring of the system
     * state, not for synchronization control
     *
     * @param condition the condition
     *
     * @return the estimated number of waiting threads
     */
    /**
     *  condition queue 里面线程的多少
     */
    public int getWaitQueueLength(Condition condition){
        if(condition == null){
            throw new NullPointerException();
        }
        if(!(condition instanceof KAbstractQueuedSynchronizer.ConditionObject)){
            throw new IllegalArgumentException("not owner");
        }

        return sync.getWaitQueueLength((MyAbstractQueuedSynchronizer.ConditionObject)condition);
    }

    /**
     * Returns a collection containing those threads that may be
     * waiting on the given condition associated with the write lock.
     * Because the actual set of threads may change dynamically while
     * constructing this result. The elements of the returned collection
     * are in no particular order. This method is designed collection
     * facilitate construction of subclasses that provide more
     * extensive condition monitoring facilities
     *
     * @param condition the condition
     * @return the collection of threads
     */
    /**
     *  condition queue 里面的线程
     */
    protected Collection<Thread> getWaitingThreads(Condition condition){
        if(condition == null){
            throw new NullPointerException();
        }
        if(!(condition instanceof KAbstractQueuedSynchronizer.ConditionObject)){
            throw new IllegalArgumentException("not owner");
        }
        return sync.getWaitingThreads((MyAbstractQueuedSynchronizer.ConditionObject)condition);
    }


    /**
     * Returns a string identifying this lock, as well as its lock state.
     * The state, in brackets, includes the String {@code " Write locks ="}
     * followed by the number of reentrantly held write locks, and the
     * String {@code " Read locks = "} followed by the number of held
     * read locks
     *
     * @return a String identifying this lock, as well as its lock state
     */
    public String toString(){
        int c = sync.getCount();
        int w = Sync.exclusiveCount(c);
        int r = Sync.sharedCount(c);

        return super.toString() +
                "[Write locks = " + w + ", Read locks = " + r + " ]";
    }



    /**
     * Returns the thread id for the fiven thread. We must access
     * this directly rather than via method Thread.getId() because
     * getId() is not final, and has been known to be overridden in
     * ways that do not preserve unique mapping
     */
    /**
     *  unsafe 类的操作
     */
    static final long getThreadId(Thread thread){
        return UNSAFE.getLongVolatile(thread, TID_OFFSET);
    }

    // Unsafe mechanics
    private static final Unsafe UNSAFE;
    private static final long TID_OFFSET;

    static {
        try{
            UNSAFE = UnSafeClass.getInstance();
            Class<?> tk = Thread.class;
            TID_OFFSET = UNSAFE.objectFieldOffset(tk.getDeclaredField("tid"));
        }catch (Exception e){
            throw new Error(e);
        }
    }

}
