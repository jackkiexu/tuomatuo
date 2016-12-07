package com.lami.tuomatuo.cache.guava;

/**
 * Created by xjk on 2016/12/7.
 */
public interface LocalCache<K, V> {
    V get(K key);
}
