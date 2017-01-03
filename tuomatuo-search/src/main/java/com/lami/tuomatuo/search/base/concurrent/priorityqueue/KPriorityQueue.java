package com.lami.tuomatuo.search.base.concurrent.priorityqueue;

import java.util.*;
import java.util.function.Consumer;

/**
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

    /**
     * Creates a {@code PriorityQueue} with the default  initial
     * capacity (11) that orders its elements according to their
     * @param comparator
     */
    public KPriorityQueue(Comparator<? super E> comparator){
        this(DEFAULT_INITIAL_CAPACITY, comparator);
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
        if(s == i){ // removed last element
            queue[i] = null;
        }else{
            E moved = (E)queue[s];
            queue[s] = null;
            siftDown(i, moved);
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
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if(key.compareTo((E)e) >= 0){
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

    private void siftUpUsingComparator(int k, E x){
        while(k > 0){
            int parent = (k - 1) >>> 1;
            Object e = queue[parent];
            if(comparator.compare(x, (E)e) >= 0){
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
        int half = size >>> 1;          // loop while a non-leaf
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
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

    private void siftDownUsingComparator(int k, E x){
        int half = size >>> 1;
        while (k < half){
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;
            if(right < size &&
                    comparator.compare((E)c, (E)queue[right]) > 0){
                c = queue[child = right];
            }
            if(comparator.compare(x, (E)x) <= 0){
                break;
            }
            queue[k] = c;
            k = child;
        }
        queue[k] = x;
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
