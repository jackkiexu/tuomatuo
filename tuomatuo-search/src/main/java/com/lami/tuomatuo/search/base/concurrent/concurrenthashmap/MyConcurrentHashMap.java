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
        }catch (Exception e){
            throw new Error(e);
        }
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
}
