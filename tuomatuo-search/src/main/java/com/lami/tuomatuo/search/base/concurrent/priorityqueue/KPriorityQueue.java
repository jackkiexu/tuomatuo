package com.lami.tuomatuo.search.base.concurrent.priorityqueue;

import java.util.AbstractQueue;
import java.util.Comparator;
import java.util.Iterator;

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
     *
     */
    public KPriorityQueue(){
        this(DEFAULT_INITIAL_CAPACITY, null);
    }

    /**
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
}
