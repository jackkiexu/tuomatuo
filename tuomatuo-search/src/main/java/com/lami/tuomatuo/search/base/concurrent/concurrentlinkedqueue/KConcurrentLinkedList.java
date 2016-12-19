package com.lami.tuomatuo.search.base.concurrent.concurrentlinkedqueue;

import org.apache.log4j.Logger;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;

/**
 * An unbounded thread-safe {@linkplain Queue queue} based on linked nodes
 * This queue orders elements FiFO (first-in-first-out).
 *
 * The <em>head</em> of the queue is that element that has been on the
 * queue the logest time.
 *
 * The <em>tail</em> of the queue is that element that has been on the
 * queue the shorest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval(检索)
 * operations obtain elements at the head of the queue.
 *
 * a {@code ConcurrentLinkedQueue} is an appropriate choice when
 * many threads will share access to a common collection
 *
 * Like most other concurrent collection implementations, this class
 * does not permit the use of {@code null} elements
 *
 * <p>
 *     This implementation employs an efficient <em>non-blocking</em>
 *     algorithm based on one described in
 *     <a href="http://www.cs.rochester.edu/u/scott/papers/1996_PODC_queues.pdf">
 *          Simple, Fast, and Practical Non-Blocking and Blocking Concurrent Queue
 *          Algorithms
 *     </a>
 *     by Maged M.Michael and Michael L. Scott
 * </p>
 *
 * <p>
 *     Iterators are <i>weakly consistent</i>, returning elements
 *     reflecting the state of the queue at some point at or since the
 *     creation of the iterator. They do <em>not</em> throw {@link java.util.ConcurrentModificationException}
 * </p>
 *
 * and may proceed concurrently with other operations. Elements contained in the queue since the creation
 * of the iterator will be returned exactly once
 *
 *
 * Created by xjk on 12/19/16.
 */
public class KConcurrentLinkedList<E> extends AbstractQueue<E> implements Queue<E>, java.io.Serializable {

    private static final Logger logger = Logger.getLogger(KConcurrentLinkedList.class);

    private static final long serialVersionUID = -980957881043363309L;

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
