package com.lami.tuomatuo.search.base.concurrent;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.LockSupport;

/**
 * Created by xjk on 2016/5/11.
 */
public class BoundedBuffer1 {

    private static final Logger logger = Logger.getLogger(BoundedBuffer1.class);

    private int contents;
    final Object[] items = new Object[100];
    int puptr, takeptr, count;

    public synchronized void put(Object x){
        while (count == items.length){
            try{
                wait();
            }catch (InterruptedException e){

            }
        }

        items[puptr] = x;
        if(++puptr == items.length){
            puptr = 0;
        }
        ++count;
        notifyAll();
    }

    public synchronized Object take(){
        while (count == 0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Object x = items[takeptr];
        if(++takeptr == items.length)
            takeptr = 0;
        --count;
        notifyAll();
        return x;
    }

    public static class Producer implements Runnable{
        private BoundedBuffer1 q;

        public Producer(BoundedBuffer1 q) {
            this.q = q;
            new Thread(this, "Producer").start();
        }

        int i = 0;

        public void run() {
            int i = 0;
            while(true){
                q.put(i++);
            }
        }
    }

    public static class Consumer implements  Runnable{

        private BoundedBuffer1 q;

        public Consumer(BoundedBuffer1 q) {
            this.q = q;
            new Thread(this, "Consumer").start();
        }

        public void run() {
            while (true){
                logger.info(q.take());
            }
        }
    }

    public static void mains(String[] args) {
        final BoundedBuffer1 boundedBuffer1 = new BoundedBuffer1();
        new Thread(new Producer(boundedBuffer1)).start();
        new Thread(new Consumer(boundedBuffer1)).start();

        LockSupport.park();
    }


    public static void main(String[] args) throws Exception{
        final Thread t = new Thread(
                new Runnable(){
                    public void run(){
                        LockSupport.park();
                        logger.info("Thread " + Thread.currentThread().getId() + " awake!");
                    }
                }
        );

        t.start();
        logger.info("Thread.sleep 3 second begin");
        Thread.sleep(3 * 1000);
        logger.info("Thread.sleep 3 second end");


        t.interrupt();
        logger.info("Thread interrupt");
        Thread.sleep(3 * 1000);

    }
}
