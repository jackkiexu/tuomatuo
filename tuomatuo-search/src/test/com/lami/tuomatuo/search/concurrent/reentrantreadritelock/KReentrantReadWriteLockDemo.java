package com.lami.tuomatuo.search.concurrent.reentrantreadritelock;

import com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock.KReentrantReadWriteLock;

/**
 * Created by xujiankang on 2017/2/3.
 */
public class KReentrantReadWriteLockDemo {

    public static void main(String[] args) throws Exception{
        KReentrantReadWriteLock rrwLock = new KReentrantReadWriteLock();
        ReadThread rt1 = new ReadThread("rt1", rrwLock);
        ReadThread rt2 = new ReadThread("rt2", rrwLock);
        WriteThread wt1 = new WriteThread("wt1", rrwLock);

        wt1.start();
        rt1.start();
        Thread.sleep(10 * 1000);
        rt2.start();
//        wt1.start();
    }

}
