package com.lami.tuomatuo.mq.jafka.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xjk on 2016/9/30.
 */
public class Pool<K, V> implements Map<K, V> {

    private ConcurrentHashMap<K, V> pool = new ConcurrentHashMap<K, V>();

    public int size() {
        return pool.size();
    }

    public boolean isEmpty() {
        return pool.isEmpty();
    }

    public boolean containsKey(Object key) {
        return pool.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return pool.containsValue(value);
    }

    public V get(Object key) {
        return pool.get(key);
    }

    public V put(K key, V value) {
        return pool.put(key, value);
    }

    public V remove(Object key) {
        return pool.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        pool.putAll(m);
    }

    public V putIfNotExists(K key,V value) {
        return pool.putIfAbsent(key, value);
    }

    public void clear() {
        pool.clear();
    }

    public Set<K> keySet() {
        return pool.keySet();
    }

    public Collection<V> values() {
        return pool.values();
    }

    public Set<Entry<K, V>> entrySet() {
        return pool.entrySet();
    }
}
