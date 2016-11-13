package com.lami.tuomatuo.search.base.concurrent.Semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

/**
 * Created by xjk on 11/12/16.
 */
public class ObjectCache<T> {

    interface ObjectFactory<T>{
        T makeObject();
    }

    private Semaphore semaphore;
    private int capacity ;
    private ObjectFactory<T> factory;
    private Lock lock;



}
