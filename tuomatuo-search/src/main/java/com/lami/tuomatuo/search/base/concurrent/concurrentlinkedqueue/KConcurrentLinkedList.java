package com.lami.tuomatuo.search.base.concurrent.concurrentlinkedqueue;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import java.util.*;

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


    private transient volatile Node<E> head;

    private transient volatile Node<E> tail;

    public KConcurrentLinkedList() {
        head = tail = new Node<E>(null);
    }

    /**
     * Create a {@code KConcurrentLinkedQueue}
     * initially containing the elements of the given collection
     * added in traversal order of the collection's iterator
     *
     * @param c the collection of the elements to initially contain
     * @throws NullPointerException if the specified collection or any of its elements are null
     */
    public KConcurrentLinkedList(Collection<? extends E> c) {
        Node<E> h = null, t = null;
        for(E e : c){
            checkNotNull(e);
            Node<E> newNode = new Node<E>(e);
            if(h == null){
                h = t = newNode;
            }else{
                t.lazySetNext(newNode);
                t = newNode;
            }
        }

        if(h == null){
            h = t = new Node<E>(null);
        }
        head = h;
        tail = t;
    }

    /**
     * Inserts the specified element at the tail of this queue
     * As the queue is unbounded, this method will never throw
     * {@link IllegalStateException} or return {@code false}
     *
     * @param e {@code true} (as specified by {@link Collection#add(Object)}})
     * @return NullPointerException if the specified element is null
     */
    public boolean add(E e){
        return offer(e);
    }

    /**
     * Tries to CAS head to p, If successfully, repoint old head to itself
     * as sentinel for succ(), blew
     *
     * 将节点 p设置为新的节点(这是原子操作),
     * 之后将原节点的next指向自己, 直接变成一个哨兵节点(为queue节点删除及garbage做准备)
     *
     * @param h
     * @param p
     */
    final void updateHead(Node<E> h, Node<E> p){
        if(h != p && casHead(h, p)){
            h.lazySetNext(h);
        }
    }

    /**
     * Returns the successor of p, or the head node if p.next has been
     * linked to self, which will only be true if traversing with a
     * stale pointer that is noew off the list
     * @param p
     * @return
     */
    final Node<E> succ(Node<E> p){
        Node<E> next = p.next;
        return (p == next)? head : next;
    }

    /**
     * Inserts the specified element at the tail of this queue
     * As the queue is unbounded, this method will never return {@code false}
     *
     * @param e {@code true} (as specified by {@link Queue#offer(Object)})
     * @return NullPointerException if the specified element is null
     */
    public boolean offer(E e){
        checkNotNull(e);
        final Node<E> newNode = new Node<E>(e);

        for(Node<E> t = tail, p = t;;){
            Node<E> q = p.next;
            if(q == null){
                // p is last node
                if(p.casNext(null, newNode)){
                    // Successful CAS is the linearization point
                    // for e to become an element of the queue,
                    // and for newNode to become "live"
                    if(p != t){ // hop two nodes at a time
                        casTail(t, newNode); // Failure is OK
                    }
                    return true;
                }
                // Lost CAS race to another thread; re-read next
            }
            // 调用 poll 时, 调用 updateHead 导致的
            else if(p == q){
                // We have fallen off list. If tail is unchanged, it
                // will also be off-list, in which case we need to
                // jump to head, from which all live nodes are always
                // reachable. Else the new tail is a better bet
                /** 1. 大前提 p 是已经被删除的节点
                 *  2. 判断 tail 是否已经改变
                 *      1) tail 已经变化, 则说明 tail 已经重新定位
                 *      2) tail 未变化, 而 tail 指向的节点是要删除的节点, 所以让 p 指向 head
                 */
                p = (t != (t = tail))? t : head;
            }else{
                // Check for tail update after two hops
                p = (p != t && (t != (t = tail))) ? t : q;
            }
        }
    }

    public E poll(){
        restartFromHead:
        for(;;){
            for(Node<E> h = head, p = h, q;;){
                E item = p.item;

                if(item != null && p.casItem(item, null)){
                    // Successful CAS is the linearization point
                    // for item to be removed from this queue
                    if(p != h){ // hop two nodes at a time
                        updateHead(h, ((q = p.next) != null)? q : p);
                    }
                    return item;
                }
                else if((q = p.next) == null){
                    updateHead(h, p);
                    return null;
                }
                else if(p == q){
                    continue restartFromHead;
                }else
                    p = q;
            }
        }
    }

    public E peek(){
        restartFromHead:
        for(;;){
            for(Node<E> h = head, p = h, q;;){
               E item = p.item;
                if(item != null || (q = p.next) == null){
                    updateHead(h, p);
                    return item;
                }
                else if(p == q){
                    continue restartFromHead;
                }else{
                    p = q;
                }
            }
        }
    }

    /**
     * Returns that first live (noe-deleted) node on the list, or null if the none
     * This is yet another variant of poll/peek. here returning the
     * first node, not element. We could make peek(), a wapper around
     * first(), but that would cost an extra volatile read of item
     * and the need to add aretry loop to deal with the possibility
     * of losing a race to a concurrent poll().
     *
     * @return
     */
    Node<E> first(){
        restartFromHead:
        for(;;){
            for(Node<E> h = head, p = h, q ;;){
                boolean hasItem = (p.item != null);
                if(hasItem || (q = p.next) == null){
                    updateHead(h, p);
                    return hasItem? p:null;
                }
                else if(p == q){
                    continue restartFromHead;
                }
                else
                    p = q;
            }
        }
    }

    /**
     * Returns {@code true} if this queue contaions no elements
     *
     * @return {@code true} if this queue contains no elements
     */
    public boolean isEmpty(){
        return first() == null;
    }

    /**
     * Returns the number of the elements in this queue. If this queue
     * contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}
     *
     * <p>
     *     Beware that, unlike in most collections, this method is
     *     <em>NOT</em> a constant-time operation. Because of the
     *     asynchrouns nature of these queue, determining the current
     *     number of the elements requires an 0(n) traversal
     *     Additionally, if the elements are added or removed during execution
     *     of the method, the returned result may be inaccurate. Thus,
     *     this method is typically not very useful in concurrent
     *     applications
     * </p>
     *
     * @return the number of the elements in this queue
     */
    public int size(){
        int count = 0;
        for(Node<E> p =first(); p!=null; p = succ(p)){
            if(p.item != null){
                // Collection.size() spec says to max out
                if(++count == Integer.MAX_VALUE){
                    break;
                }
            }
        }
        return count;
    }

    /**
     * Return {@code true} if this queue contains the specified element.
     * More formally, return {@code true} if and only if this queue contains
     * at least one element {@code e} such that {@code o.equels(e)}
     *
     * @param o object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     */
    public boolean contains(Object o){
        if(o == null) return false;
        for(Node<E> p = first(); p != null; p = succ(p)){
            E item = p.item;
            if(item != null && o.equals(item)){
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a single instance of the specified element from this queu,
     * if it is present. More formally, remove an element {@code e} such
     * that {@code o.equals(e)}, if this queue contains one or more such
     * elements
     *
     * Returns {@code true} if this queue contained the specified element
     * (or equivalently, if this queue changed as a result of the call)
     *
     * @param o element to be removed from this queue, if present
     * @return {@code} if this queue changed as a result of the call
     */
    public boolean remove(Object o){
       if(o == null) return false;
        Node<E> pred = null;

        for(Node<E> p = first(); p != null; p = succ(p)){
            E item = p.item;
            if(item != null &&
                    o.equals(item) &&
                    p.casItem(item, null)
                    ){
                Node<E> next = succ(p);
                if(pred != null && next != null){
                    pred.casNext(p, next);
                }
            }
            pred = p;
        }

        return false;
    }

    public boolean addAll(Collection<? extends E> c){
        if(c == this){
            // As historically specified in AbstractQueue#addAll
            throw new IllegalArgumentException();
        }

        // Copy c into a private chain of Nodes
        Node<E> beginningOfTheEnd = null, last = null;
        for(E e : c){
            checkNotNull(e);
            Node<E> newNode = new Node<E>(e);
            if(beginningOfTheEnd == null){
                beginningOfTheEnd = last = newNode;
            }else{
                last.lazySetNext(newNode);
                last = newNode;
            }
        }

        if(beginningOfTheEnd == null){
            return false;
        }

        // Atomically append the chain at the tail of this collection
        for(Node<E> t = tail, p = t;;){
            Node<E> q = p.next;
            if(q == null){
                // p is last node
                if(p.casNext(null, beginningOfTheEnd)){
                    // Successful CAS is the linearization point
                    // for all elements to be added to this queue.
                    if(!casTail(t, last)){
                        // Try a little harder to update tail
                        // since we may be adding many elements
                        t = tail;
                        if(last.next == null){
                            casTail(t, last);
                        }
                    }

                    return true;
                }
                // Lost CAS race to another thread; re-read next
            }
            else if(p == q){
                /** We have fallen off list. If tail is unchanged, it
                 * will also be off-list, iwhich case we need to jump
                 * to head, from which all live nodes are are always
                 * reachable. Else the new tail is a better bet
                 */
                p = (t != (t = tail)) ? t : head;
            }else{
                // Check for tail updates after two hops
                p = (p != t && t != (t = tail)) ? t : q;
            }
        }
    }

    /**
     * Returns an array containing all of the elements in this queue, in
     * proper sequence.
     *
     * <p>
     *     The returned array will be 'safe' in that no references to it are
     *     maintained by this queue. (In other words, this method must allocate
     *     a new array). The caller is thus free to modify the returned array
     *
     *     This method acts as bridge between array-based and collection-based APIs
     * </p>
     *
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray(){
        // Use ArrayList to deal with resizing
        ArrayList<E> al = new ArrayList<E>();
        for(Node<E> p = first(); p != null; p = succ(p)){
            E item = p.item;
            if(item != null){
                al.add(item);
            }
        }
        return al.toArray();
    }


    /**
     * Returns an array containing all of the elements in this queue, in
     * proper sequence; the running type of the returned array is that of
     * the specified array. If the queue fits in the specified array, it
     * is returned therein. Otherwise, a new array is allocated with the
     * runtime type of the specified array and the size of this queue
     *
     * <p>
     *     If this queue fits in the specified array with room to space
     *     (i.e, the array has more elements than this queue), the element in
     *     the array immediately following the end of the queue is set to
     *     {@code null}
     * </p>
     *
     * <p>
     *     Like the {@link #toArray(Object[])} method, this method acts as bridge between
     *     array-based and collection-based APIs, Futher, this method allows
     *     precise control over the runtime type of the output array, and may,
     *     under certain circumstances, be used to save allocation costs
     * </p>
     *
     * <pre> {@code String[] y = x.toArray(new String[0])}</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in fuction to
     * {@code toArray}
     *
     * @param a the array into which the elements of the queue are to
     *          to stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose
     *
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     *          is not a supertype of the runtime type of every element in
     *          this queue
     * @throws NullPointerException if the specified array is null
     */
    public <T> T[] toArray(T[] a){
        // try to use sent-in array
        int k = 0;
        Node<E> p;
        for(p = first(); p != null && k < a.length; p = succ(p)){
            E item = p.item;
            if(item != null){
                a[k++] = (T)item;
            }
        }
        if(p == null){
            if(k < a.length){
                a[k] = null;
            }
            return a;
        }

        // If won't fit, use ArrayList version
        ArrayList<E> al = new ArrayList<E>();
        for(Node<E> q = first(); q != null; q = succ(q)){
            E item = q.item;
            if(item != null){
                al.add(item);
            }
        }
        return al.toArray(a);
    }

    /**
     * Returns an iterator over the elements in this queue in proper sequence
     * The elements will be returned in order from first (head) to last (tail)
     *
     * <p>
     *     The returned iterator is
     *     <a href="package-summary.html#Weakly"> weakly consistent</>
     * </p>
     *
     * @return an iterator over the elements in this queue in proper sequence
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Throws NullPointerException if argument is null
     * @param v
     */
    private static void checkNotNull(Object v){
        if(v == null){
            throw new NullPointerException();
        }
    }

    private boolean casTail(Node<E> cmp, Node<E> val){
        return unsafe.compareAndSwapObject(this, tailOffset, cmp, val);
    }

    private boolean casHead(Node<E> cmp, Node<E> val){
        return unsafe.compareAndSwapObject(this, headOffset, cmp, val);
    }


    // Unsafe mechanics
    private static Unsafe unsafe;
    private static long headOffset;
    private static long tailOffset;

    static {
        try{
            unsafe = UnSafeClass.getInstance();
            Class<?> k = KConcurrentLinkedList.class;
            headOffset = unsafe.objectFieldOffset(k.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(k.getDeclaredField("tail"));
        }catch (Exception e){
            throw new Error(e);
        }
    }

    private class Itr implements Iterator<E>{

        /** Next node to return item for */
        private Node<E> nextNode;

        /**
         * nextItem holds on to item fields because once we claim
         * that an elements exists in hasNext(), we must return it in
         * the following next() call even if it was in the process of
         * being removed when hasNext() was called
         */
        private E nextItem;

        /** Node of the last returned item, to support remove */
        private Node<E> lastRet;

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            return null;
        }

        /**
         * Moves to next valid node and returns item to return for
         * next(), or null if no such
         */
        private E advance(){
            lastRet = nextNode;
            E x = nextItem;

            Node<E> pred, p;
            if(nextNode == null){
                p = first();
                pred = null;
            }else{
                pred = nextNode;
                p = succ(nextNode);
            }

            for(;;){
                if(p == null){
                    nextNode = null;
                    nextItem = null;
                    return x;
                }
                E item = p.item;
                if(item != null){
                    nextNode = p;
                    nextItem = item;
                    return x;
                }
                else{
                    // skip over nulls
                    Node<E> next = succ(p);
                    if(pred != null && next != null){
                        pred.casNext(p, next);
                    }
                    p = next;
                }
            }
        }
    }

    static class Node<E> {
        volatile E item;
        volatile Node<E> next;

        /**
         * Constructs a new Node, Uses relaxed write because item can
         * only be seen after publication vis casNext
         */
        Node(E item) {
            unsafe.putObject(this, itemOffset, item);
        }

        boolean casItem(E cmp, E val){
            return unsafe.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        void lazySetNext(Node<E> val){
            unsafe.putOrderedObject(this, nextOffset, val);
        }

        boolean casNext(Node<E> cmp, Node<E> val){
            return unsafe.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        private static Unsafe unsafe;
        private static long itemOffset;
        private static long nextOffset;

        static {
            try{
                unsafe = UnSafeClass.getInstance();
                Class<?> k = Node.class;
                itemOffset = unsafe.objectFieldOffset(k.getDeclaredField("item"));
                nextOffset = unsafe.objectFieldOffset(k.getDeclaredField("next"));
            }catch (Exception e){

            }
        }
    }
}
