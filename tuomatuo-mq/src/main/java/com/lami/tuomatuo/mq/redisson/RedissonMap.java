package com.lami.tuomatuo.mq.redisson;

import com.lami.tuomatuo.mq.redis.lettuce.RedisConnection;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by xjk on 9/24/16.
 */
public class RedissonMap<K, V> implements ConcurrentMap<K, V> {

    private static final Logger logger = Logger.getLogger(RedissonMap.class);

    private RedisConnection<Object, Object> connection;
    private String name;

    public String getName() {
        return name;
    }

    protected RedisConnection<Object, Object> getConnection(){
        return connection;
    }

    public int size() {
        return connection.hlen(name).intValue();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(Object key) {
        return connection.hexists(name, key);
    }

    public boolean containsValue(Object value) {
        return connection.hvals(name).contains(value);
    }

    public V get(Object key) {
        return (V)connection.hget(name, key);
    }

    public V put(K key, V value) {
        V prev = get(key);
        connection.hset(name, key, value);
        return prev;
    }

    public V remove(Object key) {
        V prev = get(key);
        connection.hdel(name, key);
        return prev;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        connection.hmset(name, (Map<Object, Object>) m);
    }

    public void clear() {
        connection.del(name);
    }

    public Set<K> keySet() {
        return new HashSet<K>((Collection<? extends K>) connection.hkeys(name));
    }

    public Collection<V> values() {
        return (Collection<V>) connection.hvals(name);
    }

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
}
