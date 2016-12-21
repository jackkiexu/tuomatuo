package com.lami.tuomatuo.search.base.concurrent.concurrentskiplistmap;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * A scalable concurrent {@link ConcurrentNavigableMap} implementation
 * The map is stored according to the {@linkplain Comparable  natural
 * ordering } of its keys, or by a {@link Comparator} provided at map
 * creation time, depending on which constructor is used
 *
 * <p>
 *     This class implements a concurrent variant of
 *     <a href="http://en.wikipedia.org/wiki/Skip_list" target="_top"> SkipLists</>
 *     providing expected average <i>log(n)</i> time cost for the
 *     {@code containsKey}, {@cost get}, {@cost put} and
 *     {@code remove } operations and their variants. Insertion, removal,
 *     update, and access operations safely execute concurrently by multiple
 *     threads
 * </p>
 *
 * <p>
 *     Ascending key ordered views and their iterators are faster than
 *     descending ones
 * </p>
 *
 * <p>
 *     All {@code Map.Entry} pairs returned by methods in this class
 *     and its views represent snapshots of mappings at the time they were
 *     produced. They do <em>not</em> support the {@code Entry.setValue}
 *     method (Note however that it is possible to change mappings in the associated map using {@code put}, {@code putIfAbsent}),
 *     or {@code replace}, depending on exactly which effect you need
 * </p>
 *
 * <p>
 *     Beware that, unlike in most collections, the {@code size}
 *     method its views represent snapshots of mappings at time they were
 *     produced. They do <em>not</em> support the {@code Entry.setValue}
 *     method (Note however that it is possible to change mapping in the associated map using {@code put})
 *     {@code putIfAbsent}, or {@code replace}, depending on exactly which effect you need
 * </p>
 *
 * <p>
 *     Beware that, unlike in most collections, the {@code size}
 *     method is <em>not</em> a constant-time operation, Because of the
 *     asynchrounous nature of these maps, determining the current number
 *     of the elements requires a traversal of the elements, and so may report
 *     inaccurate results, if this collection is modified during traversal
 *     Additionally, the bulk operations {@code putAll}, {@code equals},
 *     {@code toArray}, {@code containsValue}, and {@code clear} are
 *     <em>not</em> guaranteed to be performed atomically. For example, an
 *     iterator operating concurrently with a {@code putAll} operation
 *     might view only some of the added elements
 * </p>
 *
 * <p>
 *     This class and its views and iterators implement all of the
 *     <em>optional</em> methods of the {@link Map} and {@link Iterator}
 *     interface. Like most other concurrent collections, this class does
 *     <em>mot</em> permit the use of {@code null} keys or values because some
 *     null return values cannot be reliably distinguished from the absence of
 *     elements.
 * </p>
 *
 * <p>This class is amember of the
 *      <a href="{@docPost}/../technotes/guides/collections/index.html">
 *          Java Collections Framework
 *      </a>
 * </p>
 *
 *
 * Created by xujiankang on 2016/12/21.
 */
public class KConcurrentSkipListMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -8627078645895051609L;

    /**
     * Special value used to identify base-level header
     */
    private static final Object BASE_HEADER = new Object();

    /**
     * The topmost head index of the skiplist
     */
    private transient volatile HeadIndex<K, V> head;

    /**
     * The comparator used to maintain order in this map, or null if
     * using natural ordering. (Non-private to simplify access in
     * nexted classes)
     */
     Comparator<? super K> comparator;

    /** Lazily initialized key set */

    @Override
    public ConcurrentNavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return null;
    }

    @Override
    public ConcurrentNavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return null;
    }

    @Override
    public ConcurrentNavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return null;
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    @Override
    public ConcurrentNavigableMap<K, V> subMap(K fromKey, K toKey) {
        return null;
    }

    @Override
    public ConcurrentNavigableMap<K, V> headMap(K toKey) {
        return null;
    }

    @Override
    public ConcurrentNavigableMap<K, V> tailMap(K fromKey) {
        return null;
    }

    @Override
    public K firstKey() {
        return null;
    }

    @Override
    public K lastKey() {
        return null;
    }

    @Override
    public Entry<K, V> lowerEntry(K key) {
        return null;
    }

    @Override
    public K lowerKey(K key) {
        return null;
    }

    @Override
    public Entry<K, V> floorEntry(K key) {
        return null;
    }

    @Override
    public K floorKey(K key) {
        return null;
    }

    @Override
    public Entry<K, V> ceilingEntry(K key) {
        return null;
    }

    @Override
    public K ceilingKey(K key) {
        return null;
    }

    @Override
    public Entry<K, V> higherEntry(K key) {
        return null;
    }

    @Override
    public K higherKey(K key) {
        return null;
    }

    @Override
    public Entry<K, V> firstEntry() {
        return null;
    }

    @Override
    public Entry<K, V> lastEntry() {
        return null;
    }

    @Override
    public Entry<K, V> pollFirstEntry() {
        return null;
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        return null;
    }

    @Override
    public ConcurrentNavigableMap<K, V> descendingMap() {
        return null;
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public NavigableSet<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return null;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public V replace(K key, V value) {
        return null;
    }

    /* ----------------------------- Serialization ------------------ */
    /**
     * Saves this map to a stream (that is, serializes it ).
     */




    /* ----------------------------- Iterators ---------------------- */

    /**
     * Base of iterator classes
     * @param <T>
     */
    abstract class Iter<T> implements Iterator<T>{
        /** the last node returned by next() */
        Node<K, V> lastReturned;
        /** the next node to return from next() */
        Node<K, V> next;
        /** Cache of next value field to maintain weak consistency */
        V nextValue;

        /** Initializes ascending iterator for entire range */
        Iter(){
            while((next = next.next) != null){
                Object x = next.value;
                if(x != null && x != next){
                    V vv = (V) x;
                    nextValue = vv;
                    break;
                }
            }
        }

        public final boolean hasNext(){
            return next != null;
        }

        /** Advances next to higher entry */
        final void advance(){
            if(next == null){
                throw new NoSuchElementException();
            }
            lastReturned = next;
            while((next = next.next) != null){
                Object x = next.value;
                if(x != null && x != next){
                    V vv = (V) x;
                    nextValue = vv;
                    break;
                }
            }
        }

        public void remove(){
            Node<K, V> l = lastReturned;
            if(l == null){
                throw new IllegalStateException();
            }
            /**
             * It would not be worth all of the overhead to directly
             * unlink from here. Using remove is fast enough
             */
            KConcurrentSkipListMap.this.remove(l.key);
            lastReturned = null;
        }

    }

    /**
     * Nodes heading each level keep track of their level
     */
    static final class HeadIndex<K, V> extends Index<K, V>{

        final int level;

        /**
         * Creates index node with given values
         *
         * @param node
         * @param down
         * @param right
         */
        public HeadIndex(Node<K, V> node, Index<K, V> down, Index<K, V> right, int level) {
            super(node, down, right);
            this.level = level;
        }
    }

    /* ----------------------------- Indexing ----------------------- */

    /**
     *
     * Index nodes represent the value levels of the skip list. Node that
     * even though both Nodes and Indexes have forward-pointing
     * fields, they have different types and are handled in different
     * ways, that can't nicely be captured by placing field in a
     * shared abstract class.
     */

    static class Index<K, V>{

        final Node<K, V> node;
        final Index<K, V> down;
        volatile Index<K, V> right;

        /**
         * Creates index node with given values
         * @param node
         * @param down
         * @param right
         */
        public Index(Node<K, V> node, Index<K, V> down, Index<K, V> right) {
            this.node = node;
            this.down = down;
            this.right = right;
        }

        /**
         * compareAndSet right field
         * @param cmp
         * @param val
         * @return
         */
        final boolean casRight(Index<K, V> cmp, Index<K, V> val){
            return unsafe.compareAndSwapObject(this, rightOffset, cmp, val);
        }

        /**
         * Returns true if the node this indexes has been deleted.
         * @return true if indexed node is known to be deleted
         */
        final boolean indexesDeletedNode(){
            return node.value == null;
        }

        /**
         * Tries to CAS newSucc as successor. To minimize races with
         * unlink that may lose this index node, if the node being
         * indexed is known to be deleted, it doesn't try to link in
         *
         * @param succ the expecteccurrent successor
         * @param newSucc the new successor
         * @return true if successful
         */
        final boolean link(Index<K, V> succ, Index<K, V> newSucc){
            Node<K, V> n = node;
            newSucc.right = succ;
            return n.value != null  && casRight(succ, newSucc);
        }

        /**
         * Tries to CAS field to skip over apparent successor
         * succ. Fails (forcing a retravesal by caller) if this node
         * is known to be deleted
         * @param succ the expected current successor
         * @return true if successful
         */
        final boolean unlink(Index<K, V> succ){
            return node.value != null && casRight(succ, succ.right);
        }

        // Unsafe mechanics
        private static final Unsafe unsafe;
        private static final long rightOffset;

        static {
            try{
                unsafe = UnSafeClass.getInstance();
                Class<?> k = Index.class;
                rightOffset = unsafe.objectFieldOffset(k.getDeclaredField("right"));
            }catch (Exception e){
                throw new Error(e);
            }
        }
    }






    static final class Node<K, V>{
        final K key;
        volatile Object value;
        volatile Node<K, V> next;

        /**
         * Creates a new regular node
         * @param key
         * @param value
         * @param next
         */
        public Node(K key, Object value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /**
         * Creates a new marker node. A marker is distinguished by
         * having its value field point to itself. Marker nodes also
         * have null keys, a fact that is exploited in a few places,
         * but this doesn't distinguish markers from the base-level
         * header node (head.node), which also has a null key
         *
         * @param next
         */
        public Node(Node<K, V> next) {
            this.key = null;
            this.value = this;
            this.next = next;
        }

        /**
         * compareAndSet value field
         * @param cmp
         * @param val
         * @return
         */
        boolean casValue(Object cmp, Object val){
            return unsafe.compareAndSwapObject(this, valueOffset, cmp, val);
        }

        /**
         * compareAndSet next field
         * @param cmp
         * @param val
         * @return
         */
        boolean casNext(Node<K, V> cmp, Node<K, V> val){
            return unsafe.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        /**
         * Returns true if this node is a marker. This method isn't
         * actually called in any current code checking for markers
         * because callers will have already read value field and need
         * to use that read ( not another done here) and so directly
         * test f value point to node
         *
         * @return true if this node is a marker node
         */
        boolean isMarker(){
            return value == this;
        }

        /**
         * Returns true if this node is the header of the base-level list
         * @return
         */
        boolean isBaseHeader(){
            return value == BASE_HEADER;
        }

        /**
         * Tries to append a deletion marker to this node.
         *
         * @param f the assumed current successor of this node
         * @return true if successful
         */
        boolean appendMarker(Node<K, V> f){
            return casNext(f, new Node<K, V>(f));
        }

        /**
         * Help out a deletion by appending marker or unlinking from
         * predecessor. This called during traversals when value
         * field seen to be null
         *
         * @param b
         * @param f
         */
        void helpDelete(Node<K, V> b, Node<K, V> f){
            /**
             * Rechecking links and then doing only one of the
             * help-out stages per call tends to minimize CAS
             * interference among helping threads
             */
            if(f == next && this == b.next){
                if(f == null || f.value != f){ // not already marked
                    casNext(f, new Node<K, V>(f));
                }else{
                    b.casNext(this, f.next);
                }
            }
        }

        /**
         * Returns value if this node contains a valid key-value pair,
         * else null.
         *
         * @return this node's value if it isn't a marker or header or
         * is deleted, else null
         */
        V getValidValue(){
            Object v = value;
            if(v == this || v == BASE_HEADER){
                return null;
            }
            V vv = (V)v;
            return vv;
        }

        /**
         * Creates and returns a new SimpleImmutableEntry holding current
         * mapping if this node holds a valid value, else null.
         *
         * @return new entry or null
         */
        AbstractMap.SimpleImmutableEntry<K, V> createSnapshot(){
            Object v = value;
            if(v == null || v == this || v == BASE_HEADER){
                return null;
            }
            V vv = (V) v;
            return new AbstractMap.SimpleImmutableEntry<K, V>(key, vv);
        }

        // UNSAFE mechanics
        private static final Unsafe unsafe;
        private static final long valueOffset;
        private static final long nextOffset;

        static {
            try {
                unsafe = UnSafeClass.getInstance();
                Class<?> k = Node.class;
                valueOffset = unsafe.objectFieldOffset(k.getDeclaredField("value"));
                nextOffset = unsafe.objectFieldOffset(k.getDeclaredField("next"));
            }catch (Exception e){
                throw new Error(e);
            }
        }

    }

    // unsafe mechanics
    private static final Unsafe unsafe;
    private static final long headOffset;
    private static final long SECONDARY;

    static {
        try {
            unsafe = UnSafeClass.getInstance();
            Class<?> k = KConcurrentSkipListMap.class;
            headOffset = unsafe.objectFieldOffset(k.getDeclaredField("head"));
            SECONDARY = unsafe.objectFieldOffset(k.getDeclaredField("threadLocalRandomSecondarySeed"));
        }catch (Exception e){
            throw new Error(e);
        }
    }
}
