package com.lami.tuomatuo.search.base.concurrent.stampedlock;

import com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock.KReentrantReadWriteLock;
import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * http://www.hpl.hp.com/techreports/2012/HPL-2012-68.pdf
 *
 * A capability-based lock with three modes for controlling read/write
 * access. The state of a StampedLock consists of a version and mode
 * Lock acquisition methods return a stamp that represents and
 * controls access with respect to a lock state: "try" versions of
 * these methods may instead return the special value zero to
 * represent failure to acquire access. Lock release and conversion
 * methods require stamps as arguments, and fail if they do not metch
 * the state of the lock. the three modes are:
 *
 * <ul>
 *     <li>
 *         Writing. Method {@link #writeLock} possibly blocks
 *         waiting for exclusive access, returning a stamp that can be used
 *         in method {@link #unlockWrite} to release the lock. Untimed and
 *         timed versions of {@code tryWriteLock} are also provided. When
 *         the lock is held in write mode, no read locks may be obtained,
 *         and all optimistic read validations will fail
 *     </li>
 *
 *     <li>
 *         Reading Method {@link #readLock} possibly blocks
 *         waiting for non-exclusive access, returning a stamp that can be
 *         used in method {@link #unlockRead} to release the lock. Untimed
 *         and timed versions of {@code tryReadLock} are also provided
 *     </li>
 *
 *     <li>
 *         Optimistic Reading Method {@link #tryOptimisticRead}
 *         returns a non-zero stamp only if the lock is not currently held
 *         in write mode. Method {@link #validate} returns true if the lock
 *         has not been acquired in write mode since obtaining a given
 *         stamp. This mode can be though of as an extremely weak version
 *         of a read-lock, that can be broken by a writer at any time. The use
 *         of optimistic mode for short read-only code segments often
 *         reduces contention and improves throughput. However, its use is
 *         inherently fragile. Optimistic read sections should only read
 *         fields and hold them in local variables for later use after
 *         validation. Fields read while in optimistic mode may be wildly
 *         inconsistent, so usage applies only when you are familiar enough
 *         with data representations to check consistancy and/or repeatedly
 *         invoke method {@code validate()}. For example, such steps are
 *         typically required when first reading an object or array
 *         reference, and then accessing one if its fields, elements or
 *         methods
 *     </li>
 * </ul>
 *
 * <p>
 *     This class also supports methods that conditionally provide
 *     conversions across the three modes. For example, method {@link
 *     #"tryConvertToWriteLock} attempts to "upgrade" a mode, returning
 *     a valid write stamp if (1) already in write mode (2) in reading
 *     mode and there are no other readers or (3) in optimistic mode and
 *     the lock is available. The forms of these methods are designed to
 *     help reduce some of the code bloat that otherwise occurs in
 *     retry-based designs
 * </p>
 *
 * <p>
 *     StampedLocks are designed for use as internal utilities in the
 *     development of thread-safe components. Their use relies on
 *     knowledge of the internal properties of the data, objects, and
 *     methods they are protecting. They are not reentrant, so locked bodies
 *     should not call other unknown methods that may try to
 *     re-acquire locks (although you may pass a stamp to other methods
 *     that can use or convert it). The use of read lock modes relies on
 *     the associated code sections being side-effect-free. Unvalidated
 *     optimistic read sections cannot call methods that are not known to
 *     tolarate potential inconsistencies. Stamp use finite
 *     representation, and are not cryptographically secure (i.e, a
 *     valid stamp may be guessable). Stamp values may recycle after (no
 *     sooner than) one year of continuous operation. A stamp held without
 *     use or validation for longer than this period may fail to va;idate
 *     correctly. StampedLocks are serializable, but always deserialize
 *     into initial unlocked state, so they are not useful for remote
 *     locking
 * </p>
 *
 * <p>
 *      The scheduling policy of StampedLock does not consistently
 *      prefer readers over writers or vice versa. All "try" methods are
 *      best-effort and do not necessarily conform to any scheduling or
 *      fairness policy. A zero return from any "try" method for acquiring
 *      or converting locks does not carry any information about the state
 *      of the lock: a subsequent invocation may succeed.
 * </p>
 *
 * <p>
 *     Because it support coordinated usage across multiple lock
 *     modes, this class does not directly implement the {@link Lock} or
 *     {@link ReadWriteLock} interfaces. Howevere, s StampedLock may be
 *     viewed {@link #asReadWriteLock()}, {@link #asReadWriteLock()}, or {@link
 *     #asReadWriteLock()} in application requiring only the associated
 *     set of functionality
 * </p>
 *
 * <p>
 *     <b>Sample Usage</b> The following illustrates some usage idioms
 *     in a class that maintains simple two-dimensional points. The sample
 *     code illustrates some try/catch conventions even though they are
 *     not strictly needed here because no exceptions can occur in their
 *     bodies.
 *
 *     class Point {
 *
 *         private double x, y;
 *         private final StampedLock sl = new StampedLock();
 *
 *         void move(double deltaX, double deltaY){ // an exclusively locked method
 *             long stamp = sl.writeLock();
 *             try{
 *                 x += deltaX;
 *                 y += deltaY;
 *             }finally{
 *                 sl.unlockWrite(stamp);
 *             }
 *         }
 *
 *         double distanceFromOrigin(){ // A read-only method
 *             long stamp = sl.tryOptimisticRead();
 *             double currentX = x, currentY = y;
 *             if(!s.validate(stamp)){
 *                 stamp = sl.readLock();
 *                 try{
 *                     currentX = x;
 *                     currentY = y;
 *                 }finally{
 *                     sl.unlockRead(stamp);
 *                 }
 *             }
 *             return Math.sqrt(currentX * currentX + currentY * currentY);
 *         }
 *
 *         void moveIfAtOrigin(double newX, double newY){ // upgrade
 *             // Could instead start with optimistic, not read mode
 *             long stamp = sl.readLock();
 *             try{
 *                 while(x == 0.0 && y == 0.0){
 *                     long ws = sl.tryConvertToWriteLock(stamp);
 *                     if(ws != 0L){
 *                         stamp = ws;
 *                         x = newX;
 *                         y = newY;
 *                         break;
 *                     }else{
 *                         sl.unlockRead(stamp);
 *                         stamp = sl.writeLock();
 *                     }
 *                 }
 *             }finally{
 *                 sl.unlock(stamp);
 *             }
 *         }
 *
 *     }
 *
 * </p>
 *
 * Created by xujiankang on 2017/2/10.
 */
public class KStampedlock implements Serializable {

    /**
     * Algorithmic notes:
     *
     * The design employs elements of Sequence locks
     * (as used in linux kernels: see Lameter's
     * http://www.lameter.com/galato2005.pdf
     * and elsewhere; see
     * Boehm's http://www.hpl.hp.com/techreports/2012/HPL-2012-68.html)
     * and Ordered RW locks (see Shirako et al
     * http:// dl.acm.org/citation.cfm?id=2312015)
     *
     * Conceptually, the primary state of the lock includes a sequence
     * number that is odd when write-locked and even otherwise
     * However, this is offset by a reader count that is non-zero when
     * read-locked. The read count is ignored when validating
     * "optimistic" seqlock-reader-style stamps. Because we must use
     * a small finite a number of bits (currently 7) for readers, a
     * supplementary reader overflow word is used when the number of
     * readers exceeds the count field. We do this by treating the max
     * reader exceeds the count field. We do this by treating the max
     * reader count value(RBITS) as spinlock protecting overflow
     * updates
     *
     * Waiters use a moddified form of CLH lock used in
     * AbstractQueuedSynchronizer (see its internal documentation for
     * a fuller account), where each node is tagged (field mode) as
     * either a reader or writer. Sets of waiting readers are grouped
     * (linked) under a common node (field cowait) so act as a single
     * node with respect to most CLH mechanics. By virtue of the
     * queue structure, wait nodes need not actually carry sequence
     * number; we known each is greater than its predecessor. This
     * simplifies the scheduling policy to a mainly-FIFO scheme that
     * incorporates elements of Phase-Fair locks(see Brandenburg &
     * Anderson, especially http://www.cs.unc.edu/~bbb/diss/). In
     * particular, we use the phase-fair anti-barging rule: if an
     * incoming reader arrives while read lock is held but there is a
     * queued writer, this incoming reader is queued. (this rule is
     * responsible for some of the complexity of method acquireRead.
     * but without it, the lock becomes hightly unfair) Method release
     * does not (and sometimes cannot) itself wake up cowaiters. This
     * is done by the primary thread, but helped by any other threads
     * with nothing better to do in methods acquireRead and
     * acquireWrite
     *
     * These rules apply to threads actually queued. All tryLock forms
     * opportunistically try to acquire locks regardless of preference
     * rules, and so may "barge" their way in. Randomized spining is
     * used in the acquire methods to reduce (increasingly expensive)
     * context switching while also acoiding sustained memory
     * thrashing among many threads. We limit spins to the head of
     * queue. A thread spin-waits up to SPINS times (where each
     * iteration deceases spin count with 50% probability) before
     * blocking. If. upon wakening it fails to obtain lock, and is
     * still (or become) the first waiting thread (which indicates
     * that some other thread barged and obtained lock). it escalates
     * spins (up to MAX_HEAD_SPINS) to reduce the linklihood of
     * continually losing to barging threads
     *
     * Nearly all of there mechanics are carried out in methods
     * acquireWrite and acquireRead, that, as typical of such code,
     * sprawl out because actions and retries rely on consistent sets
     * of locally cached reads
     *
     * As noted in Boehm's paper(above), sequence validation(mainly
     * method validate()) requires stricter ordering rules than apply
     * to normal valatile reads (of "state"). To force orderings of
     * reads before a validation and the validation itself in those
     * cases where this is not already forced we use
     * Unsafe loadFence
     *
     * The memory layout keeps lock state and queue pointers together
     * (normally on the same cache line). This usually works well for
     * read-mostly loads. In most other cases. the natural tendency of
     * adaptive-spin CLH locks to reduce memory contention lessens
     * motivation to further spread out contended locations, but might
     * be subject to future improvements
     */

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
     * Exclusively acquires the lock, blocking if necessary
     * until available
     *
     * @return a stamp that can be used to unlock or convert mode
     */
    public long writeLock(){
        long s, next; // bypass acquireWrite in fully unlocked case only
        return ((((s = state) & ABITS) == 0L &&
                U.compareAndSwapLong(this, STATE, s, next = s + WBIT)) ?
                next : acquireWrite(false, 0L));
    }

    /**
     * Exclusively acquires the lock if it is immediately available
     *
     * @return a stamp that can be used to unlock or convert mode
     *      or zero if the lock is not available
     */
    public long tryWriteLock(){
        long s, next;
        return ((((s = state) & ABITS) == 0L &&
                U.compareAndSwapLong(this, STATE, s, next = s + WBIT)) ?
                next : 0L);
    }


    /**
     * See above for explanation
     *
     * @param interruptible true if should check interrupts and if so
     *                      return INTERRUPTED
     * @param deadline if nonzero, the System.nanoTime value to timeout
     *                 at (and return zero)
     * @return next state, or INTERRUPTED
     */
    private long acquireWrite(boolean interruptible, long deadline){
        WNode node = null, p;
        for(int spins = -1;;){ // spin while enqueuing
            long m, s, ns;
            if((m = (s = state) & ABITS) == 0L){
                if(U.compareAndSwapLong(this, STATE, s, ns = s + WBIT)){
                    return ns;
                }
            }
            else if(spins < 0){
                spins = (m == WBIT && wtail == whead) ? SPINS : 0;
            }
            else if(spins > 0){
                if(LockSupport.nextSecondarySeed() >= 0){
                    --spins;
                }
            }
            else if((p = wtail) == null){ // initialize queue
                WNode hd = new WNode(WMODE, null);
                if(U.compareAndSwapObject(this, WHEAD, null, hd)){
                    wtail = hd;
                }
            }
            else if(node == null){
                node = new WNode(WMODE, p);
            }
            else if(node.prev != p){
                node.prev = p;
            }
            else if(U.compareAndSwapObject(this, WTAIL, p, node)){
                p.next = node;
                break;
            }
        }

        for(int spins = -1;;){
            WNode h, np, pp; int ps;
            if((h = whead) == p){
                if(spins < 0){
                    spins = HEAD_SPINS;
                }
                else if(spins < MAX_HEAD_SPINS){
                    spins <<= 1;
                }

                for(int k = spins;;){ // spin at head
                    long s, ns;
                    if(((s = state) & ABITS) == 0L){
                        if(U.compareAndSwapLong(this, STATE, s, ns = s + WBIT)){
                            whead = node;
                            node.prev = null;
                            return ns;
                        }
                    }
                    else if(LockSupport.nextSecondarySeed() >= 0 && --k <= 0){
                        break;
                    }
                }
            }
            else if(h != null){ // help release stale waiters
                WNode c; Thread w;
                while((c = h.cowait) != null){
                    if(U.compareAndSwapObject(h, WCOWAIT, c, c.cowait) && ((w = c.thread) != null)){
                        U.unpark(w);
                    }
                }
            }

            if(whead == h){
                if((np = node.prev) != p){
                    if(np != null){
                        if(){

                        }
                    }
                }
            }
        }
        return 0l;
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
