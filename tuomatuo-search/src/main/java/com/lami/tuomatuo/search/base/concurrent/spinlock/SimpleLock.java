package com.lami.tuomatuo.search.base.concurrent.spinlock;

import org.apache.log4j.Logger;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by xujiankang on 2016/5/12.
 */
public class SimpleLock extends MyAbstractQueuedSynchronizer{

    private static Logger logger = Logger.getLogger(SimpleLock.class);

    private static final long serialVersionUID = 5635114653518113673L;

    public SimpleLock() {
        logger.info("SimpleLock");
        logger.info("this.head:"+this.head);
        logger.info("this.tail:"+this.tail);
        logger.info("this.state:"+this.state);
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
        logger.info("this.head:"+this.head);
        logger.info("this.tail:"+this.tail);
        logger.info("this.state:"+this.state);
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
        logger.info("fuck");
        final SimpleLock lock = new SimpleLock();
        lock.lock();

        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                public void run() {
                    logger.info("Thread name " + Thread.currentThread().getName() + ", " + Thread.currentThread().getId() + ", acquired the lock begin!");
                    logger.info("this.head:"+lock.head);
                    logger.info("this.tail:" + lock.tail);
                    logger.info("this.state:" + lock.state);
                    lock.lock();
                    logger.info("this.head:" + lock.head);
                    logger.info("this.tail:" + lock.tail);
                    logger.info("this.state:" + lock.state);
                    logger.info("Thread name " + Thread.currentThread().getName() + ", " + Thread.currentThread().getId() + ", acquired the lock over!");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("Thread name " + Thread.currentThread().getName() + ", " + Thread.currentThread().getId() + ", acquired the unlock begin!");
                    lock.unlock();
                    logger.info("Thread name " + Thread.currentThread().getName() + ", " + Thread.currentThread().getId() + ", acquired the unlock over!");
                }
            }).start();
            // 简单的让线程按照 for 循环的顺序阻塞在 lock 上
            Thread.sleep(100);
        }

        logger.info("this.head:"+lock.head);
        logger.info("this.tail:" + lock.tail);
        logger.info("this.state:" + lock.state);

        Thread.sleep(1500);
        logger.info("main thread unlock begin");
        lock.unlock();
        logger.info("main thread unlock over");
    }

}
