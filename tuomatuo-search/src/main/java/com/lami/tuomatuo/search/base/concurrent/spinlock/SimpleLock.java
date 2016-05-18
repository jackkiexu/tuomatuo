package com.lami.tuomatuo.search.base.concurrent.spinlock;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by xujiankang on 2016/5/12.
 */
public class SimpleLock extends AbstractQueuedSynchronizer{

    private static Logger logger = Logger.getLogger(SimpleLock.class);

    private static final long serialVersionUID = 5635114653518113673L;

    public SimpleLock() {
    }

    protected boolean tryAcquire(int unused){
        if(compareAndSetState(0, 1)){
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }

    protected boolean tryRelease(int unused){
        setExclusiveOwnerThread(null);
        setState(0);
        return true;
    }

    public void lock(){
        acquire(1);
    }

    public boolean tryLock(){
        return tryAcquire(1);
    }

    public void unlock(){
        release(1);
    }

    public boolean isLocked(){
        return isHeldExclusively();
    }

    public static void main(String[] args) throws Exception{
        final SimpleLock lock = new SimpleLock();
        lock.lock();

        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                public void run() {
                    lock.lock();
                    logger.info("Thread name " + Thread.currentThread().getName() + ", " + Thread.currentThread().getId() + ", acquired the lock!");
                    lock.unlock();
                }
            }).start();
            // 简单的让线程按照 for 循环的顺序阻塞在 lock 上
            Thread.sleep(1000);
        }

        logger.info("main thread unlock");
        lock.unlock();
    }

}
