package com.lami.tuomatuo.search.concurrent.reentrantreadritelock;

import com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock.KReentrantReadWriteLock;
import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2017/2/3.
 */
public class WriteThread extends Thread {

    private static final Logger logger = Logger.getLogger(WriteThread.class);

    private KReentrantReadWriteLock rrwLock;

    public WriteThread(String name, KReentrantReadWriteLock rrwLock) {
        super(name);
        this.rrwLock = rrwLock;
    }

    @Override
    public void run() {
        logger.info(Thread.currentThread().getName() + " trying to lock");
        try {
            rrwLock.writeLock().lock();
            Thread.sleep(500 * 1000);
            logger.info(Thread.currentThread().getName() + " lock successfully");
        }catch (Exception e){

        }finally {
            rrwLock.writeLock().unlock();
            logger.info(Thread.currentThread().getName() + " unlock successfully");
        }
    }
}
