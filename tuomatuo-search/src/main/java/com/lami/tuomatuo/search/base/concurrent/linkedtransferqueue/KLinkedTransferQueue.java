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

    /**
     * Implements all queuing methods. See above for explanation
     *
     * @param e the item or null for take
     * @param haveData true if this is a put, else a take
     * @param how NOW, ASYNC, SYNC, or TIMED
     * @param nanos timeout in nanosecs, used only if mode is TIMED
     * @return an item if matched, else e
     * @throws NullPointerException if haveData mode but e is null
     */
    private E xfer(E e, boolean haveData, int how, long nanos){
        if(haveData && (e == null)){
            throw new NullPointerException();
        }
        Node s = null;

        return null;
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
}
