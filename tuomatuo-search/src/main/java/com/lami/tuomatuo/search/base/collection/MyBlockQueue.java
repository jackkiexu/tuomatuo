package com.lami.tuomatuo.search.base.collection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xujiankang on 2016/5/4.
 */
public class MyBlockQueue {

    public static void main(String[] args) {

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(1024);
        ReentrantLock reentrantLock = new ReentrantLock();
    }

}
