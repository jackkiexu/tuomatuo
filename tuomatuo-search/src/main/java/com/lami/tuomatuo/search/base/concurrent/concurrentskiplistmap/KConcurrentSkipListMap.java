package com.lami.tuomatuo.search.base.concurrent.concurrentskiplistmap;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

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
 *     asynchronous nature of these maps, determining the current number
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
 *     <em>not</em> permit the use of {@code null} keys or values because some
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

    /**
     * This class implements a tree-like two dimensionally linked skip
     * list in which the index levels are represented in separate
     * nodes from the base nodes holding data. There are two reasons
     * for taking this approach instead of the usual array-based
     * structure:
     * 1) Array based implementations seem to encounter
     * more complexity and overhead
     * 2) We can use cheaper algorithms for the heavily-traversed index lists than can be used for the
     * base lists.
     * Here's picture of some of the basics for a possible list with 2 levels of index
     *
     * Head nodes          Index nodes
     * +-+    right        +-+                      +-+
     * |2|---------------->| |--------------------->| |->null
     * +-+                 +-+                      +-+
     *  | down              |                        |
     *  v                   v                        v
     * +-+            +-+  +-+       +-+            +-+       +-+
     * |1|----------->| |->| |------>| |----------->| |------>| |->null
     * +-+            +-+  +-+       +-+            +-+       +-+
     *  v              |    |         |              |         |
     * Nodes  next     v    v         v              v         v
     * +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+
     * | |->|A|->|B|->|C|->|D|->|E|->|F|->|G|->|H|->|I|->|J|->|K|->null
     * +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+  +-+
     *
     * The base lists use a variant of the HM linked ordered set
     * algorithm. See Tim Harris, "A pragmatic implementation of
     * non-blocking linked lists"
     *
     * http://wwwcl.cam.ac.uk/~tlh20/publications.html and maged
     * Michael "High Performance Dynamic Lock-Free Hash Tables and
     * List-Based Sets"
     *
     * http://www.research.ibm.com/people/m/michael/pubs.htm. The
     * basic idea in there lists is to mark the "next" pointers of
     * deleted nodes when deleting t oavoid conflicts with concurrent
     * insertions, and when traversion to keep track of triples
     * (predecessor, node, succor) in order to detect when and how
     * to unlink these deleted nodes
     *
     * Rather than using mark-bits to mark list deletions (which can
     * be slow and space-intensive using AtomicMarkedReference), nodes
     * use diect CAS's able next pointers. On deletion, instead of
     * marking a pointer, they splice in another node that can be
     * thought of as standing for a marked pointer (indicating this by
     * using otherwise impossible field values Using plain nodes
     * acts roughly like "boxed" implementations of marked pointers
     * but uses new nodex only when nodes are deleted, not for every
     * link. This requires less space and supports faster
     * traversal. Even if marked references were better supported by
     * JVMs, traversal using this trchnique might still be faster
     * because any search need only read ahead one more node than
     * otherwise required (to check for trailing marker) rather than
     * unmasking mark bits or whatever on each read
     *
     * This approach maintains the essential property needed in the HM
     * algorithm of changing the next-pointer of a deleted node so
     * that any other CAS of it will fail, but implements the idea by
     * changing the pointer to point to a different node, not by
     * marking it. While it would be possible to further squeeze
     * space by defining marker nodes not to have key/value fields. it
     * isn't worth the extra type-testing overhead. The deletion
     * markers are rarely encountered duringtraversal and are
     * normally quickly garbage collected. (Note that this technique
     * would not work well in systems without garbage collection)
     *
     * In addition to using deletion marker. the lists also use
     * nullness of value fields to indicate deletion, in a style
     * similar to typical lazy-deletion schemes. If a node's value is
     * null, then it is considered logically deleted and ignored even
     * though it is still reachable. This maintains proper control of
     * concurrent replace vs delete operations -- an attempted replace
     * must fail if a delete beat it by nulling field, and a delete
     * must return the last non-null value held in the field. (Note:
     * Null, rather than some special marker, is used for value fields
     * here because it just so happens to mesh with the Map API
     * requirement that method get returns null if there is no
     * mapping, which allows nodes to remain concurrently readable
     * even when deleted. Using any other marker value here would be
     * messy at best.)
     *
     * Here's the sequence of the events for a deletion of node n with
     * predecessor b and successor f, initially:
     *
     *        +------+       +------+      +------+
     *   ...  |   b  |------>|   n  |----->|   f  | ...
     *        +------+       +------+      +------+
     * 1. CAS n's value field from non-null to null
     *    from this point on, no public operations encountering
     *    the node consider this mapping to exist. However. other
     *    ongoing insertions and deletion might still modify
     *    n's next pointer
     *
     * 2.CAS n's next pointer to point to a new marker node.
     *   From this point on, no other nodes can be appended to
     *   n, which avoids deletion errors in CAS-based linked lists.
     *
     *        +------+       +------+      +------+       +------+
     *   ...  |   b  |------>|   n  |----->|marker|------>|   f  | ...
     *        +------+       +------+      +------+       +------+
     *
     * 3. CAS b's next pointer over both a and its marker
     *    From this point on , on new traversals will encounter n,
     *    and it can eventually be GCed
     *        +------+                                    +------+
     *   ...  |   b  |----------------------------------->|   f  | ...
     *        +------+                                    +------+
     * A failure at step a leads to simple retry due to a lost race
     * with another operation. Step 2-3 can fail because some other
     * thread noticed during a traversal a node with null value and
     * helped out by marking and/or unlinking. This helping-out
     * ensures that no thread can become stuck waiting for progress of
     * the deleting thread. The use of marker nodes slightly
     * complicate help-out code because traversals must track
     * consistent reads of up to four nodes (b, n, marker, f), not
     * just (b, n, f), although the next field of a marker is
     * immutable, and once a next field is CAS'ed to point to a
     * marker, it never again changes, so this requires less care
     *
     * Skip lists add indexing to this scheme, so that the base-level
     * traversal start close to the locations being found, inserted
     * or deleted -- usually base level traversals only traverse a few
     * nodes. This doesn't change the basic algorithm except for the
     * need to make sure base traversals start at predecessors (here,
     * b) that are not (structurally) deleted, otherwise retrying
     * after processing the deletion
     *
     * Index levels are maintained as lists with volatile next fields,
     * using CAS to link and unlink. Races are allowed in index-list
     * operations that can (rarely) fail to link in a new index node
     * or delete one. (We can't do this of course for data nodes)
     * However, even when this happens, the index lists remain sorted,
     * so correctly serve as indices. This can impact performance,
     * but since skip lists are probabilistic anyway, that net result
     * is that under contention, the effective "p" value may be lower
     * than its nominal value. And race windows are kept smalll enough
     * that in practice these failure are rare. even under a lot of
     * contention.
     *
     * The fact that retries (for both base and index lists) are
     * relatively cheap due to indexing allows some minor
     * simplification of retry logic. Traversal restart are
     * performed after most "helping-out" CASes. This isn't always
     * strictly necessary. but the implicit backoffs tend to help
     * reduce other downstream failed CAS's enough to outweigh restart
     * cost. This worsens the worst case, but seems to improve even
     * highly contended case.
     *
     * Unlike most skip-list implementations, index insertion and
     * deletion here require a separate traversal pass occuring after
     * the base-level action, to add or remove index nodes. This adds
     * to single-thread overhead, but improves contented
     * multithreaded performance by narrowing interference windows,
     * and allows deletion to ensure that all index nodes will be made
     * unreachable upon return from a publi remove operation, thus
     * avoiding unwanted garbage retention. This is more inportant
     * here than in some other data structures because we cannot null
     * out node fields referencing user keys since they might still be
     * read by other ongoing traversals
     *
     * Indexing uses skip list parameters that maintain good search
     * performance while using sparser-than-usual indices: The
     * hardwired parameters k=1, p=0.5 (see method doPut) mean
     * that about one-quarter of the nodes have indices. Of those that
     * do, half have one level, a quarter have two, and so on (see
     * Pugh's Skip List Cookbook, sec 3.4 ). The expected total space
     * requirement for a map is slightly less than for the current
     * implementation of java.util.TreeMap
     *
     * Changing the level of the index (i.e, the height of the tree-like
     * structures) also uses CAS. The head index has initial
     * level/height of one , Creation of an index with height greater
     * than the current level adds a level to the head index by
     * CAS'ing on a new top-most head. To maintain good performance
     * afetr a lot of removals, deletion methods heuristically try to
     * reduce the height if the topmost levelsappear to be empty
     * This may encounter races in which it possible (but race) to
     * reduce and "lose" a level just as it is about to contain an
     * index (that will than never be encountered). This does no
     * structural harm, and in practice appears to be a better option
     * than allowing unrestrained groth of levels
     *
     * The code for all this is more verbose than you'd like. Most
     * operation entail locating an element (or position to insert an
     * element). The code to do this can't be nicely factored out
     * because subsequent and/or value fields which can't be returned
     * all at once, at least not without creating yet another object
     * to hold them -- creating such little objects is an especially
     * bad idea for basic internal search operations because it adds
     * to GC overhead. (This is one of the few times I've wished Java
     * had macros) Instead, some traversal code is interleaved within
     * insertion and removal operations. The control logic to handle
     * all retry conditions is sometimes twisty. Most search is
     * broken into 2 parts. findPredecessor() searches index nodes
     * only, returning a base-level predecessor of the key. findNode()
     * finishes out the base-level search. Even with this factoring
     * there is a fair amount of near-duplication of code to handle
     * variants
     *
     * To produce random values without interface across threads
     * we use within-JDK thread local random support (via the
     * "secondary seed", to avoid interference with user-level
     * ThreadLocalRandom)
     *
     * A previous version of this class wrapped non-comparable keys
     * with their comparators to emulate Comparables when using
     * comparators vs Comparables. However, JVMs now appear to better
     * handle infusing comparator-vs-comparable choice into search
     * loops. Static method cpr (comparator, x, y) is used for all
     * comparsions, which woeks well as long as the comparator
     * argument is set up outside of loops (thus sometimes passed as
     * an argument to internal methods) to avoid field re-reads
     *
     * For explanation of algorithms sharing at least a couple of
     * features with this one, see Mikhail Fomitchev's thesis
     * (http://www.cs.yorku.ca/~mikhail/), Keir Fraser's thesis
     * (http://www.cl.cam.ac.uk/users/kaf24/), and Hakan Sundell's
     * thesis (http://www.cs.chalmers.se/phs/)
     *
     * Given the use of tree-like index nodes, you might wonder why
     * this doesn't use some kind of search tree instead, which would
     * support somewhat faster search operations, The reason is that
     * there are no known efficient lock-free insertion and deletion
     * algorithms for search trees. The immutability of the "down"
     * links of the index nodes (as opposed to mutable "left" fields in
     * true trees) makes this tractable using only CAS operations
     *
     * Notation guide for local variables
     * Node:        b, n, f     for predecessor, node, successor
     * Index:       q, r, d     for another node, right, down
     *              t           for another index node
     * Head:        n
     * Levels:      j
     * Keys:        k, key
     * Values:      v, value
     * Comparisons: c
     *
     */

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
    private transient KeySet<K> keySet;
    /** Lazily initialized entry set */
    private transient EntrySet<K, V> entrySet;
    /** Lazily initialized values collection */
    private transient Values<V> values;
    /** Lazily initialized descending key set */
    private transient ConcurrentNavigableMap<K, V> descendingMap;


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

    /* ------------------------------------ Comparison utilities ------------------------------- */

    /**
     * Compares using comparator or natural ordering if null
     * Called only by methods that have performed required type checks
     */
    static final int cpr(Comparator c, Object x, Object y){
        return (c != null) ? c.compare(x, y) : ((Comparable)x).compareTo(y);
    }


    /* ------------------------------------ Traversal ------------------------------------------ */

    /**
     * Returns a base-level node with key strictly less than given key,
     * or the base-level header if there is no such node. Also
     * unlinks indexes to deleted nodes found along the way. Callers
     * rely on this side-effect of clearing indices to deleted nodes
     * @param key the key
     * @return a predecessor of the key
     */
    private Node<K, V> findPredecessor(Object key, Comparator<? super K> cmp){
        if(key == null)
            throw new NullPointerException(); // don't postpone errors
        for(;;){
            for(Index<K, V> q = head, r = q.right, d;;){
                if(r != null){
                    Node<K, V> n = r.node;
                    K k = n.key;
                    if(n.value == null){
                        if(!q.unlink(r)){
                            break; // restart
                        }
                        r = q.right; //reread r
                        continue;
                    }

                    if(cpr(cmp, key, k) > 0){
                        q = r;
                        r = r.right;
                        continue;
                    }
                }

                if((d = q.down) == null){
                    return q.node;
                }

                q = d;
                r = d.right;
            }
        }
    }

    /**
     * Returns node holding key or null if no such, clearing out any
     * deleted nodes seen along the way. Repeatedly traverses at
     * base-level looking for key staring at predecessor returned
     * from findPredecessor, processing base-level deletions as
     * encountered. Some callers rely on this side-effect of clearing
     * deleted nodes
     *
     * Restarts occur, at traversal step centered on node n, if:
     *
     *  (1) After reading n's next field, n is no longer assumed
     *      predecessor b's current successor, which means that
     *      we don't have a consistent 3-node snapshot and so cannot
     *      unlink any subsequent deleted nodes encountered
     *
     *  (2) n's value field is null, indicating n is deleted, in
     *      which case we help out an ongoing structural deletion
     *      before retrying. Event though there are cases where such
     *      unlinking doesn't require restart, they aren't sorted out
     *      here because doing so would not usually outweight cost of
     *      restarting
     *
     *  (3) n is a marker or n's predecessor's value field is null,
     *      indicating (among other possibilities) that
     *      findPredecessor returned a deleted node. We can't unlink
     *      the node because we don't know its predecessor, so rely
     *      on another call to findPredecessor to notice and return
     *      some earlier predecessor, which is will do. this check is
     *      only strictly needed at begining of loop. (and the
     *      b.value check isn't strictly needed at all) but is done
     *      each iteration to help avoid contention with other
     *      threads by callers that will fail to be able to change
     *      links, and so will retry anyway
     *
     *  The traversal loops in doPost, doRemove, and findNear all
     *  include the same three kinds of checks, And specialized
     *  versions appear in findFirst, and findLast and their
     *  variants. They can't easily share code because each uses the
     *  reads of fields held in locals occurring in the orders they
     *  were performed
     *
     * @param key the key
     * @return node holding key, or null if no such
     */
    private Node<K, V> findNode(Object key){
        if(key == null){
            throw new NullPointerException(); // don't postpone errors
        }

        Comparator<? super K> cmp = comparator;
        outer:
        for(;;){
            for(Node<K, V> b = findPredecessor(key, cmp), n = b.next;;){
                Object v; int c;
                if(n == null){
                    break outer;
                }
                Node<K, V> f = n.next;
                if(n != b.next){ // inconsistent read
                    break ;
                }
                if((v = n.value) == null){ // n is deleted
                    n.helpDelete(b, f);
                    break ;
                }
                if(b.value == null || v == n){ // b is deleted
                    break ;
                }
                if((c = cpr(cmp, key, n.key)) == 0){
                    return n;
                }
                if(c < 0){
                    break outer;
                }
                b = n;
                n = f;
            }
        }

        return null;
    }

    /**
     * Gets value for key. Almost the same as findNode, but returns
     * the found value (to avoid retires during ret-reads)
     *
     * @param key the key
     * @return the value, or null if absent
     */
    private V doGet(Object key){
        if(key == null){
            throw new NullPointerException();
        }
        Comparator<? super K> cmp = comparator;
        outer:
        for(;;){
            for(Node<K, V> b = findPredecessor(key, cmp), n = b.next;;){
                Object v; int c;
                if(n == null){
                    break outer;
                }
                Node<K, V> f = n.next;
                if(n != b.next){ // inconsistent read
                    break ;
                }
                if((v = n.value) == null){ // n is deleted
                    n.helpDelete(b, f);
                    break ;
                }
                if(b.value == null || v == n){ // b is deleted
                    break ;
                }
                if((c = cpr(cmp, key, n.key)) == 0){
                    V vv = (V) v;
                    return vv;
                }
                if(c < 0){
                    break outer;
                }
                b = n;
                n = f;
            }
        }

        return null;
    }

    /* ------------------------------------- Insertion ----------------------------------------- */

    /**
     * Main insetion method. Adds element if not present, or
     * replaces value if present and onlyIfAbsent is false.
     *
     * @param key the key
     * @param value the values that must be associated with key
     * @param onlyIfAbstsent if should not insert if already present
     * @return the old value, or null if newly inserted
     */
    private V doPut(K key, V value, boolean onlyIfAbstsent){
        Node<K, V> z; // adde node
        if(key == null){
            throw new NullPointerException();
        }
        Comparator<? super K> cmp = comparator;
        outer:
        for(;;){
            for(Node<K, V> b = findPredecessor(key, cmp), n = b.next;;){
                if(n != null){
                    Object v; int c;
                    Node<K, V> f = n.next;
                    if(n != b.next){ // inconsistent read
                        break ;
                    }
                    if((v = n.value) == null){
                        n.helpDelete(b, f);
                        break ;
                    }

                    if(b.value == null || v == n){ // b is deleted
                        break ;
                    }
                    if((c = cpr(cmp, key, n.key)) > 0){
                        b = n;
                        n = f;
                        continue ;
                    }
                    if(c == 0){
                        if(onlyIfAbstsent || n.casValue(v, value)){
                            V vv = (V) v;
                            return vv;
                        }
                        break ; // restart if lost race to replace value
                    }
                    // else c < 0; fall through
                }

                z = new Node<K, V> (key, value, n);
                if(!b.casNext(n, z)){
                    break ; // restart if lost race to append to b
                }
                break outer;
            }
        }

        int rnd = ThreadLocalRandom.nextSecondarySeed();
        if((rnd & 0x80000001) == 0){ // test hightest and lowest bits
            int level = 1, max;
            while(((rnd >>>= 1) & 1) != 0){
                ++level;
            }
            Index<K, V> idx = null;
            HeadIndex<K, V> h = head;
            if(level <= (max = h.level)){
                for(int i = 1; i < level; ++i){
                    idx = new Index<K, V>(z, idx, null);
                }
            }
            else{ // try to grow by one level
                level = max + 1; // hold in array and later pick the one to use
                Index<K, V>[] idxs =
                        (Index<K, V>[])new Index<?, ?>[level + 1];
                for(int i = 1; i <= level; ++i){
                    idxs[i] = idx = new Index<K, V>(z, idx, null);
                }
                for(;;){
                    h = head;
                    int oldLevel = h.level;
                    if(level <= oldLevel){ // lost race to add level
                        break;
                    }
                    HeadIndex<K, V> newh = h;
                    Node<K, V> oldbase = h.node;
                    for(int j = oldLevel+1; j <= level; ++j){
                        newh = new HeadIndex<K, V>(oldbase, newh, idxs[j], j);
                    }
                    if(casHead(h, newh)){
                        h = newh;
                        idx = idxs[level = oldLevel];
                        break;
                    }
                }
            }

            // find insertion points and splice in
            splice:
            for(int insertionLevel = level;;){
                int j = h.level;
                for(Index<K, V> q = h, r = q.right, t = idx;;){
                    if(q == null || t == null){
                        break splice;
                    }
                    if(r != null){
                        Node<K, V> n = r.node;
                        // compare before deletion check avoids needing recheck
                        int c = cpr(cmp, key, n.key);
                        if(n.value == null){
                            if(!q.unlink(r)){
                                break ;
                            }
                            r = q.right;
                            continue ;
                        }

                        if(c > 0){
                            q = r;
                            r = r.right;
                            continue ;
                        }
                    }

                    if(j == insertionLevel){
                        if(!q.link(r, t)){
                            break ; // restrt
                        }
                        if(t.node.value == null){
                            findNode(key);
                            break splice;
                        }
                        if(--insertionLevel == 0){
                            break splice;
                        }
                    }

                    if(--j >= insertionLevel && j < level){
                        t = t.down;
                    }
                    q = q.down;
                    r = q.right;

                }
            }

        }


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

    // Factory methods for iterators need by KConcurrentSkipListSet etc
    Iterator<V> keyIterator(){
        return null;
    }

    Iterator<V> valueIterator(){
        return new ValueIterator();
    }

    Iterator<Map.Entry<K, V>> entryIterator(){
        return new EntryIterator();
    }

    /* ----------------------- Deletion ------------------------- */

    /**
     * Main deletion method. Locates node, nulls value, appends a
     * deletion marker, unlinks predecessor, removes associated index
     * nodes, and possibly reduces head index level
     *
     * Index nodes are cleared out simply by calling findPredecessor.
     * which unlinks indexes to deleted nodes found along path to key,
     * which will include the indexes to this node. This is node
     * unconditionally. We can't check beforehand whether there are
     * indexes hadn't been inserted yet for this node during initial
     * search for it, and we'd like to ensure lack of garbage
     * retention, so must call to be sure
     *
     * @param key the key
     * @param value if non-null, the value that must be
     *              associated with key
     * @return the node, or null if not found
     */
    final V doRemove(Object key, Object value){
        if(key == null){
            throw new NullPointerException();
        }
        Comparator<? super K> cmp = comparator;
        outer:
        for(;;){
            for(Node<K, V> b = findPredecessor(key, cmp), n = b.next;;){
                Object v; int c;
                if(n == null){
                    break outer;
                }
                Node<K, V> f = n.next;
                if(n != b.next){ // inconsistent read
                    break ;
                }
                if((v = n.value) == null){ // n is deleted
                    n.helpDelete(b, f);
                    break ;
                }

                if(b.value == null || v == n){ // b is deleted
                    break ;
                }
                if((c = cpr(cmp, key, n.key)) < 0){
                    break outer;
                }

                if(c > 0){
                    b = n;
                    n = f;
                    continue ;
                }

                if(value != null && !value.equals(v)){
                    break outer;
                }
                if(!n.casValue(v, null)){
                    break ;
                }
                if(!n.appendMarker(f) || !b.casNext(n, f)){
                    findNode(key); // retry via findNode
                }
                else{
                    findPredecessor(key, cmp); // clean index
                    if(head.right == null){
                        tryReduceLevel();
                    }
                }

                V vv = (V) v;
                return vv;

            }
        }

        return null;
    }

    /**
     * compareAndSet head node
     */
    private boolean casHead(HeadIndex<K, V> cmp, HeadIndex<K, V> val){
        return unsafe.compareAndSwapObject(this, headOffset, cmp, val);
    }

    /**
     * Possibly reduce head level if it has no nodes. This method can
     * (rarely) make mistakes, in which case levels can disappear even
     * though they are about to contain index nodes. This impatcs
     * performance, not correctness. To minimize mistakes as well as
     * to reduce hysteresis, the level is reduced by one only if the
     * topmost three levels look empty. Also, if the removed level
     * looks non-empty after CAS, we try to change it back quick
     * before anyone notices our mistake! (This trick works pretty
     * well because this method will practically never make mistakes
     * unless current thread stalls immediately before first CAS, in
     * which case it is very unlikely to stall again immediately
     * afterwards, so will recover)
     *
     * We put up with all this rather than just let levels grow
     * because otherwise, even a small map that has undergone a large
     * number of insertions and removals will have a lot of levels
     * slowing down access more than would an occasional unwanted
     * reduction
     *
     */
    private void tryReduceLevel(){
        HeadIndex<K, V> h = head;
        HeadIndex<K, V> d;
        HeadIndex<K, V> e;
        if(h.level > 3 &&
                (d = (HeadIndex<K, V>)h.down) != null &&
                (e = (HeadIndex<K, V>)d.down) != null &&
                e.right == null &&
                d.right == null &&
                h.right == null &&
                casHead(h, d) && // try to set
                h.right == null
                ){
            casHead(d, h); // try to backout
        }
    }


    /* ------------------- Finding and removing first element ----------------- */

    /**
     * Specified variant of findNode to get first valid node.
     * @return first node or null if empty
     */
    final Node<K, V> findFirst(){
        for(Node<K, V> b, n;;){
            if((n = (b = head.node).next) == null){
                return null;
            }
            if(n.value != null){
                return n;
            }
            n.helpDelete(b, n.next);
        }
    }

    /**
     * Clears out index nodes associated with deleted first entry
     */
    private void clearIndexToFirst(){
        for(;;){
            for(Index<K, V> q = head;;){
                Index<K, V> r = q.right;
                if(r != null && r.indexesDeletedNode() && !q.unlink(r)){
                    break;
                }
                if((q = q.down) == null){
                    tryReduceLevel();
                }
                return;
            }
        }
    }

    /**
     * Removes first entry; returns its snapshots
     * @return null if empty, else snapshot of first entry
     */
    private Map.Entry<K, V> doRemoveFirstEntry(){
        for(Node<K, V> b, n;;){
            if((n = (b = head.node).next) == null){
                return null;
            }
            Node<K, V> f = n.next;
            if(n != b.next){
                continue;
            }
            Object v = n.value;
            if(v == null){
                n.helpDelete(b, f);
                continue;
            }
            if(!n.casValue(v, null)){
                continue;
            }
            if(!n.appendMarker(f) || !b.casNext(n, f)){
                findFirst();
            }
            clearIndexToFirst();
            V vv = (V) v;
            return new AbstractMap.SimpleImmutableEntry<K, V>(n.key, vv);
        }
    }



    /* ----------------------------- Serialization ------------------ */
    /**
     * Saves this map to a stream (that is, serializes it ).
     */

    /**
     *
     * @param s the stream
     * @throws IOException if an I/O error occurs
     * @serialData The key (Object) and value (Object) for each
     * key-value mapping represented by the mappings are emitted in kay-order
     * (as determined by the Comparator, or by the key's natural
     * ordering if no Comparator)
     */
    private void writeObject(ObjectOutputStream s) throws IOException{
        // Write out the Comparator and any hidden stuff
        s.defaultWriteObject();

        // Write out keys and values (alternating)

        for(Node<K, V> n = findFirst(); n != null; n = n.next){
            V v = n.getValidValue();
            if(v != null){
                s.writeObject(n.key);
                s.writeObject(v);
            }
        }
        s.writeObject(null);
    }




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

    final class ValueIterator extends Iter<V>{
        public V next(){
            V v = nextValue;
            advance();
            return v;
        }
    }

    final class KeyIterator extends Iter<K>{
        public K next(){
            Node<K, V> n = next;
            advance();
            return n.key;
        }
    }

    final class EntryIterator extends Iter<Map.Entry<K, V>>{
        public Map.Entry<K, V> next(){
            Node<K, V> n = next;
            V v = nextValue;
            advance();
            return new AbstractMap.SimpleImmutableEntry<K, V>(n.key, v);
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
    /* ----------------------------- View Classes ------------------- */
    /**
     * View classes are static, delegating to a ConcurrentNavigableMap
     * to allow use by SubMaps, which outweighs the ugliness of
     * needing type-tests for Iterator methods
     */

    static final <E> List<E> toList(Collection<E> c){
        // Using size() here would be a pessimization
        ArrayList<E> list = new ArrayList<E>();
        for(E e : c){
            list.add(e);
        }
        return list;
    }

    static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E>{
        final ConcurrentNavigableMap<E, ?> m;
        KeySet(ConcurrentNavigableMap<E, ?> map) { m = map; }
        public int size() { return m.size(); }
        public boolean isEmpty() { return m.isEmpty(); }
        public boolean contains(Object o) { return m.containsKey(o); }
        public boolean remove(Object o) { return m.remove(o) != null; }
        public void clear(){
            m.clear();
        }
        public E lower(E e){
            return m.lowerKey(e);
        }

        public E floor(E e){
            return m.floorKey(e);
        }

        public E ceiling(E e){
            return m.ceilingKey(e);
        }

        public E higher(E e){
            return m.higherKey(e);
        }

        public Comparator<? super E> comparator(){
            return m.comparator();
        }

        public E pollFirst(){
            Map.Entry<E, ?> e = m.pollFirstEntry();
            return (e == null) ? null : e.getKey();
        }

        public E pollLast(){
            Map.Entry<E, ?> e = m.pollLastEntry();
            return (e == null)? null : e.getKey();
        }

        public Iterator<E> iterator(){
            if(m instanceof KConcurrentSkipListMap){
                return ((KConcurrentSkipListMap<E, Object>)m).keyIterator();
            }
            return null;
        }
    }

    /**
     *
     * @param <K>
     * @param <V>
     */
    static final class SubMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, Serializable{

        @Override
        public Set<Entry<K, V>> entrySet() {
            return null;
        }

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
        public NavigableSet<K> descendingKeySet() {
            return null;
        }

        @Override
        public V getOrDefault(Object key, V defaultValue) {
            return null;
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {

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
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {

        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            return null;
        }

        @Override
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return null;
        }

        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return null;
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            return null;
        }
    }


    static final class EntrySet<K1, V1> extends AbstractSet<Map.Entry<K1, V1>>{
        final ConcurrentNavigableMap<K1, V1> m;

        public EntrySet(ConcurrentNavigableMap<K1, V1> m) {
            this.m = m;
        }

        @Override
        public Iterator<Entry<K1, V1>> iterator() {
            if(m instanceof KConcurrentSkipListMap){
                return ((KConcurrentSkipListMap<K1, V1>)m).entryIterator();
            }else{
                return ((SubMap<K1, V1>)m).entryIterator();
            }
        }

        @Override
        public int size() {
            return 0;
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
