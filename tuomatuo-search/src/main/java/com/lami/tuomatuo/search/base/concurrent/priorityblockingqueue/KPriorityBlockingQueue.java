package com.lami.tuomatuo.search.base.concurrent.priorityblockingqueue;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrent 包中对象的序列化和反序列化方法, SynchronousQueue
 * An unbounded {@linkplain BlockingQueue blocking queue} that uses
 * the same ordering rules as class {@link java.util.PriorityQueue} and supplies
 * blocking retrieval operation. While this queue is logically
 * unbounded, attempted additions may fail due to resource exhaustion(耗竭)
 * (causing {@code OutOfMemoryError}). This class does not permit
 * {@code null} elements. A priority queue relying on {@linkplain
 * Comparable natural ordering} also does not permit insertion of
 * non-comparable objects (doing so results in {@code ClassCastException})
 *
 * <p>
 *     This class and its iterator implement all of the <em>optional</em>
 *     methods of the {@link Collection} and {@link #iterator()} is <em>not</em>
 *     guraranteed to traverse the elements of the PriorityBlocking in any particular order
 *     If you need ordered traversal, consider using
 *     {@code Arrays.sort(pq.toArray()}, Also method {@code drainTo}
 *     can be used to <em>remove</em> some or all elements in priority
 *     order and place them in another collection
 * </p>
 *
 * <p>
 *     Operations on this class make no gurarantees abput the ordering
 *     of elements with equal priority. If you need to enforce an
 *     ordering, you can define custom classes or comparators that use a secondary
 *     key to break ties in primary priority values, For
 *     example, here is a class that applies first-in-first-out
 *     tie-breaking to comparable elements. To use it, you would insert a
 *     {@code new FIFOEntry(anEntry)} instead of a plain entry object
 * </p>
 *
 * <p>
 *      {@code
 *      class FIFOEntry<E extends Comparable<? super E>> implements Comparable<FIFOEntry<E>>{
 *          static final AtomicLong seq = new AtomicLong(0);
 *          final long seqNum;
 *          final E entry;
 *          public FIFOEntry(E entry){
 *              seqNum = seq.getAndIncrement();
 *              this.entry = entry;
 *          }
 *
 *          public E getEntry(){
 *              return entry;
 *          }
 *
 *          public int compareTo(FIFOEntry<E> other){
 *              int res = entry.compareTi(other.entry);
 *              if(res = 0 && other.entry != this.entry){
 *                  res = (seqNum < other.seqNum? -1 : 1);
 *              }
 *              return res;
 *          }
 *      }
 * </p>
 *
 * <p> This class is a member of the
 *  <a href="{@docRoot}/../technotes/guides/collections/index.html"> Java Collection Framework </a>
 *
 *  Created by xujiankang on 2016/12/29.
 */
public class KPriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, java.io.Serializable {

    private static final long serialVersionUID = 5595510919245408276L;

    /**
     * The implementation uses an array-based binary heap, with public
     * operations protected with s single lock. However, allocation
     * during resizing uses a simple spinlock (used only while not
     * holding main lock) in order to allow takes to operate
     * concurrently with allocation. This avoids repeated
     * postponement of waiting consumers and consequent(随后) element
     * build-up. The need to back away from lock during allocation
     * makes it impossible to simply wrap delegated
     * java.util.PriorityQueue operation within a lock , as was done
     * in a previous version of this class. To maintain
     * interoperability, a plain PriorityQueue is still used during
     * serialization, which maintains compatibility at the expense of
     * transiently doubling overhead
     *
     */

    /** Default array capacity */
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    /**
     * The maximum size of array to allocate
     * Some VMs reserve some header words in an array
     * Attempts to allocate larger arrays may result in
     * OutOgMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*(n+1)]. The
     * priority queue is ordered by comparater. or by the elements'
     * natural ordering, if comparator is null: For each node n in the
     * heap and each descendant d of n, n <= d. The element with the
     * lowest value is in queue[0], assuming the queue is nonempty
     */
    private transient Object[] queue;

    /** The number of elements in the priority queue */
    private transient int size;

    /**
     * The comparator, or null if priority queue uses elements'
     * natural ordering
     */
    private transient Comparator<? super E> comparator;

    /**
     * Lock used for all public operation
     */
    private final ReentrantLock lock;

    /**
     * Condition for blocking when empty
     */
    private final Condition notEmpty;

    /**
     * Spinlock for allocation, acquired via CAS
     */
    private transient volatile int allocationSpinLock;

    /**
     * A plain PriorityQueue used only for serialization,
     * to maintain compatibility with previous versions
     * of this class, Non-null only during serialization/deserialization
     */
    private PriorityQueue<E> q;

    /**
     * Creates 
     * @param initialCapacity
     */
    public KPriorityBlockingQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * Creates a {@code PriorityBlockingQueue} with the specified initial
     * capacity that orders its elements according to the specified
     * comparator
     *
     * @param initialCapacity the initial capacity for this priority queue
     * @param comparator the comparator that will be used to order this
     *                   priority queue, If {@code null}, the {@linkplain Comparable
     *                   natural ordering} of the elements will be used
     * @throws IllegalArgumentException if{@code initialCapacity} is less than 1
     */
    public KPriorityBlockingQueue(int initialCapacity, Comparator<? super E> comparator) {
        if(initialCapacity < 1){
            throw new IllegalArgumentException();
        }
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.comparator = comparator;
        this.queue = new Object[initialCapacity];
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
}
