package com.lami.tuomatuo.cache;

/**
 * Created by xjk on 2016/10/13.
 */
public interface Computable<K, V> {

    V compute(K k) throws InterruptedException ;

}
