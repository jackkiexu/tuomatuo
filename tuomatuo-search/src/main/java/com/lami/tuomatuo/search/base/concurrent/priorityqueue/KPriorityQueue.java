package com.lami.tuomatuo.search.base.concurrent.priorityqueue;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * http://wlh0706-163-com.iteye.com/blog/1850125
 * http://dl2.iteye.com/upload/attachment/0083/3873/65864977-3509-36bb-8b5f-eb573c7aecdc.png
 *
 * An unbounded priority {@linkplain Queue queue} based on a priority heap
 * The elements of the priority queue are ordered according to their
 * {@linkplain Comparable natural ordering}, or by a {@link java.util.Comparator}
 * provided at queue construction time, depending on which constructor is
 * used. A priority queue does not permit {@code null} elements.
 * A priority queue relying on natural also does not permit
 * insertion of non-comparable objects (doing so many result in
 * {@code ClassCastException})
 *
 * <p>
 *     The <em>head</em> of this queue is the <em>least</em> element
 *     with respect to the specified ordering. If multiple elements are
 *     tied for least value, the head is one of those elements -- ties are
 *     broken arbitrarily. The queue retrieval operations {@code poll},
 *     {@code remove}, {@code peek}, and {@code element} access the
 *     element at the head of the queue.
 * </p>
 *
 * <p>
 *     A priority queue is unbounded, but has an internal
 *     <i>capacity</i> governing the size of an array used to store the
 *     elements on the queue. It is always at least as large as the queue
 *     size. As elements are added to a priority queue. its capacity
 *     grows automatically. The details of the growth policy are not
 *     specified
 * </p>
 *
 * <p>
 *     This class and its iterator implement all of the
 *     <em>optional</em> methods of the {@link java.util.Collection} and {@link
 *     Iterator} interfaces. The Iterator provided n method {@link
 *     #iterator()} is <em>not</em> guaranteed to traverse the elements of
 *     the priority queue in any particular order. If you need ordered
 *     traversal, consider using {@code Arrays.sort(pq.toArray())}.
 * </p>
 *
 * <p>
 *     <strong>
 *         Note that this implementation is not synchronized.
 *     </strong>
 *     Multiple threads should not access a {@code PriorityQueue}
 *     instance concurrently if any of the threads modifies the queue.
 *     Instead, use the thread-safe {@link java.util.concurrent.PriorityBlockingQueue} class
 * </p>
 *
 * <p>
 *     Implementation note: this implementation provides
 *     O(log(n)) time for enqueuing and dequeuing methods
 *     {@code offer}, {@code poll}, {@code remove} and {@code add};
 *     linear time for the {@code remove} and {@code contains}
 *     methods; and constant time for the retrieval methods
 *     {@code peek}, {@code element}, and {@code size()}
 * </p>
 *
 * <p>
 *     This class is a member of the
 *     <a href="{@docRoot}/../technotes/guides/collections/index.html">
 *         Java Collections Framework
 *     </a>
 * </p>
 *
 * Created by xjk on 1/2/17.
 */
public class KPriorityQueue<E> extends AbstractQueue<E> implements java.io.Serializable {

    private static final long serialVersionUID = 7190392904493326145L;

    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    /**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*(n+1)]. The
     * priority queue is ordered by comparator, or by the elements'
     * natural ordering, if comparator is null: For each node n in the
     * heap and each descendant d of n, n <= d. The element with the
     * lowest value is in queue[0], assuming the queue is nonempty.
     */
    transient Object[] queue; // non-private to simplify nested class access

    /**
     * The number of elements in the priority queue
     */
    private int size = 0;

    /**
     * The comparator, or null if priority queue uses elements'
     * natural ordering
     */
    private final Comparator<? super E> comparator;

    /**
     * The number of times this priority queue has been
     * <i>structurally modified</i>. See
     */
    transient int modCount = 0; // non-private to simplify nested class access

    /**
     * Creates a {@code PriorityQueue} with the default initial
     * capacity(11) that orders its elements according to their
     * {@linkplain Comparable natural ordering}
     */
    public KPriorityQueue(){
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    /**
     * Creates a {@code PriorityQueue} with the default  initial
     * capacity (11) that orders its elements according to their
     * {@linkplain Comparable natural ordering}
     *
     * @param initialCapacity
     */
    public KPriorityQueue(int initialCapacity){
        this(initialCapacity, null);
    }

    /**
     * Creates a {@code PriorityQueue} with specified initial capacity
     * that orders its elements according to the specified comparator
     *
     * @param initialCapacity the initial capacity for this priority queue
     * @param comparator the comparator that will be used to the order this
     *                   priority queue. If {@code null}, the {@linkplain Comparable
     *                   natural ordering} of the elements will be used
     *
     */
    public KPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        // Note: This restriction at least one is not actually needed,
        // but continues for 1.5 compatibility
        if(initialCapacity < 1){
            throw new IllegalArgumentException();
        }
        this.queue = new Object[initialCapacity];
        this.comparator = comparator;
    }

    public KPriorityQueue(Collection<? extends E> c){
        if(c instanceof SortedSet<?>){
            SortedSet<? extends E> ss = (SortedSet<? extends E>)c;
            this.comparator = (Comparator<? super E>)ss.comparator();
            initElementsFromCollection(ss);
        }
        else if(c instanceof KPriorityQueue<?>){
            KPriorityQueue<? extends E> pq = (KPriorityQueue<? extends E>)c;
            this.comparator = (Comparator<? super E>)pq.comparator();
            initFromPriorityQueue(pq);
        }
        else{
            this.comparator = null;
            initFromCollection(c);
        }
    }

    /**
     * Return the comparator used to order the elements in this
     * queue, or {@code null} if this queue is sorted according to
     * the {@link Comparable natural ordering} of its elements
     *
     * @return the comparator used to order this queue, or
     *          {@code null} if thie queue is sorted according to the
     *          natural ordering of its elements
     */
    public Comparator<? super E> comparator(){
        return comparator;
    }

    private void initFromPriorityQueue(KPriorityQueue<? extends E> c){
        if(c.getClass() == KPriorityQueue.class){
            this.queue = c.toArray();
            this.size = c.size();
        }else{
            initFromCollection(c);
        }
    }

    private void initElementsFromCollection(Collection<? extends E> c){
        Object[] a = c.toArray();
        // if c.toArray incorrectly doesn't return Object[], copy it
        if(a.getClass() != Object[].class){
            a = Arrays.copyOf(a, a.length, Object[].class);
        }
        int len = a.length;
        if(len == 1 || this.comparator != null){
            for(int i = 0; i < len; i++){
                if(a[i] == null){
                    throw new NullPointerException();
                }
            }
        }
        this.queue = a;
        this.size = a.length;
    }

    /**
     * Initializes queue array with elements from the given Collection
     * @param c
     */
    private void initFromCollection(Collection<? extends E> c){
        initElementsFromCollection(c);
        heapify();
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE;

    /**
     * Increses the capacity of the array
     *
     * @param minCapacity
     */
    private void grow(int minCapacity){
        int oldCapacity = queue.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ? (oldCapacity = 2): (oldCapacity >> 1)  );

        // overflow-conscious code
        if(newCapacity - MAX_ARRAY_SIZE > 0){
            newCapacity = hugeCapacity(minCapacity);
        }
        queue = Arrays.copyOf(queue, newCapacity);
    }

    private static int hugeCapacity(int minCapacity){
        if(minCapacity < 0){ // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    /**
     * Establish the heap invariant (described above) in the entire tree,
     * assuming nothing about the order of the elements priori to the call
     */
    public void heapify(){
        /** 构建最大堆
         *  int i = (size >>> 1) - 1; 获取最后一个节点的parent节点在 array 上的下标
         *  从数组的最后一个节点开始, 将对应的堆进行排序, 最大的进行 swap 放在 parent 上
         *  注意: 这里从底层的堆开始进行堆调整, 从下往上层进行调整, 而每一层又是将比较的结果值放在对应的 parent 上
         */
        for(int i = (size >>> 1) - 1; i >= 0; i--){
            siftDown(i, (E)queue[i]);
        }
    }
    /**
     * Creates a {@code PriorityQueue} with the default  initial
     * capacity (11) that orders its elements according to their
     * @param comparator
     */
    public KPriorityQueue(Comparator<? super E> comparator){
        this(DEFAULT_INITIAL_CAPACITY, comparator);
    }

    /**
     * Inserts the specified element into priority queue.
     *
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws ClassCastException if the specified element cannot be
     *          compared with element currently in this priority queue
     *          according to the priority queue's ordering
     * @throws NullPointerException if the element is null
     */
    public boolean add(E e){
        return offer(e);
    }

    /**
     * Inserts the specified element into priority queue.
     *
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws ClassCastException if the specified element cannot be
     *          compared with element currently in this priority queue
     *          according to the priority queue's ordering
     * @throws NullPointerException if the element is null
     */
    @Override
    public boolean offer(E e) {
        if(e == null){
            throw new NullPointerException();
        }
        modCount++;
        int i = size;
        if(i >= queue.length){
            grow(i + 1);
        }
        size = i + 1;
        if(i == 0){
            queue[0] = e;
        }else{
            siftUp(i, e);
        }
        return true;
    }


    @Override
    public E peek() {
        return ((size == 0)? null : (E)queue[0]);
    }

    private int indexOf(Object o){
        if(o != null){
            for(int i = 0; i < size; i++){
                if(o.equals(queue[i])){
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Removes a single instance of the specified element from thie queue,
     * if it is present, More formally, removes an element {@code e} such
     * that {@code o.equals(e)}, if this queue contains one ormore such
     * elements. Returns {@code true} if and only if this queueu contained
     * the specified element (or equalently, if this queue changed as a
     * result of the call)
     * @param o
     * @return
     */
    public boolean remove(Object o){
       int i = indexOf(o);
        if(i == -1){
            return false;
        }else{
            removeAt(i);
            return true;
        }
    }

    /**
     * Return {@code true} if this queue contains the specified element
     * More formally, returns {@code true} if and only if thie queue contains
     * at least one element {@code e} such that {@code o.equals(e)}
     *
     * @param o object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     */
    public boolean contains(Object o){
        return indexOf(o) != -1;
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
    public E poll() {
        return null;
    }


    /**
     * Removes the ith element from queue
     *
     * Normally this method leaves the elements at up to i-1,
     * inclusive, untouched. Under these circumstances, it returns
     * null, Occasionally, in order to maintain the heap invariant,
     * it must swap a later element of the list with one earlier than
     * i. Under these circumstances, this method returns the element
     * that was previously at the end of the list and is now at some
     * position before i. This fact us used by iterator.remove so as to
     * avoid missing traversing elements
     *
     * @param i
     * @return
     */
    private E removeAt(int i){
        // assert i >= 0 && i < size
        modCount++;
        int s = --size;
        // 1.如果是最后一个元素, 则直接赋值给null
        if(s == i){ // removed last element
            queue[i] = null;
        }
        else{
            // 2.将最后一个元素取出来, 并且赋值 null
            E moved = (E)queue[s];
            queue[s] = null;
            // 3.将最后一个元素在i位置进行siftDown(这时最后一个元素不一定是整个堆中最大的, 但最大的节点一定会在最下面的那层)
            siftDown(i, moved);
            // 4. 经过下滤后, 发现节点 moved 没有变动, 说明节点没有变动, 则再进行一次siftUp
            if(queue[i] == moved){
                siftUp(i, moved);
                if(queue[i] != moved){
                    return moved;
                }
            }
        }
        return null;
    }

    /**
     * Returns an array containing all of the elements in this queue.
     * The element are in no particular order
     *
     * <p>
     *     The returned array will be "safe" in that no references to it are
     *     maintained by this queue. (In other words, this method must allocvate
     *     a new array), The caller is thus free to modify the returned array
     * </p>
     *
     * <p>
     *     This method acts bridge between array-based and collection-based APIs
     * </p>
     *
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray(){
        return Arrays.copyOf(queue, size);
    }

    /**
     * Returns an array containing all of the elements in this queue: the
     * runtime type of the returned array is that of the specified array.
     * The returned array elements are in no particular order.
     * If the queue fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this queue
     *
     * <p>
     *     If the queue fits in the specified array with room to spare
     *     (i.e, the array has more elements than queue), the element in
     *     the array immediately following the end of the collection is set to
     *     {@code null}
     * </p>
     *
     * <p>
     *     like the {@link #toArray()} method, this method acts as bridge between
     *     array-based and collection-based APIs. Further, this method allows
     *     precise control over the runtime type of the output array, and may,
     *     under certain circumstances, be used to save allocation costs
     * </p>
     *
     * <p>
     *     Suppose {@code x} is a queue known to contain only strings
     *     The following code can be used to dump the queue into a newly
     *     allocated array of {@code String}
     * </p>
     *
     * <pre>{@code String[] y = x.toArray(new String[0])}</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}
     *
     * @param a the array into which the elements of the queue are to
     *          be sorted, if it is big enough: otherwise, a new array of the
     *          same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     * is not a supertype of the runtime type of every element in
     * this queue
     */
    public <T> T[] toArray(T[] a){
        final int size = this.size;
        if(a.length < size){
            // Make a new array of a's runtime type, but my contents
            return (T[])Arrays.copyOf(queue, size, a.getClass());
        }
        System.arraycopy(queue, 0, a, 0, size);
        if(a.length > size){
            a[size] = null;
        }
        return a;
    }

    /**
     * Version of remove using reference equality, not equals.
     * Needed by iterator,remove
     *
     * @param o element to be removed from this queue, if present
     * @return {@code true} if removed
     */
    boolean removeEq(Object o){
        for(int i = 0; i < size; i++){
            if(o == queue[i]){
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * promoting x up the tree until it is greater than or equal to
     * its parent, or is the root.
     *
     * To simplify and speed up coercions and comparisons. the
     * Comparable and Comparator versions are separated into different
     * methods that are otherwise identical. (Similarly for siftDown)
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private void siftUp(int k, E x){
        if(comparator != null){
            siftUpUsingComparator(k, x);
        }else{
            siftUpComparable(k, x);
        }
    }

    private void siftUpComparable(int k, E x){
        Comparable<? super E> key = (Comparable<? super E>)x;
        while(k > 0){
            int parent = (k - 1) >>> 1; // 获取对应的父节点的下标
            Object e = queue[parent];  // 获取对应的父节点对应的值
            // 将当前节点与父节点的值进行比较
            // 若当前节点比其父节点大, 则说明不在需要在向上 sift 比较了
            //
            if(key.compareTo((E)e) >= 0){
                break;
            }
            queue[k] = e; // 将父节点下沉
            k = parent; // 将这次比较的父节点赋值给k, 为下次 k 与其父节点作比较而准备
        }
        // 这里的k 有可能是最初节点 x的父节点, 也有可能就是x节点最初的下标s
        queue[k] = key;
    }

    private void siftUpUsingComparator(int k, E x){
        while(k > 0){
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if(comparator.compare(x, (E) e) >= 0){
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = x;
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private void siftDown(int k, E x){
        if(comparator != null){
            siftDownUsingComparator(k, x);
        }else{
            siftDownComparable(k, x);
        }
    }


    private void siftDownComparable(int k, E x){
        Comparable<? super E> key = (Comparable<? super E>)x;
        // 获取对中最后节点的父节点
        // 注意 这里 size 是queue的length, 我们一般写法是用数组的下标
        int half = size >>> 1;          // loop while a non-leaf
        // 当 k >= half 时表示进行调整已经调整到了最底层(即叶子层)
        // 所以直接跳出循环
        while(k < half){
            int child = (k << 1) + 1; // assume left child is least
            Object c = queue[child];
            int right = child + 1;
            if(right < size &&
                    ((Comparable<? super E>)c).compareTo((E)queue[right]) > 0){
                c = queue[child = right];
            }
            if(key.compareTo((E)c) <= 0){
                break;
            }
            queue[k] = c; // 将子节点赋值到父节点上
            k = child; // 进行子节点的调整
        }
        // 跳出循环, 将 key值赋给对应上面 "queue[k]=c" 而进行交换的子节点上
        queue[k] = key;
    }

    private void siftDownUsingComparator(int k, E x){
        int half = size >>> 1;
        while (k < half){
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if(right < size &&
                    comparator.compare((E) c, (E) queue[right]) > 0){
                c = queue[child = right];
            }
            if(comparator.compare(x, (E) x) <= 0){
                break;
            }
            queue[k] = c;
            k = child;
        }
        queue[k] = x;
    }

    /**
     * Saves this queue to a stream (that is, serializes it)
     * @serialData The length of the array backing the instance is
     *              emitted (int), followed by all its elements
     *              (each an {@code Object}) in the proper order
     * @param s the stream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException{
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();

        // Write out array length, for compatibility with 1.5 version
        s.writeInt(Math.max(2, size + 1));

        // Write out all elements in the "proper order"
        for(int i = 0; i < size; i++){
            s.writeObject(queue[i]);
        }
    }

    /**
     * Reconstitutes the {@code KPriorityQueue} instance from a stream
     * (that is, deserializes it)
     * @param s the stream
     * @throws Exception
     */
    private void readObject(java.io.ObjectInputStream s) throws Exception{
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in (and discard) array length
        s.readInt();

        queue = new Object[size];

        //Read in all elements
        for(int i = 0; i < size; i++){
            queue[i] = s.readObject();
        }

        // Elements are guaranteed to be in "proper order", but the
        // spec has never explained what that might be
        heapify();
    }

    private final class Itr implements Iterator<E>{

        /**
         * Index (into queue array) of element to be returned by
         * subsequent call to next
         */
        private int cursor = 0;

        /**
         * Index of element returned by most recent call to next,
         * unless that element came from the forgetMeNot list.
         * Set to -1 if element is deleted by a call to remove
         */
        private int lastRet = -1;

        /**
         * A queue of elements that were moved from the unvisited portion of
         * the heap into the visited portion as a result of "unluck" element
         * removal during the iteration. (Unlucky element removal are those
         * that require a siftup instead of a siftdown.) Wemust visit all of
         * the element in this list to completed the "normal" iteration
         *
         * We expect that most iterations, even those involving removals,
         * will not need to store elements in this field
         */
        private ArrayDeque<E> forgetMeNot = null;

        /**
         * Element returned by the most recent call to next iff that
         * element was drawn from the forgetMeNot list
         */
        private E lastRetElt = null;

        /**
         * The modCount value that the iterator believes hat the backing
         * Queue should have. If this expectation is violated, the iterator
         * has detected concurrent modification
         */
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor < size ||
                    (forgetMeNot != null && !forgetMeNot.isEmpty());
        }

        @Override
        public E next() {
            if(expectedModCount != modCount){
                throw new ConcurrentModificationException();
            }
            if(cursor < size){
                return (E)queue[lastRet = cursor++];
            }
            if(forgetMeNot != null){
                lastRet = -1;
                lastRetElt = forgetMeNot.poll();
                if(lastRetElt != null){
                    return lastRetElt;
                }
            }

            throw new NoSuchElementException();
        }

        public void remove(){
            if(expectedModCount != modCount){
                throw new ConcurrentModificationException();
            }
            if(lastRet != -1){
                E moved = KPriorityQueue.this.removeAt(lastRet);
                lastRet = -1;
                if(moved == null){
                    cursor--;
                }else{
                    if(forgetMeNot == null){
                        forgetMeNot = new ArrayDeque<E>();
                    }
                    forgetMeNot.add(moved);
                }
            }else if(lastRetElt != null){
                KPriorityQueue.this.removeEq(lastRetElt);
                lastRetElt = null;
            }else{
                throw new IllegalStateException();
            }
        }
    }

    static final class PriorityQueueSpliterator<E> implements Spliterator<E>{


        /**
         * This is very similar to ArrayList Spliterator, expect for
         * extra null checks
         */
        private final KPriorityQueue<E> pq;
        private int index;              // current index, modified on advance/split
        private int fence;              // -1 until first use
        private int expectedModCount;   // initialized when fence set

        /** Creates new spliterator covering the given range */
        public PriorityQueueSpliterator(KPriorityQueue<E> pq, int orign,
                                        int fence, int expectedModCount) {
            this.pq = pq;
            this.index = orign;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence(){ // initialize fence to size on first use
            int hi;
            if((hi = fence) < 0){
                expectedModCount = pq.modCount;
                hi = fence = pq.size;
            }
            return hi;
        }

        @Override
        public Spliterator<E> trySplit() {
            return null;
        }

        public void forEachRemaining(Consumer<? super E> action){

        }

        @Override
        public boolean tryAdvance(Consumer<? super E> action) {
            if(action == null){
                throw new NullPointerException();
            }
            int hi = getFence(), lo = index;
            if(lo >= 0 && lo < hi){
                index = lo + 1;
                E e = (E)pq.queue[lo];
                if(e == null){
                    throw new ConcurrentModificationException();
                }
                action.accept(e);
                if(pq.modCount != expectedModCount){
                    throw new ConcurrentModificationException();
                }
                return true;
            }
            return false;
        }

        @Override
        public long estimateSize() {
            return (long)(getFence() - index);
        }

        @Override
        public int characteristics() {
            return Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.NONNULL;
        }
    }
}
