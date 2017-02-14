package com.lami.tuomatuo.search.base.concurrent.linkedtransferqueue;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * Created by xujiankang on 2017/2/7.
 */
public class KLinkedTransferQueue<E> extends AbstractQueue<E> implements TransferQueue<E>, Serializable {

    private static final Logger logger = Logger.getLogger(KLinkedTransferQueue.class);

    private static final long serialVersionUID = -3223113410248163686L;

    /** True if on multiprocessor */
    private static final boolean MP = Runtime.getRuntime().availableProcessors() > 1;

    /**
     * The number of times to spin (with randomly interspersed calls
     * or Thread yield) on multiprocessor blocking when a node
     * is apparently the first waiter in the queue. See above for
     * explanation. Must be a power of two. The value is empirically
     * derived -- it works pretty well across a variety of processors
     * derived -- it works pretty well across a variety of processors,
     * number of CPU, and OSes.
     */
    private static final int FRONT_SPINS = 1 << 7;

    /**
     * The number of time to spin before blocking when a node is
     * preceded by another node that is apparently spinning. Also
     * servers as an increment to FRONT_SPINS on phase changes, and as
     * base average frquency for yielding during spins. Must be a
     * power of two
     */
    private static final int CHAINED_SPINS = FRONT_SPINS >>> 1;

    /**
     * The maximum number of estimated removal failure (sweepVotes)
     * to tolerate before sweeping through the queue unlinking
     * cancelled nodes that were not unlinked upon initial
     * removal. See above for explanation. The value must be at least
     * two to avoid useless sweeps when removing trailing nodes
     */
    static final int SWEEP_THRESHOLD = 32;



    /**
     * Creates an initially empty {@code KLinkedTransferQueue}
     */
    public KLinkedTransferQueue() {
    }

    /**
     * Creates a {@code KLinkedTransferQueue}
     * initially containing the lements of the given collection
     * added in traversal order of the collection's iterator
     *
     * @param c the collection of elements to initially contain
     * @throws NullPointerException if the specified collection or any
     *          of its element are null
     */
    public KLinkedTransferQueue(Collection<? extends E> c) {
        this();
        addAll(c);
    }


    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean tryTransfer(E e) {
        return false;
    }

    @Override
    public void transfer(E e) throws InterruptedException {

    }

    @Override
    public boolean tryTransfer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public boolean hasWaitingConsumer() {
        return false;
    }

    @Override
    public int getWaitingConsumerCount() {
        return 0;
    }

    @Override
    public void put(E e) throws InterruptedException {

    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public E take() throws InterruptedException {
        return null;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }

    @Override
    public boolean offer(E e) {
        return false;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }


    final class Itr implements Iterator<E> {
        private Node nextNode;      // next node to return item for
        private E nextItem;         // the corresponding item
        private Node lastRet;       // last returned node, to support remove
        private Node lastPred;      // predecessor to unlonk lastRet

        private void advance(Node prev){
            /**
             * To track and avoid buidup of deleted nodes in the face北京-喜(xiaoxi0324@126.com);
             * of calls to both Queue.remove and Itr remove, we must
             * include variants of unsplice and sweep upon each
             * advance: Upon Itr.remove. we may need to catch up links
             * from lastPred, and upon other removes, we might need to
             * skip ahead from stale nodes and unsplice deteled nodes
             * found while advancing
             */

            Node r, b; // reset lastpred upon possible deleteion of lastRet
            if((r = lastRet) != null && !r.isMatched()){
                lastPred = r;   // next lastPred is old lastRet
            }
            else if ((b = lastPred) == null || b.isMatched()){
                lastPred = null; // at start of list
            }
            else{
                Node s, n; // help with removal of lastPred next
                while((s = b.next) != null &&
                        s != b && s.isMatched() &&
                        (n = s.next) != null && n != s){
                    b.casNext(s, n);
                }
            }
            this.lastRet = prev;

            for(Node p = prev, s, n;;){
                s = (p == null) ? head : p.next;
                if(s == null){
                    break;
                }
                else if(s == p){
                    p = null;
                    continue;
                }
                Object item = s.item;
                if(s.isData){
                    if(item != null && item != s){
                        nextItem = KLinkedTransferQueue.<E>cast(item);
                        nextNode = s;
                        return ;
                    }
                }
                else if(item == null){
                    break;
                }
                // assert s.isMatached();
                if(p == null){
                    p = s;
                }
                else if((n = s.next) == null){
                    break;
                }
                else if(s == null){
                    p = null;
                }
                else{
                    p.casNext(s, n);
                }
            }
            nextNode = null;
            nextItem = null;
        }

        Itr() { advance(null);}


        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public E next() {
            Node p = nextNode;
            if(p == null) throw new NoSuchElementException();
            E e = nextItem;
            advance(p);
            return e;
        }

        public final void remove(){
            final Node lastRet = this.lastRet;
            if(lastRet == null){
                throw new IllegalStateException();
            }
            this.lastRet = null;
            if(lastRet.tryMatchData()){
                unsplice(lastPred, lastRet);
            }
        }
    }

    /** A customized variant of Spliterators IteratorSpliterator */
    static final class LTQSpliterator<E> implements Spliterator<E>{

        @Override
        public boolean tryAdvance(Consumer<? super E> action) {
            return false;
        }

        @Override
        public Spliterator<E> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }
    }


    /**
     * Queue nodes. Uses Object, not E, for items to allow forgetting
     * them after use. Relies heavily on Unsafe mechanics to minimize
     * unnecessary ordering constraints: Writes that are intrinsically
     * ordered wrt other accessed or CASes use simple relaxed forms
     */
    static final class Node {

        private static final long serialVersionUID = -3375979862319811754L;

        final boolean isData;   // false if this is a request node
        volatile Object item;    // initially non-null if isData, CASed to match
        volatile Node next;
        volatile Thread waiter; // null until waiting

        // CAS methods for fields
        final boolean casNext(Node cmp, Node val){
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        final boolean casItem(Object cmp, Object val){
            // assert cmp == null || cmp.getClass() != Node.class
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        /**
         * Constructs a new node. Uses relaxed write because item can
         * only be seen after publication via casNext
         * @param isData
         * @param item
         */
        public Node(boolean isData, Object item) {
            this.isData = isData;
            UNSAFE.putObject(this, itemOffset, item); // relaxed write
        }

        /**
         * Links node to itself to avoid garbage retention. Called
         * only after CASing head field, so uses relaxed write
         */
        final void forgetNext(){
            UNSAFE.putObject(this, nextOffset, this);
        }


        /**
         * Sets item to self and waiter to null, to avoid garbage
         * retention after matching or cancelling, Uses relaxed writes
         * because order is already constrained in the only calling
         * contexts; item is forgetten only after volatile.atomic
         * mechanics that extract items. Similarly, clearing waiter
         * follow either CAS return from park (if ever parked
         * else we don't care)
         */
        final void forgetContents(){
            UNSAFE.putObject(this, itemOffset, this);
            UNSAFE.putObject(this, waiterOffset, null);
        }

        /**
         * Returns true if this node has been matched, including the
         * case of artificial matches due to cancellation
         */
        final boolean isMatched(){
            Object x = item;
            return (x == this) || ((x == null) == isData);
        }

        final boolean isUnmatchedRequest(){
            return !isData && item == null;
        }

        /**
         * Returns true if a node with the given mode cannot be
         * appended to this node because this node is unmatched and
         * has opposite data mode
         */
        final boolean cannotPrecede(boolean haveData){
            boolean d = isData;
            Object x;
            return d != haveData && (x = item) != this && (x != null) == d;
        }

        /**
         * Tries to artificially match a data node -- used by remove
         */
        final boolean tryMatchData(){
            // assert isData
            Object x = item;
            if(x != null && x != this && casItem(x, null)){
                LockSupport.unpark(waiter);
                return true;
            }
            return false;
        }


        // Unsafe mechanics
        private static final Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;
        private static final long waiterOffset;

        static {
            try{
                UNSAFE = UnSafeClass.getInstance();
                Class<?> k = Node.class;
                itemOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
                waiterOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("waiter"));
            }catch (Exception e){
                throw new Error(e);
            }
        }
    }

    /** head of the queue; null until first enqueue */
    transient volatile Node head;

    /** tail of the queue: null until first append  */
    private transient volatile Node tail;

    /** The number of apparent failure to unsplice removed nodes */
    private transient volatile int sweepVotes;

    // CAS methods for field
    private boolean casTail(Node cmp, Node val){
        return unsafe.compareAndSwapObject(this, tailOffset, cmp, val);
    }

    private boolean casHead(Node cmp, Node val){
        return unsafe.compareAndSwapObject(this, headOffset, cmp, val);
    }

    private boolean casSweepVotes(int cmp, int val){
        return unsafe.compareAndSwapInt(this, sweepVoteOffset, cmp, val);
    }


    private static final int NOW    = 0; // for untimed poll, tryTransfer
    private static final int ASYNC  = 1; // for offer, put, add
    private static final int SYNC   = 2; // for transfer, take
    private static final int TIMED  = 3; // for timed poll, tryTransfer

    static <E> E cast(Object item){
        // assert item == null || item.getClass() != Node class
        return (E)item;
    }

    /**
     * Implements all queuing methods See above for explanation
     *
     * @param e             the item or null for take
     * @param haveData     true if this is a put, else a take
     * @param how           NOW, ASYNC, SYNC, or TIMED
     * @param nanos         timeout in nanoses, used only if mode is TIMED
     * @return              an item if matched, else e
     */
    private E xfer(E e, boolean haveData, int how, long nanos){
        if(haveData && (e == null)){
            throw new NullPointerException();
        }
        Node s = null;

        return null;
    }


    /**
     * Tries to append node s as tail
     *
     * @param s the node to append
     * @param haveData true if appending in data mode
     * @return  null on failure due to losing race with append in
     *          different mode, else's predecessor, or s itself if no predecessor
     */
    private Node tryAppend(Node s, boolean haveData){
        for(Node t = tail, p = t;;){        // move p to last node and append
            Node n, u;                       // temps for reads of next & tail
            if(p == null && (p = head) == null){
                if(casHead(null, s)){
                    return s;               // initialize
                }
            }
            else if(p.cannotPrecede(haveData)){
                return null;                // lost race vs opposite mode
            }
            else if((n = p.next) != null){ // not last: keep traversing
                p = p != t && t != (u = tail)?(t = u): // stale tail
                        (p != null)? n : null; // restart if off list
            }
            else if(!p.casNext(null, s)){
                p = p.next;                 // re-read on CAS failure
            }else{
                if(p != t){
                    while((tail != t || !casTail(t, s)) &&
                            (t = tail)      != null &&
                            (s = t.next)    != null && // advance and retry
                            (s = s.next) != null && s != t
                            ){

                    }
                }
                return p;
            }
        }
    }

    /**
     * Spins/yeilds/blocks until node s is matched or caller gives up
     *
     * @param s the waiting node
     * @param pred the predecessor of s, or s itself if it has no
     *             predecessor, or null if uknown (the null case does not occur
     *             in any current calls but may in possible future extension)
     * @param e the comparison value for checking match
     * @param timed if true, wait only until timeout elapses
     * @param nanos timeout in nanosecs, used only if timed is true
     * @return matched item, or e if unmatched on interrupt or timeout
     */
    private E awaitMatch(Node s, Node pred, E e, boolean timed, long nanos){
        final long deadline = timed? System.nanoTime() + nanos : 0L;
        Thread w = Thread.currentThread();
        int spins = -1; // initialized after first item and cancel checks
        ThreadLocalRandom randomYields = null; // bound if needed

        for(;;){
            Object item = s.item;
            if(item != e){      // matched
                // assert item != s
                s.forgetContents();     // avoid garbage
                return KLinkedTransferQueue.<E>cast(item);
            }

            if((w.isInterrupted() || (timed && nanos <= 0)) &&
                    s.casItem(e, s)){ // cancel
                unsplice(pred, s);
                return e;
            }

            if(spins < 0){  // establish spins at/near front
                if((spins = spinsFor(pred, s.isData)) > 0){
                    randomYields = ThreadLocalRandom.current();
                }
            }
            else if(spins > 0){
                --spins;
                if(randomYields.nextInt(CHAINED_SPINS) == 0){
                    Thread.yield(); // occasionally yield
                }
            }
            else if(s.waiter == null){
                s.waiter = w; // request unpark then recheck
            }
            else if(timed){
                nanos = deadline - System.nanoTime();
                if(nanos > 0L){
                    LockSupport.parkNanos(this, nanos);
                }
            }
            else {
                LockSupport.park(this);
            }
        }
    }


    /**
     * Returns spin/tield value for a node with given predecessor and
     * data mode. See above for explanation.
     */
    private static int spinsFor(Node pred, boolean haveData){
        if(MP && pred != null){
            if(pred.isData != haveData){        // phase change
                return FRONT_SPINS + CHAINED_SPINS;
            }
            if(pred.isMatched()){               // probably at front
                return FRONT_SPINS;
            }
            if(pred.waiter == null){          // pred apparently spining
                return CHAINED_SPINS;
            }
        }
        return 0;
    }


    /************************* Traversal methods ***************************/

    /**
     * Returns the successor of p, or the head node if p.next has been
     * linked to self, which will only be true if traversing with a
     * stale pointer that is now off the list
     */
    final Node succ(Node p){
        Node next = p.next;
        return (p == next) ? head : next;
    }

    /**
     * Returns the first unmateched node of the given mode, or null if
     * none. Used by methods isEmpty, hasWaitingConsumer
     */
    private Node firstOfMode(boolean isData){
        for(Node p = head; p != null; p = succ(p)){
            if(!p.isMatched()){
                return (p.isData == isData)? p : null;
            }
        }
        return null;
    }

    /**
     * Version of firstOfMode used by Spliterator. Callers must
     * recheck if the returned node's item field is null or
     * self-linked before using
     * @return
     */
    final Node firstDataNode(){
        for(Node p = head; p != null;){
            Object item = p.item;
            if(p.isData){
                if(item != null && item != p){
                    return p;
                }
            }
            else if(item == null){
                break;
            }

            if(p == (p = p.next)){
                p = head;
            }
        }
        return  null;
    }

    /**
     * Returns the item in the first unmatched node with isData; or
     * null if none. Used by peek
     */
    private E firstDataItem(){
       for(Node p = head; p != null; p = succ(p)){
           Object item = p.item;
           if(p.isData){
               if(item != null && item != p){
                   return KLinkedTransferQueue.<E>cast(item);
               }
           }
           else if(item == null){
               return null;
           }
       }
        return null;
    }

    /**
     * Traverses and counts unmatched nodes of the given mode.
     * Used by method size and getWaitingConsumerCount
     */
    private int countOfMode(boolean data){
        return 0;
    }


    /**************** Removal methods  *****************************/

    private void sweep(){

    }


    /**
     * Unsplices (now or later) the given deleted/cancelled node with
     * the given predecessor
     *
     * @param pred a node that was at one time known to be the
     *             predecessor of s, or null or s itself if s is/was at head
     * @param s the node t obe unspliced
     */
    final void unsplice(Node pred, Node s){
        s.forgetContents(); // forget unneeded fields

        /**
         * See above for rationale. Briefly: if pred still points to
         * s, try to unlink s. if cannot be unlinked, because it is
         * tailing node or pred might be unlinked, and neither pred
         * nor are head or offlist, add to sweepVotes, and if enough
         * votes have accumulated, sweep.
         */
        if(pred != null && pred != s && pred.next == s){
            Node n = s.next;
            if(n == null ||
                    (n != s && pred.casNext(s, n) && pred.isMatched())){
                for(;;){ // check if at, or could be, head
                    Node h = head;
                    if(h == pred || h == s || h == null){
                        return;         // at head or list empty
                    }
                    if(!h.isMatched()){
                        break;
                    }
                    Node hn = h.next;
                    if(hn == null){
                        return;     // now empty
                    }
                    if(hn != h && casHead(h, hn)){
                        h.forgetNext(); // advance head
                    }
                }

                if(pred.next != pred && s.next != s){ // recheck if offlist
                    for(;;){
                        int v = sweepVotes;
                        if(v < SWEEP_THRESHOLD){
                            if(casSweepVotes(v, v + 1)){
                                break;
                            }
                        }
                        else if(casSweepVotes(v, 0)){
                            sweep();
                            break;
                        }
                    }
                }
            }
        }
    }

    // Unsafe mechanics
    private static final Unsafe unsafe;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long sweepVoteOffset;

    static {
        try{
            unsafe = UnSafeClass.getInstance();
            Class<?> k = KLinkedTransferQueue.class;
            headOffset = unsafe.objectFieldOffset(k.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(k.getDeclaredField("tail"));
            sweepVoteOffset = unsafe.objectFieldOffset(k.getDeclaredField("sweepVotes"));
        }catch (Exception e){
            throw new Error(e);
        }
    }
}
