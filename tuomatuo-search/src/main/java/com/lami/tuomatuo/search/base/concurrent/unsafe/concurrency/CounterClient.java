package com.lami.tuomatuo.search.base.concurrent.unsafe.concurrency;

import com.lami.tuomatuo.search.base.concurrent.unsafe.A;
import sun.misc.Unsafe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by xjk on 2016/5/13.
 */
public class CounterClient implements Runnable{

    private Counter c;
    private int num;

    public CounterClient(Counter c, int num) {
        this.c = c;
        this.num = num;
    }

    public void run() {
        for(int i = 0; i < num; i++){
            c.increment();
        }
    }

    static class CASCounter implements Counter{

        private volatile  long counter = 0;
        private Unsafe unsafe;
        private long offset;

        public CASCounter() throws Exception{
            unsafe = A.getUnsafe();
            offset = unsafe.objectFieldOffset(CASCounter.class.getDeclaredField("counter"));
        }

        public void increment() {
            long before = counter;
            while(!unsafe.compareAndSwapLong(this, offset, before, before + 1)){
                before = counter;
            }
        }

        public long getCounter() {
            return 0;
        }
    }


    public static void main(String[] args) throws Exception{
        int NUM_OF_THREADS = 1000;
        int NUM_OF_INCREMENTS = 100000;

        ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
        Counter counter = new Counter() {
            private AtomicInteger i = new AtomicInteger(0);
            private ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();
            public   void increment() {
                i.getAndIncrement();
            }

            public long getCounter() {
                return i.get();
            }
        };

        counter = new CASCounter();

        long before = System.currentTimeMillis();
        for(int i = 0; i < NUM_OF_THREADS; i++){
            service.submit(new CounterClient(counter, NUM_OF_INCREMENTS));
        }

        service.shutdown();
        service.awaitTermination(20, TimeUnit.SECONDS);
        long after = System.currentTimeMillis();
        System.out.println("Counter result:" + counter.getCounter());
        System.out.println("The passed in ms :" + (after - before));
    }
}
