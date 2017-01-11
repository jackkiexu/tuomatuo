package com.lami.tuomatuo.search.base.concurrent.priorityblockingqueue;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrent 包中对象的序列化和反序列化方法, SynchronousQueue
 *
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
     * A plain PriorityQueue used only for serialization.
     * to maintain compatibility with previous versions
     * of this class. No-null udring serialization/deserialization
     */
    public KPriorityBlockingQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    /**
     * Creates a {@code PriorityBlockingQueue} with the default
     * initial capacity (11) that orders its elements according to
     * their {@linkplain Comparable natural ordering}
     *
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

    public KPriorityBlockingQueue(Collection<? extends E> c){
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        boolean heapify = true; // true if not known to be in heap order
        boolean screen = true; // true if must screen for nulls
        if(c instanceof SortedSet<?>){
            SortedSet<? extends E> ss = (SortedSet<? extends E>) c;
            this.comparator = (Comparator<? super E>)ss.comparator();
            heapify = false;
        }
        else if(c instanceof KPriorityBlockingQueue<?>){
            KPriorityBlockingQueue<? extends E> pq =
                    (KPriorityBlockingQueue<? extends E>)c;
            this.comparator = (Comparator<? super E>)pq.comparator;
            screen = false;
            if(pq.getClass() == KPriorityBlockingQueue.class){ // exact match
                heapify = false;
            }
        }

        Object[] a = c.toArray();
        int n = a.length;
        // If c.toArray incorrectly doesn't return Object[]. copy it.
        if(a.getClass() != Object[].class){
            a = Arrays.copyOf(a, n , Object[].class);
        }
        if(screen && (n == 1 || this.comparator != null)){
            for(int i = 0; i < n; ++i){
                if(a[i] == null)
                    throw new NullPointerException();
            }
        }

        this.queue = a;
        this.size = n;
        if(heapify){
            heapify();
        }
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return size;
        }finally {
            lock.lock();
        }
    }

    /**
     * Inserts the specified element into this priority queue
     * As the queue is unbounded, this method will never block
     *
     * @param e the element to add
     * @throws ClassCastException if the specified element cannot be compared
     *                              with elements currently in the priority
     *                              queue according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public void put(E e) throws InterruptedException {
        offer(e); // never need to block
    }

    /**
     * Inserts the specified element into this priority queue.
     * As the queue is unbounded, this method will never block or
     * return {@code false}
     *
     * @param e the element to add
     * @param timeout   This parameter is ignored as method never blocks
     * @param unit      This parameter is ignored as the method never blocks
     * @return          {@code true} (as specified by {@link BlockingQueue#offer(Object)})
     *
     * @throws ClassCastException if the specified element cannot be comparaed
     *                              with elements currently in the priority queue according to the
     *                              priority qeueu's ordering
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return offer(e); // never need to block
    }

    @Override
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try{
            while((result = dequeue()) == null){
                notEmpty.await();
            }
        }finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try{
            while((result = dequeue()) == null && nanos > 0){
                nanos = notEmpty.awaitNanos(nanos);
            }
        }finally {
            lock.unlock();
        }
        return result;
    }

    /**
     * Always returns {@code Integer.Max_VALUE} because
     * a {@code PriorityBlockingQueue} is not capacity constrained
     *
     * @return {@code Integer.MAX_VALUE} always
     */
    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    private int indexOf(Object o){
        if(o != null){
            Object[] array = queue;
            int n = size;
            for(int i = 0; i < n; i++){
                if(o.equals(array[i])){
                    return i;
                }
            }
        }
        return  -1;
    }

    /**
     * Remove a single instance of the specified element from this queue,
     * if it is present. More formally, removes an element {@code e} such
     * that {@code o.equal(e)}, if this queue contains one or more such
     * elements. Returns {@code true} if and onlu if this queue contained
     * the specified element (or equivalently, if this queue changed as
     * a result of the call)
     *
     * @param o element to removed from this queue, if present
     * @return {@code true} if this queue changed as a result of the call
     */
    public boolean remove(Object o){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            int i = indexOf(o);
            if(i == -1){
                return false;
            }
            removeAt(i);
            return true;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Returns {@code true} if this queue contains this specified element
     * More formally, returns {@code true} if and only if this queue contains
     * at least one element {@code e} such that {@code o.equal(e)}
     *
     * @param o object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     */
    public boolean contains(Object o){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return indexOf(o) != -1;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Returns an array containing all of the elements in this queue
     * The returned array elements are in no particular order.
     *
     * <p>
     *     The returned array will be "safe" in that no references to is are
     *     maintained by this queue. (In other words, this method must allocate
     *     a new array). The caller is thus free to modify the returned array
     * </p>
     *
     * <p>
     *     This method acts as bridge between array-based and collection-based
     * </p>
     *
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return Arrays.copyOf(queue, size);
        }finally {
            lock.unlock();
        }
    }

    public String toString() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = size;
            if (n == 0)
                return "[]";
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < n; ++i) {
                Object e = queue[i];
                sb.append(e == this ? "(this Collection)" : e);
                if (i != n - 1)
                    sb.append(',').append(' ');
            }
            return sb.append(']').toString();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if(c == null){
            throw new NullPointerException();
        }
        if(c == this){
            throw new IllegalArgumentException();
        }
        if(maxElements <= 0){
            return 0;
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            int n = Math.min(size, maxElements);
            for(int i = 0; i < n; i++){
                c.add((E)queue[0]); // In this order, in case add() throws
                dequeue();
            }
            return n;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Atomically removes all of the elements from this queue
     * The queue will be empty after this call returns
     */
    public void clear(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            Object[] array = queue;
            int n = size;
            size = 0;
            for(int i = 0; i < n; i++){
                array[i] = null;
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element into this priority queue
     * As the queue is unbounded, his method will never return {@code false}
     *
     * @param e the lement to add
     * @return {@code true} (as specified element cannot be compared
     *          with elements currently in the priority queue according to the
     *          priority queue's ordering)
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public boolean offer(E e) {
        if(e != null){
            throw new NullPointerException();
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        int n, cap;
        Object[] array;
        while((n = size) >= (cap = (array = queue).length)){
            tryGrow(array, cap);
        }

        try{
            Comparator<? super E> cmp = comparator;
            if(cmp == null){
                siftUpComparable(n, e, array);
            }else{
                siftUpUsingComparator(n, e, array, cmp);
            }
            size = n + 1;
            notEmpty.signal();
        }finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public E poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return (size == 0) ? null : (E)queue[0];
        }finally {
            lock.unlock();
        }
    }

    /**
     * Returns the comparator used to order the elements in this queue
     * or {@code null} if this queue uses the {@linkplain Comparable
     * natural ordering} of its elements
     *
     * @return the comparator used to order the elements in this queue
     * or {@code null} if this queue uses the natural ordering of its elements
     */
    public Comparator<? super E> comparator(){
        return comparator;
    }

    /**
     * Identity-based version for use in Itr.remove
     */
    void removeEQ(Object o){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            Object[] array = queue;
            for(int i = 0, n = size(); i < n; i++){
                if(o == array[i]){
                    removeAt(i);
                    break;
                }
            }
        }finally {
            lock.unlock();
        }
    }


    /**
     * Returns an array containing all of the elements in this queue; the
     * runtime type og the returned array is that of the specified array
     * The returned array elements are in no particular order.
     * The the queue fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this queue
     *
     * <p>
     *      If this queue fits in the specified array with room to spare
     *      (i.e, the array has more elements than this queue), the element in
     *      the array immediately following the end of the queue is set to
     *      {@code null}
     * </p>
     *
     * <p>
     *     Like the {@link #toArray} method, this method acts as bridge between
     *     array-based and collection-based APIs. Further, this method allows
     *     precise control over the runtime type of the output array, and may,
     *     under certain circumstances, be used to save allocation costs
     * </p>
     *
     * <p>
     *     
     * </p>
     *
     * @param a
     * @param <T>
     * @return
     */
    public <T> T[] toArray(T[] a){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            int n = size;
            if(a.length < n){
                // Make a new array of a's runtime type, but my contents
                return (T[])Arrays.copyOf(queue, size, a.getClass());
            }
            System.arraycopy(queue, 0, a, 0, n);
            if(a.length > n){
                a[n] = null;
            }
            return a;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Tries to grow array to accommodate at least one more lement
     * (but normally expend by about 50%), giving up (allowing retry)
     * on contention (which we expect to be race). Call only this while
     * holding lock
     *
     * @param array the heap array
     * @param oldCap    the length of the array
     */
    private void tryGrow(Object[] array, int oldCap){
        lock.unlock(); // must release and then re-acquire main lock
        Object[] newArray = null;
        if(allocationSpinLock == 0 &&
                unsafe.compareAndSwapInt(this, allocationSpinLockOffset, 0, 1)){
            try{
                int newCap = oldCap + ((oldCap < 64)?
                        (oldCap + 2): // grow faster if small
                        (oldCap >> 1)
                                        );
                if(newCap - MAX_ARRAY_SIZE > 0){ // possible overflow
                    int minCap = oldCap + 1;
                    if(minCap < 0 || minCap > MAX_ARRAY_SIZE){
                        throw new OutOfMemoryError();
                    }
                    newCap = MAX_ARRAY_SIZE;
                }
                if(newCap > oldCap && queue == array){
                    newArray = new Object[newCap];
                }
            }finally {
                allocationSpinLock = 0;
            }
        }

        if(newArray == null){ // back off if another thread is allocating
            Thread.yield();
        }
        lock.lock();
        if(newArray != null && queue == array){
            queue = newArray;
            System.arraycopy(array, 0, newArray, 0, oldCap);
        }
    }

    /**
     * Removes the ith element from queue
     * @param i
     */
    private void removeAt(int i){
        Object[] array = queue;
        int n = size - 1;
        if(n == i){ // remove last lement
            array[i] = null;
        }else{
            E moved = (E)array[n];
            array[n] = null;
            Comparator<? super E> cmp = comparator;
            if(cmp == null){
                siftDownComparable(i, moved, array, n);
            }else{
                siftDownUsingComparator(i, moved, array, n, cmp);
            }
            if(array[i] == moved){
                if(cmp == null){
                    siftUpComparable(i, moved, array);
                }else{
                    siftUpUsingComparator(i, moved, array, cmp);
                }
            }
        }
        size = n;
    }

    /**
     * Establishes the heap invariant (described above) in the entire tree
     * assuming nothing about the order of the elements prior to the call
     */
    private void heapify(){
        Object[] array = queue;
        int n = size;
        int half = (n >>> 1) -1;
        Comparator<? super E> cmp = comparator;
        if(cmp == null){
            for(int i = half; i >= 0; i--){
                siftDownComparable(i, (E)array[i], array, n);
            }
        }else{
            for(int i = half; i >= 0; i--){
                siftDownUsingComparator(i, (E)array[i], array, n, cmp);
            }
        }
    }

    private E dequeue(){
        int n = size - 1;
        if(n < 0){
            return null;
        }
        else{
            Object[] array = queue;
            E result = (E)array[0];
            E x = (E)array[n];
            array[n] = null;
            Comparator<? super E> cmp = comparator;
            if(cmp == null){
                siftDownComparable(0, x, array, n);
            }else{
                siftDownUsingComparator(0, x, array, n, cmp);
            }
            size = n;
            return result;
        }
    }

    /**
     * Insert item x at position k, maintaining heap invariant by
     * promoting x up the tree until it is greater than or equal to
     * its parent, or is the root
     *
     * To simplify and speed up coercions and comparisons. the
     * Comparable and Comparator versions are separated into different
     * method that are otherwise identical. (Similarly for siftDown)
     * These methods are statics, with heap state as arguments, to
     * simplify use in light og possible comparator exceptions
     *
     * @param k the position to fill
     * @param x the item to insert
     * @param array the heap array
     * @param <T>
     */
    private static <T> void siftUpComparable(int k, T x, Object[] array){
        Comparable<? super T> key = (Comparable<? super T>)x;
        while(k > 0){
            int parent = (k - 1) >>> 1;
            Object e = array[parent];
            if(key.compareTo((T)e) >= 0){
                break;
            }
            array[k] = e;
            k = parent;
        }
        array[k] = key;
    }

    private static <T> void siftUpUsingComparator(int k, T x, Object[] array, Comparator<? super T> cmp){
        while(k > 0){
            int parent = (k - 1) >>> 1;
            Object e = array[parent];
            if(cmp.compare(x, (T)e) >= 0){
                break;
            }
            array[k] = e;
            k = parent;
        }
        array[k] = x;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf
     *
     * @param k     the position to fill
     * @param x     the item to insert
     * @param array the heap array
     * @param n     the heap array
     * @param <T>
     */
    private static <T> void siftDownComparable(int k, T x, Object[] array, int n){
        if(n > 0){
            Comparable<? super T> key = (Comparable<? super T>)x;
            int half = n >>> 1;
            while(k < half){
                int child = (k << 1) + 1; // assume left child is least
                Object c = array[child];
                int right = child + 1;
                if(right < n &&
                        ((Comparable<? super T>)c).compareTo((T)array[right]) > 0
                        ){
                    c = array[child = right];
                }
                if(key.compareTo((T)c) <= 0){
                    break;
                }
                array[k] = c;
                k = child;
            }
            array[k] = key;
        }
    }

    private static <T> void siftDownUsingComparator(int k, T x, Object[] array,
                                                    int n,
                                                    Comparator<? super T> cmp
                                                    ){
        if(n > 0){
            int half = n >>> 1;
            while(k < half){
                int child = (k << 1) + 1;

            }
        }
    }


    final class Itr implements Iterator<E>{
        final Object[] array; // Array of all elements
        int cursor;           // index of next element to return
        int lastRet;          // index of last element, or -1 if no such

        public Itr(Object[] array) {
            lastRet = -1;
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return cursor < array.length;
        }

        @Override
        public E next() {
            if(cursor >= array.length){
                throw new NoSuchElementException();
            }
            lastRet = cursor;
            return (E)array[cursor++];
        }

        @Override
        public void remove() {
            if(lastRet < 0){
                throw new IllegalStateException();
            }
            removeEQ(array[lastRet]);
            lastRet = -1;
        }
    }

    // Unsafe mechanics
    private static final Unsafe unsafe;
    private static final long allocationSpinLockOffset;
    static {
        try{
         unsafe = UnSafeClass.getInstance();
            Class<?> k = KPriorityBlockingQueue.class;
            allocationSpinLockOffset = unsafe.objectFieldOffset(k.getDeclaredField("allocationSpinLock"));
        }catch(Exception e){
            throw new Error(e);
        }
    }
}
