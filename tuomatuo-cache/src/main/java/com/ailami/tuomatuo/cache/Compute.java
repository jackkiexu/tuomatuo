package com.ailami.tuomatuo.cache;

/**
 * Created by xjk on 2016/12/6.
 */
public interface Compute<K, V> {

    V compute(K key) throws Exception;

}
