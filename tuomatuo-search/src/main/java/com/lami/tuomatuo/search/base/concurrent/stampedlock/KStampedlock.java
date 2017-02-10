package com.lami.tuomatuo.search.base.concurrent.stampedlock;

import com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock.KReentrantReadWriteLock;
import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by xujiankang on 2017/2/10.
 */
public class KStampedlock implements Serializable {

    private static final long serialVersionUID = -6001602636862214147L;

    /** Number of processors, for spin control */
    private static final int NCPU = Runtime.getRuntime().availableProcessors();

    /** Maximum number of retries before enqueuing on acquisition */
    private static final int SPINS = (NCPU > 1)? 1 <<6 : 0;

    /** Maximum number of retries before blocking at head on acquisition */
    private static final int HEAD_SPINS = (NCPU > 1) ? 1 << 10 : 0;

    /** Maximum number of retries before re-blocking */
    private static final int MAX_HEAD_SPINS = (NCPU > 1)? 1 << 16 : 0;

    /** The period for yielding whn waiting for overflow spinlock */
    private static final int OVERFLOW_YIELD_RATE = 7; // must be power 2 -1

    /** The number of bits to use for reader count before overflowing */
    private static final int LG_READERS = 7;

    // Values for lock state and stamp operations
    private static final long RUNIT = 1L;
    private static final long WBIT = 1L << LG_READERS;
    private static final long RBITS = WBIT - 1L;
    private static final long RFULL = RBITS - 1L;
    private static final long ABITS = RBITS | WBIT;
    private static final long SBITS = ~RBITS; // note overlap with ABITS


    // Initial value for lock state; avoid failure value zero
    private static final long ORIGIN = WBIT << 1;

    // Special value from cancelled acquire methods so caller can throw IE
    private static final long INTERRUPTED = 1L;

    // Values for nodes status; order matters
    private static final int WAITING    = -1;
    private static final int CANCELLED  = 1;

    // Mode for nodes (int not boolean to allow arithmetic)
    private static final int RMODE = 0;
    private static final int WMODE = 1;


    /** Wait nodes */
    static final class WNode {
        volatile WNode prev;
        volatile WNode next;
        volatile WNode cowait;  // list of linked readers
        volatile Thread thread; // non-null while possibly parked
        volatile int status;   // 0, WAITING, or CANCELLED
        final int mode;         // RMODE or WMODE
        WNode(int m, WNode p) {
            mode = m; prev = p;
        }
    }

    /** Head of CLH queue */
    private transient volatile WNode whead;
    /** Tail (last) of CLH queue */
    private transient volatile WNode wtail;

    // views
    transient ReadLockView readLockView;
    transient WriteLockView writeLockView;
    transient ReadWriteLockView readWriteLockView;

    /** Lock sequence/state */
    private transient volatile long state;
    /** extra reader count when state read count saturated */
    private transient int readerOverflow;

    /** Creates a new lock, initially in unlocked state */
    public KStampedlock() {
        state = ORIGIN;
    }

    /**
     * Returns a {@link ReadWriteLock} view
     *
      * @return the lock
     */
    public ReadWriteLock asReadWriteLock(){
        ReadWriteLockView v;
        return ((v = readWriteLockView) != null ? v :
                (readWriteLockView = new ReadWriteLockView()));
    }

    // view classes
    final class ReadLockView implements Lock {

        @Override
        public void lock() {

        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void unlock() {

        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }

    final class WriteLockView implements Lock {

        @Override
        public void lock() {

        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void unlock() {

        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }

    final class ReadWriteLockView implements ReadWriteLock{

        @Override
        public Lock readLock() {
            return null;
        }

        @Override
        public Lock writeLock() {
            return null;
        }
    }



    // Unsafe mechanics
    private static final Unsafe U;
    private static final long STATE;
    private static final long WHEAD;
    private static final long WTAIL;
    private static final long WNEXT;
    private static final long WSTATUS;
    private static final long WCOWAIT;
    private static final long PARKBLOCKER;

    static {
        try {
            U = UnSafeClass.getInstance();
            Class<?> k = KStampedlock.class;
            Class<?> wk = WNode.class;
            STATE = U.objectFieldOffset(k.getDeclaredField("state"));
            WHEAD = U.objectFieldOffset(k.getDeclaredField("whead"));
            WTAIL = U.objectFieldOffset(k.getDeclaredField("wtail"));
            WNEXT = U.objectFieldOffset(k.getDeclaredField("status"));
            WSTATUS = U.objectFieldOffset(k.getDeclaredField("next"));
            WCOWAIT = U.objectFieldOffset(k.getDeclaredField("cowait"));
            PARKBLOCKER = U.objectFieldOffset(k.getDeclaredField("parkBlocker"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }
}
