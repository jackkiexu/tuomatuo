package com.lami.tuomatuo.search.base.concurrent.concurrentskiplistmap;

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
 *     Additionally, the bulk operation
 * </p>
 *
 *
 * Created by xujiankang on 2016/12/21.
 */
public class KConcurrentSkipListMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -8627078645895051609L;

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
}
