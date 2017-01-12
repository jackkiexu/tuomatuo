package com.lami.tuomatuo.search.base.concurrent.threadlocal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provides thread-local variables. These variables differ from
 * their normal counterparts in that each thread that access one (via its
 * {@code get} or {@code set} method) has its own.idependently initialized
 * copy of the variable. {@code ThreadLocal} instances are typically private
 * static fields in classes that wish to associate state with a thread (e.g,
 * a user ID or Transaction ID).
 *
 * <p>
 *     For example, the class below generates unique identifiers local to each
 *     thread
 *     A thread's id is assigned the first time it invokes {@code ThreadId.get()}
 *     and remains unchanged on subsequent calls
 * </p>
 *
 *  public class ThreadId{
 *      // Atomic Integer containing the next threadID to be assigned
 *      private static final AtomicInteger nextid = new AtomicInteger(0);
 *
 *      // Thread local variable containing each thread's ID
 *      private static final ThreadLocal
 *  }
 *
 * <p>
 *     Each thread holds an implicit reference to its copy of a thread-local
 *     variable as long as the thread is alive and {@code KThreadlocal}
 *     instance is accessible: after a thread goes aways, all of its copies of
 *     thread-local instances are subject to garbage collection (unless other
 *     references to these copies exist)
 * </p>
 *
 * Created by xujiankang on 2017/1/6.
 */
public class KThreadLocal<T> {

    /**
     * ThreadLocals reply on pre-thread linear-probe hash maps attached
     * to each thread (Thread.threadLocals and inheritableThreadLocals)
     * .The ThreadLocal objects act as keys
     * searched via threadLocalHashCode. This is a custom hash code.
     * (useful only within ThreadLocalMaps) that eliminates collisions
     * in the common case where consecutively constructed ThreadLocals
     * are used by the same threads, which remaining well-behaved in
     * less common cases
     */
    private final int threadLocalHashCode = nextHashCode();

    /** The next hash code to be given out, Updated atomically, Starts at zero */
    private static AtomicInteger nextHashCode = new AtomicInteger();

    /**
     * The difference between successively generated hash codes - turns
     * implicit sequential thread-local into near-optimally spread
     * multiplicative hash values for power-of-two-sized tables
     */
    private static final int HASH_INCREMENT = 0x61c88647;

    /**
     * Returns the next hash code
     * @return
     */
    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    /**
     * Returns the current thread's "initial value" for this
     * thread-local variable. This method will be invoked the first
     * time a thread access the variable with the {@link #get}
     * method, unless the thread previously invoked the {@link #set}
     * method, in which case the {@code initialValues} method will not
     * be invoked for the thread, Normally, this method is invoked at
     * most once per thread, but it may be invoked agein in case of
     * subsequent invocations of {@link #remove} followed by {@link #get}
     *
     * <p>
     *     This implementation simply returns {@code null}, if the
     *     programmer desires thread-local variables to have an initial
     *     value other than {@code null}, {@code ThreadLocal} must be
     *     subclassed, and this method overridden, Typically, an
     *     anonymous inner class will be used
     * </p>
     *
     * @return the initial value for this thread-local
     */
    protected T initialValue(){
        return null;
    }

    /**
     * Creates a thread local variable
     */
    public KThreadLocal() {
    }

    /**
     * Remove the current thread's value for this thread-local
     * variable. if this thread-local variable is subsequently
     * {@link #get read} by the current thread, its value will be
     * reinitialized by invoking its {@link #initialValue()} method,
     * unless its value is {@link #set} by the current thread
     * in the interm. This may result in multiple invocations of the
     * {@code initialValue} method in the current thread
     */
    private void remove(){
        ThreadLocalMap m = getMap(Thread.currentThread());
        if(m != null){
            m.remove(this);
        }
    }

    /**
     * Get the map associated with a ThreadLocal. Overridden in
     * InheritableThreadlocal
     * @param t
     * @return
     */
    ThreadLocalMap getMap(Thread t){
        return t.threadLocals;
    }

    /**
     * Create the map associated with a ThreadLocal Overridden in
     * InheritableThreadLocal
     * @param t
     * @param firstValue
     */
    void createMap(Thread t, T firstValue){
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }

    /**
     * Factory method to create map of inherited thread locals.
     * Designed to be called only from Thread constructor
     *
     * @param parentMap the map associated with parent thread
     * @return a map containing the parent's inheritable bindings
     */
    static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap){
        return new ThreadLocalMap(parentMap);
    }

    /**
     * Method childValue is visibly defined in subclass
     * InheritableThreadLocal, but is internally defined here for the
     * sake of providing createInheritedMap factory method without
     * needing to subclass the map class in InheritableThreadLocal
     * This technique is preferable to the alternative of embedding
     * instanceof tests in methods
     *
     * @param parentValue
     * @return
     */
    T childValue(T parentValue){
        throw new UnsupportedOperationException();
    }

    /**
     * ThreadLocalMap is a customed hash map suitable only for maintaining
     * thread local values. No operations are exported
     * outside of the ThreadLocal class. The class is package private to
     * allow declaration of fields in class Thread. To help deal with
     * vary large and long-lived usages. the hash table entries use
     * WeakReferences for keys. However, since reference queues are not
     * used, stale entries are guaranteed to be removed only when
     * the table starts running out of space
     */
    static class ThreadLocalMap{

        /**
         * The entries in this hash map extend WeakReference, using
         * its main ref field as the key (which is always a
         * ThreadLocal object). Note that null keys (i, e entry.get()
         * == null) mean that key is no longer referenced, so tha entry can be
         * expunged from table, Such entries are referred to
         * as "stale entries" in the code that follows
         */
        static class Entry extends WeakReference<KThreadLocal<?>>{
            /** The value associated with this ThreadLocal */
            Object value;

            public Entry(KThreadLocal<?> referent, Object v) {
                super(referent);
                value = v;
            }
        }

        /** The initial capacity -- MUST be power of two */
        private static final int INITIAL_CAPICATY = 16;

        /** The table, resized as necessary.
         *  table.length MUST always be a power of two
         */
        private Entry[] table;

        /** The number of entries in the table */
        private int size = 0;

        /** The next size value at which to resize */
        private int threshold; // Default to 0

        /**
         * Set the resize threshold to maintain at worst a 2/3 load factor
         * @param len
         */
        private void setThreshold(int len){
            threshold = len * 2 / 3;
        }

        /** Increment i modulo len */
        private static int nextIndex(int i, int len){
            return ((i + 1 < len) ? i + 1 : 0);
        }

        /** Decrement i modulo len */
        private static int preIndex(int i, int len){
            return ((i - 1 >= 0)? i - 1 : len - 1);
        }

        /**
         * Construct a new map initially containing (firstKey, firstvalue)
         * ThreadlocalMaps are constructed lazily, so we only create
         * one when we have at least one entry to put in it
         */
        ThreadLocalMap(KThreadLocal<?> firstKey, Object firstValue){
            table = new Entry[INITIAL_CAPICATY];
            int i = firstKey.threadLocalHashCode & (INITIAL_CAPICATY - 1);
            table[i] = new Entry(firstKey, firstValue);
            size = 1;
            setThreshold(INITIAL_CAPICATY);
        }

        /**
         * Construct a new map including all Inheritable ThreadLocals
         * from given parent map, Called only by created InheritedMap
         *
         * @param parentMap the map associated with parent thread
         */
        private ThreadLocalMap(ThreadLocalMap parentMap){
            Entry[] parentTable = parentMap.table;
            int len = parentTable.length;
            setThreshold(len);
            table = new Entry[len];

            for(int j = 0; j < len; j++){
                Entry e = parentTable[j];
                if(e != null){
                    KThreadLocal<Object> key = (KThreadLocal<Object>)e.get();
                    if(key != null){
                        Object value = key.chil
                    }
                }
            }
        }

        /**
         * Remove the entry for key
         */
        private void remove(KThreadLocal<?> key){
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len - 1);
            for(Entry e = tab[i];
                    e != null;
                    e = tab[i = nextIndex(i, len)]){
                if(e.get() == key){
                    e.clear();
                    expungeStaleEntry(i);
                    return;
                }
            }
        }

        /**
         * Replace a stale entry encountered during a set operation
         * with an entry for specified key. The value passed in
         * the value parameter is tored in the entry, whether or not
         * an entry already exist for the specified key
         *
         * As a side effect, this method expun
         *
         * @param key
         * @param value
         * @param staleSlot
         */
        private void replaceStaleEntry(KThreadLocal<?> key, Object value, int staleSlot){

        }

        /**
         * Expunge a stale entry by rehashing any possibly colliding entries
         * lying between staleSlot and the next null slot. This also expunges
         * any other stale entries encountered before the trailing null. See
         * Knuth, Section 6.4
         *
         * @param staleSlot index of slot known to have null key
         * @return index of the next null slot after staleSlot
         * (all between staleSlot and this slot will have been checked
         * for expunging)
         */
        private int expungeStaleEntry(int staleSlot){
            Entry[] tab = table;
            int len = tab.length;

            // expunge entry at staleSlot
            tab[staleSlot].value = null;
            tab[staleSlot] = null;
            size--;

            // Rehash until we encounter null
            Entry e;
            int i;
            for(i = nextIndex(staleSlot, len);
                (e = tab[i]) != null;
                i = nextIndex(i, len)){
                KThreadLocal<?> k = e.get();
                if(k == null){
                    e.value = null;
                    tab[i] = null;
                    size--;
                }else{
                    int h = k.threadLocalHashCode & (len -1);
                    if(h != i){
                        tab[i] = null;

                        /**
                         *  Unlike Knuth 6.4 Algorithm R, we must scan until
                         *  null because multiple entries could have been stale
                         */
                        while(tab[h] != null){
                            h = nextIndex(h, len);
                        }
                        tab[h] = e;
                    }
                }
            }
            return i;
        }

        /**
         * Heuristically scan some cells looking for stale entries
         * This is invoked when either a new element is added, or
         * another stale one has been expunged, It performs a
         * logarithmic numbet of scans, as a balance between no
         * scanning (fast but retains garbage) and a number of scans
         * proportional ot number of element, that would find all
         * garbage but would cause some insertions to take O(n) time
         *
         * @param i a position know NOT to hold a stale entry. The scan starts at the lement afetr i
         * @param n scan control; {@code log2(n)} cells are scanned
         *          unless a stale entry is found, in which case
         *          {@code log2(table.length) - 1} additional cells are scaned
         *          When called from insertions, this parameter is the number
         *          of elements, but when from replaceStaleEntry, it is the table
         *          length. (Note: all ths could be changed to be either
         *          more or less aggressive by weighting n changed of just
         *          using straight log n, But this version is simple is simple, fast and
         *          seems to work well)
         *
         * @return true if any stale entries have been removed
         */
        private boolean cleanSomeSlots(int i, int n){
            boolean removed = false;
            Entry[] tab = table;
            int len = tab.length;
            do{
                i = nextIndex(i, len);
                Entry e = tab[i];
                if(e != null && e.get() == null){
                    n = len;
                    removed = true;
                    i = expungeStaleEntry(i);
                }
            }while((n >>>= 1) != 0);
            return removed;
        }

        /**
         * Re-pack and/or re-size the table. First scan the entire
         * table removing stale entries. If this doesn't sufficiently
         * shrink the size of the table, double the table size
         */
        private void rehash(){
            expungeStaleEntries();
            // Use lower threshold for doubling to avoid hysteresis
            if(size >= threshold - threshold/4){
                resize();
            }
        }

        /**
         * Double the capacity of the table
         */
        private void resize(){
            Entry[] oldTab = table;
            int oldLen = oldTab.length;
            int newLen = oldLen * 2;
            Entry[] newTab = new Entry[newLen];
            int count = 0;

            for(int j = 0; j < oldLen; ++j){
                Entry e = oldTab[j];
                if(e != null){
                    KThreadLocal<?> k = e.get();
                    if(k == null){
                        e.value = null; // help the gc
                    }else{
                        int h = k.threadLocalHashCode & (newLen - 1);
                        while(newTab[h] != null){
                            h = nextIndex(h, newLen);
                        }
                        newTab[h] = e;
                        count++;
                    }
                }
            }

            setThreshold(newLen);
            size = count;
            table = newTab;
        }

        /**
         * Expunge all stale entries in the table
         */
        private void expungeStaleEntries(){
            Entry[] tab = table;
            int len = tab.length;
            for(int j = 0; j < len; j++){
                Entry e = tab[j];
                if(e != null && e.get() == null){
                    expungeStaleEntry(j);
                }
            }
        }

    }


}
