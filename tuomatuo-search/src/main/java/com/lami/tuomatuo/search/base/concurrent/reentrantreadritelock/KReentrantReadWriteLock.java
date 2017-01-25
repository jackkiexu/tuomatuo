package com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock;

import com.lami.tuomatuo.search.base.concurrent.aqs.KAbstractQueuedSynchronizer;
import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
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
 *     constraints. A nonfair lock that is continously contended may
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
 *     writer thread. The thread will not acquirethe read lock until
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
 *     readers are not alloweduntil allwrite locks held by the writing
 *     thread have been released.
 * </p>
 *
 * <p>
 *     Additionally, a writer can acquire the read lock, but not
 *     vice-versa. Among other applications, reentrancy can be useful
 *     when write locks are heldduring calls or callbacks to method that
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


    @Override
    public Lock readLock() {
        return null;
    }

    @Override
    public Lock writeLock() {
        return null;
    }

    /**
     * Synchronization implementation for ReentrantReadWriteLock
     * Subclassed into fair and nonfair versions
     */
    abstract static class Sync extends KAbstractQueuedSynchronizer{
        private static final long serialVersionUID = 6317671515068378041L;

        /**
         * Read vs write count extraction constants and functions.
         * Lock state is logically divided into two unsigned shorts:
         * The lower one representing the exclusive(writer) lock hold count,
         * and the upper the shared (reader) hold count.
         */

        static final int SHARED_SHIFT   = 16;
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        /** Returns the number of shared holds represented in count */
        static int sharedCount(int c)       { return c >>> SHARED_SHIFT; }
        /** Returns the number of exclusive holds represented in count */
        static int exclusiveCount(int c)    { return c & EXCLUSIVE_MASK; }

        /**
         * A counter for per-thread read hold counts
         * Maintained as a ThreadLocal; cached in cachedHoldCounter
         */
        static final class HoldCounter {
            int count = 0;
            // Use id, not reference, to avoid garbage retention
            final long tid = getThreadId(Thread.currentThread());
        }

        /**
         * ThreadLocal subclass, Easiest to explicitly define for sake
         * of deserialization mechanics
         */
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
         *     Accessed via a benign data race; relies on the memory
         *     model's out-of-thin-air guarantees for references.
         * </p>
         *
         * <p>
         *     This allows tracking of read holds for uncontended read
         *     locks to be very cheap
         * </p>
         */
        private transient Thread firstReader = null;
        private transient int    firstReaderHoldCount;

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
        abstract boolean readerShouldBlock();

        /**
         * Returns true if the current thread, when trying to acquire
         * the write lock, and otherwise eligible to do so, should block
         * because of policy for overtaking other waiting threads.
         */
        abstract boolean writerShouldBlock();

        /**
         * Note that tryRelease and tryAcquire can be called by
         * Conditions. So it is possible that their arguments contain
         * both read and write holds that are all released during a
         * condition wait and re-established in tryAcquire
         */
        protected final boolean tryRelease(int releases){
            if(!isHeldExclusively()){
                throw new IllegalMonitorStateException();
            }
            int nextc = getState() - releases;
            boolean free = exclusiveCount(nextc) == 0;
            if(free){
                setExclusiveOwnerThread(null);
            }
            setState(nextc);
            return free;
        }

        protected final boolean tryAcquire(int acquires){
            /**
             * Walkthrough:
             * 1. If read count nonzero or write count nonzero
             *      and owner is a different thread, fail
             * 2. If count would saturate, fail. (This can only
             *      happen if count is already nonzero.)
             * 3. Otherwisethis thread is eligible for lock if
             *      it is either a reentrant acquire or
             *      queue policy allows it. If so, update state
             *      and set owner.
             */
            Thread current = Thread.currentThread();
            int c = getState();
            int w = exclusiveCount(c);
            if(c != 0){
                // Note: if c != 0 and w == 0 then shared count != 0
                if(w == 0 || current != getExclusiveOwnerThread()){
                    return false;
                }
                if(w + exclusiveCount(acquires) > MAX_COUNT){
                    throw new Error("Maximum lock count exceeded");
                }
                // Reentrant acquire
                setState(c + acquires);
                return true;
            }
            if(writerShouldBlock() || !compareAndSetState(c, c + acquires)){
                return false;
            }
            setExclusiveOwnerThread(current);
            return true;
        }


    }


    /**
     * Returns the thread id for the fiven thread. We must access
     * this directly rather than via method Thread.getId() because
     * getId() is not final, and has been known to be overridden in
     * ways that do not preserve unique mapping
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
