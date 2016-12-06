package com.lami.tuomatuo.core.base;

/**
 * Created by xjk on 2016/10/17.
 */
public interface Callable<K, V> {
    V call(K k) throws Exception;
}