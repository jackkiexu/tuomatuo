package com.lami.tuomatuo.search.base.concurrent.linkedtransferqueue;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by xujiankang on 2017/2/7.
 */
public class KLinkedTransferQueue<E> extends AbstractQueue<E> implements TransferQueue<E>, Serializable {

    private static final Logger logger = Logger.getLogger(KLinkedTransferQueue.class);

    private static final long serialVersionUID = -3223113410248163686L;

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

//    private E await



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
