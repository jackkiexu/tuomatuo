package com.lami.tuomatuo.search.base.concurrent.concurrenthashmap;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xjk on 2016/11/16.
 */
public class MyConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {

    private static final long serialVersionUID = 7249069246763182397L;

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static final int DEFAULT_CAPACITY = 16;

    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    private static final float LOAD_FACTOR = 0.75f;

    static final int TREEIFY_THRESHOLD = 8;

    static final int UNTREEIFY_THRESHOLD = 6;

    static final int MIN_TREEIFY_CAPACITY = 64;

    private static final int MIN_TRANSFER_STRIDE = 16;

    private static int RESIZE_STAMP_BITS = 16;

    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

    static final int MOVED          = -1;
    static final int TREEBIN        = -2;
    static final int RESERVED       = -3;
    static final int HASH_BITS      = 0x7fffffff;

    static final int NCPU = Runtime.getRuntime().availableProcessors();

    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("segments", Segment[].class),
            new ObjectStreamField("segmentMask", Integer.TYPE),
            new ObjectStreamField("segmentShift", Integer.TYPE),
    };


    static class Node<K, V> implements Map.Entry<K, V> {
        int hash;
        K key;
        volatile V val;
        volatile Node<K, V> next;

        public Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return val;
        }

        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object o) {
            Object k, v, u; Map.Entry<?,?> e;
            return ((o instanceof Map.Entry) &&
                    (k = (e = (Map.Entry<?,?>)o).getKey()) != null &&
                    (v = e.getValue()) != null &&
                    (k == key || k.equals(key)) &&
                    (v == (u = val) || v.equals(u)));
        }

        Node<K, V> find(int h, Object k){
            Node<K, V> e = this;
            if(k != null){
                do{
                    K ek;
                    if(e.hash == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek)))){
                        return e;
                    }
                }while((e = e.next) != null);
            }

            return null;
        }

    }

    static final int spread(int h){
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    private static final int tableSizeFor(int c){
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;

        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY)? MAXIMUM_CAPACITY : n + 1;
    }

    static Class<?> comparableClassFor(Object x){
        if(x instanceof Comparable){
            Class<?> c; Type[] ts, as; Type t;
            ParameterizedType p;
            if((c = x.getClass()) == String.class){
                return c;
            }
            if((ts = c.getGenericInterfaces()) != null){
                for(int i = 0; i < ts.length; i++){
                    if(((t = ts[i]) instanceof ParameterizedType) &&
                            ((p = (ParameterizedType)t).getRawType() == Comparable.class) &&
                            (as = p.getActualTypeArguments()) != null &&
                            as.length == 1 && as[0] == c
                            ){
                        return c;
                    }
                }
            }
        }

        return null;
    }

    static int compareComparables(Class<?> kc, Object k, Object x){
        return (x == null || x.getClass() != kc ? 0 : ((Comparable)k).compareTo(x));
    }

    static final <K, V> Node<K, V> tabAt(Node<K, V>[] tab, int i){
        return (Node<K, V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }

    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i, Node<K,V> c, Node<K,V> v){
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }

    static final <K, V> void setTabAt(Node<K,V>[] tab, int i, Node<K,V> v){
        U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);
    }

    transient volatile Node<K,V>[] table;

    private transient volatile Node<K,V>[] nextTable;

    private transient volatile long baseCount;

    private transient volatile int sizeCtl;

    private transient volatile int transferIndex;

    private transient volatile int cellsbusy;

    private transient volatile CounterCell[] counterCells;

    /** --------------------------- Fields ------------------------ */

    transient volatile Node<K,V>[] table;

    private transient volatile Node<K, V>[] nextTable;

    private transient volatile long baseCount;

    private transient volatile int sizeCtl;

    private transient volatile int transferIndex;

    private transient volatile int cellsBusy;

    private transient volatile CounterCell[] counterCells;

    // views
    private transient KeySetView<K, V> keySet;
    private transient ValuesView<K, V> values;
    private transient EntrySetView<K, V> entrySet;







    long sumCount(){
        CounterCell[] as = counterCells; CounterCell a;
        long sum = baseCount;
        if(as != null){
            for(int i = 0; i < as.length; ++i){
                if((a = as[i]) != null){
                    sum += a.value;
                }
            }
        }
        return sum;
    }

    public long mappingCount(){
        long n = sumCount();
        return  (n < 0L) ? 0L : n;
    }








    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    public V putIfAbsent(K key, V value) {
        return null;
    }

    public boolean remove(Object key, Object value) {
        return false;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    public V replace(K key, V value) {
        return null;
    }

    public static class KeySetView<K,V> extends CollectionView<K, V, K> implements Set<K>, java.io.Serializable{

        private static final long serialVersionUID = 7249069246763182397L;
        private V value;

        KeySetView(MyConcurrentHashMap<K, V> map) {
            super(map);
            this.value = value;
        }

        public V getMappedValue(){
            return value;
        }
        public boolean contains(Object o){
            return map.containsKey(o);
        }

        public boolean remove(Object o){
            return map.remove(o) != null;
        }

        @Override
        public Iterator<K> iterator() {
            Node<K,V>[] t;
            MyConcurrentHashMap<K, V> m = map;
            int f = (t = m.table) == null ? 0 : t.length;
            return null;
        }

        public boolean add(K k) {
            return false;
        }

        public boolean containsAll(Collection<?> c) {
            return false;
        }

        public boolean addAll(Collection<? extends K> c) {
            return false;
        }
    }

    static final class TableStack<K, V>{
        int length;
        int index;
        Node<K, V>[] tab;
        TableStack<K,V> next;
    }

    static class ForwardingNode<K, V> extends Node<K,V>{
        Node<K,V>[] nextTable;
        ForwardingNode(Node<K,V>[] tab){
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }

        public ForwardingNode(int hash, K key, V val, Node<K, V> next) {
            super(hash, key, val, next);
        }

        Node<K,V> find(int h, Object k){
            // loop to avoid arbitrarily deep recursion on forwarding nodes
            outer:
            for (Node<K,V>[] tab = nextTable;;){
                Node<K,V> e;int n;
                if(k == null || tab == null || (n = tab.length) == 0 || (e = tabAt(tab, (n - 1) & h)) == null){
                    return null;
                }
                for(;;){
                    int eh; K ek;
                    if((eh = e.hash) == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek)))
                            ){
                        return e;
                    }
                    if(eh < 0){
                        if(e instanceof ForwardingNode){
                            tab = ((ForwardingNode<K,V>)e).nextTable;
                            continue outer;
                        }else{
                            return e.find(h, k);
                        }
                    }

                    if((e = e.next) == null){
                        return null;
                    }
                }
            }
        }
    }

    static final class TreeNode<K,V> extends Node<K,V>{

        TreeNode<K, V> parent;
        TreeNode<K, V> left;
        TreeNode<K, V> right;
        TreeNode<K, V> prev;
        boolean red;

        public TreeNode(int hash, K key, V val, Node<K, V> next, TreeNode<K, V> parent) {
            super(hash, key, val, next);
            this.parent = parent;
        }

        Node<K,V> find(int h, Object k){
            return findTreeNode(h, k, null);
        }

        final TreeNode<K,V> findTreeNode(int h, Object k, Class<?> kc){
            if(k != null){
                TreeNode<K,V> p = this;
                do{
                    int ph, dir = 0; K pk; TreeNode<K,V> q;
                    TreeNode<K,V> pl = p.left, pr = p.right;
                    if((ph = p.hash) > h){
                        p = pl;
                    }else if(ph < h){
                        p = pr;
                    }else if((pk = p.key) == k || (pk != null && k.equals(pk))){
                        return p;
                    }else if(pl == null){
                        p = pr;
                    }else if(pr == null){
                        p = pl;
                    }else if((kc != null || (kc = comparableClassFor(k)) != null &&
                            (dir = compareComparables(kc, k, pk)) != 0
                            )){
                        p = (dir < 0)?pl : pr;
                    }else if ((q = pr.findTreeNode(h, k, kc)) != null){
                        return q;
                    }else{
                        p = pl;
                    }
                }while(p != null);
            }

            return null;
        }

        public TreeNode(int hash, K key, V val, Node<K, V> next) {
            super(hash, key, val, next);
        }
    }

    static final class TreeBin<K, V> extends Node<K, V>{

        TreeNode<K,V> root;
        volatile TreeNode<K, V> first;
        volatile Thread waiter;
        volatile int lockState;
        // values for lockState
        static final int WRITER = 1; // see while holding write lock
        static final int WAITER = 2; // set when waiting for write lock
        static final int READER = 4; // increment value for setting read lock

        static int tieBreakOrder(Object a, Object b){
            int d;
            if(a == null || a == null ||
                    (d = a.getClass().getName().compareTo(b.getClass().getName())) == 0
                    ){
                d = (System.identityHashCode(a) <= System.identityHashCode(b)? -1:1);
            }
            return d;
        }

        TreeBin(TreeNode<K,V> b){
            super(TREEBIN, null, null, null);
            this.first = b;
            TreeNode<K,V> r = null;
            for(TreeNode<K,V> x = b, next = null; x != null; x = next){
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                if(r == null){
                    x.parent = null;
                    x.red = false;
                    r = x;
                }else{
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for(TreeNode<K,V> p = r;;){
                        int dir, ph;
                        K pk = p.key;
                        if((ph = p.hash) > h){
                            dir = -1;
                        }else if(ph < h){
                            dir = 1;
                        }else if((kc == null &&
                                (kc = comparableClassFor(k)) == null ||
                                (dir = compareComparables(kc, k, pk)) == 0
                            )){
                            dir = tieBreakOrder(k, pk);
                            TreeNode<K,V> xp = p;

                            if((p = (dir <= 0)? p.left : p.right) == null){
                                x.parent = xp;
                                if(dir <= 0){
                                    xp.left = x;
                                }else{
                                    xp.right = x;
                                }
                                r = balanceInsertion(r, x);
                                break;
                            }
                        }
                    }
                }

                this.root = r;
                assert checkInvariants(root);
            }
        }

        /**
         * Acquires write lock for tree restructuring
         */
        private final void lockRoot(){
            if(!U.compareAndSwapInt(this, LOCKSTATE, 0, WRITER)){
                contendedLock();
            }
        }

        /**
         * Releases write lock for tree restructuring
         */
        private final void unlockRoot(){
            lockState = 0;
        }

        /**
         * Possibly blocks awaiting root lock
         */
        private final void contendedLock(){
            boolean waiting = false;
            for(int s;;){
                if(((s = lockState) & ~WAITER) == 0){
                    if(U.compareAndSwapInt(this, LOCKSTATE, s, WRITER)){
                        if(waiting){
                            waiter = null;
                        }
                        return;
                    }
                }else if((s & WAITER) == 0){
                    if(U.compareAndSwapInt(this, LOCKSTATE, s, s | WAITER)){
                        waiting = true;
                        waiter = Thread.currentThread();
                    }
                }else if(waiting){
                    LockSupport.park(this);
                }
            }
        }


        final Node<K,V> find(int h, Object k){
            if(k != null){
                for(Node<K,V> e = first; e != null;){
                    int s; K ek;
                    if(((s = lockState) & (WAITER|WAITER)) != 0){
                        if(e.hash == h &&
                                ((ek = e.key) == k || (ek != null && k.equals(ek)))
                                ){
                           return e;
                        }
                        e = e.next;
                    }else if(U.compareAndSwapInt(this, LOCKSTATE, s, s + READER)){
                        TreeNode<K,V> r, p;
                        try {
                            p = ((r = root) == null ? null : r.findTreeNode(h,k,null));
                        }finally {
                            Thread w;
                            if(U.getAndAddInt(this, LOCKSTATE, -READER) == (READER|WAITER) && (w = waiter) != null){
                                LockSupport.unpark(w);
                            }
                        }

                    }
                }
            }
        }

        public TreeBin(int hash, K key, V val, Node<K, V> next) {
            super(hash, key, val, next);
        }

        private static Unsafe U;
        private static long LOCKSTATE;
        static {
            try{
                U = UnSafeClass.getInstance();
                Class<?> k = TreeBin.class;
                LOCKSTATE = U.objectFieldOffset(k.getDeclaredField("lockState"));
            }catch (Exception e){
                throw new Error(e);
            }
        }
    }

    static class Traverser<K,V>{
        Node<K,V>[] tab;
        Node<K,V> next;
        TableStack<K,V> stack, spare;
        int index;
        int baseIndex;
        int baseLimit;
        int baseSize;

        public Traverser(Node<K, V>[] tab, int size, int index, int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.baseIndex = this.index = index;
            this.baseLimit = limit;
            this.next = null;
        }

        final Node<K, V> advance(){
            Node<K,V> e;
            if((e = next) != null){
                e = e.next;
            }
            for(;;){
                Node<K,V>[] t; int i, n;
                if(e != null){
                    return next = e;
                }
                if(baseIndex >= baseLimit || (t = tab) == null ||
                (n = t.length) <= (i = index) || i < 0
                        ){
                    return next = null;
                }
                if((e = tabAt(t, i)) != null && e.hash < 0){
                    if(e instanceof ForwardingNode){
                        tab = ((ForwardingNode<K, V>)e).nextTable;
                        e = null;
                        pushState(t, i, n);
                        continue;
                    }else if(e instanceof TreeBin){
                        e = ((TreeBin<K,V>)e).first;
                    }else{
                        e = null;
                    }
                }

                if(stack != null){
                    recoverState(n);
                }else if((index = i + baseSize) >= n){
                    index = ++baseIndex;
                }
            }
        }
    }

    static final class ValuesView<K,V> extends CollectionView<K,V,V> implements Collection<V>, java.io.Serializable{

        private static final long serialVersionUID = 2249069246763182397L;



        ValuesView(MyConcurrentHashMap<K, V> map) {
            super(map);
        }


        @Override
        public Iterator<V> iterator() {
            MyConcurrentHashMap<K, V> m = map;
            Node<K, V>[] t;
            int f = (t = m.table) == null ? 0 : t.length;
            return new ValuesView<K,V>(t, f, 0, f, m);
        }

        public boolean add(V v) {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return map.containsValue(o);
        }

        @Override
        public boolean remove(Object o) {
            if(o != null){
                for(Iterator<V> it = iterator(); it.hasNext();){
                    if(o.equals(it.next())){
                        it.remove();
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean containsAll(Collection<?> c) {
            return false;
        }

        public boolean addAll(Collection<? extends V> c) {
            return false;
        }
    }


    static class Segment<K, V> extends ReentrantLock implements Serializable{
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;
        Segment(float lf) {
            this.loadFactor = lf;
        }
    }

    @sun.misc.Contended static final class CounterCell {
        volatile long value;
        CounterCell(long x) { value = x; }
    }

    abstract static class CollectionView<K, V, E> implements Collection<E>, java.io.Serializable{

        private static final long serialVersionUID = 7249069246763182397L;
        MyConcurrentHashMap<K, V> map;
        CollectionView(MyConcurrentHashMap<K, V> map) { this.map = map; }

        public MyConcurrentHashMap<K, V> getMap(){
            return map;
        }

        public void clear(){
            map.clear();
        }

        public int size(){
            return map.size();
        }

        public boolean isEmpty(){
            return map.isEmpty();
        }

        public abstract Iterator<E> iterator();
        public abstract boolean contains(Object o);
        public abstract boolean remove(Object o);

        private static final String oomeMsg = "Required array size too large";

        public final Object[] toArray(){
            long sz = map.mappingCount();
            if(sz > MAX_ARRAY_SIZE){
                throw new OutOfMemoryError(oomeMsg);
            }
            int n = (int)sz;
            Object[] r = new Object[n];
            int i = 0;
            for(E e : this){
                if(i == n){
                    if(n >= MAX_ARRAY_SIZE){
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    if(n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1){
                        n = MAX_ARRAY_SIZE;
                    }else{
                        n += (n >>> 1) + 1;
                    }
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = e;
            }

            return (i == n)? r : Arrays.copyOf(r, i);
        }

        public final <T> T[] toArray(T[] a){
            long sz = map.mappingCount();
            if(sz > MAX_ARRAY_SIZE){
                throw new OutOfMemoryError(oomeMsg);
            }
            int m = (int)sz;
            T[] r = (a.length >= m)? a :
                    (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), m);
            int n = r.length;
            int i = 0;
            for(E e : this){
                if(i == n){
                    if(n >= MAX_ARRAY_SIZE){
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    if(n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1){
                        n = MAX_ARRAY_SIZE;
                    }else{
                        n += (n >>> 1) + 1;
                    }
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = (T)e;
            }

            if(a == r && i < n){
                r[i] = null;
                return r;
            }

            return (i == n) ? r : Arrays.copyOf(r, i);
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator<E> it = iterator();
            if (it.hasNext()) {
                for (;;) {
                    Object e = it.next();
                    sb.append(e == this ? "(this Collection)" : e);
                    if (!it.hasNext())
                        break;
                    sb.append(',').append(' ');
                }
            }
            return sb.append(']').toString();
        }


        public final boolean containAll(Collection<?> c){
            if(c != this){
                for(Object e : c){
                    if(e == null || !contains(e)){
                        return false;
                    }
                }
            }
            return true;
        }

        public final boolean removeAll(Collection<?> c){
            if(c == null){
                throw new NullPointerException();
            }
            boolean modified = false;
            for(Iterator<E> it = iterator(); it.hasNext();){
                if(c.contains(it.next())){
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        public final boolean retainAll(Collection<?> c) {
            if(c == null){
                throw new NullPointerException();
            }
            boolean modified = false;
            for(Iterator<E> it = iterator(); it.hasNext();){
                if(!c.contains(it.next())){
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

    }


    // Unsafe mechains
    private static Unsafe U;
    private static long SIZECTL;
    private static long TRANSFERINDEX;
    private static long BASECOUNT;
    private static long CELLSBUSY;
    private static long CELLVALUE;
    private static long ABASE;
    private static long ASHIFT;

    static {

        try{
            U = UnSafeClass.getInstance();
            Class<?> k = MyConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset(k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset(k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset(k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset(k.getDeclaredField("cellsBusy"));
            Class<?> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset(ck.getDeclaredField("value"));
            Class<?> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if((scale & (scale - 1)) != 0){
                throw new Error("data type scale not a power of two");
            }
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);

        }catch (Exception e){
            throw new Error(e);
        }
    }
}
