package com.lami.tuomatuo.search.concurrent.reentrantreadritelock;

import com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock.KReentrantReadWriteLock;
import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2017/2/3.
 */
public class ReadThread extends Thread {

    private static final Logger logger = Logger.getLogger(ReadThread.class);

    private KReentrantReadWriteLock rrwLock;

    public ReadThread(String name, KReentrantReadWriteLock rrwLock) {
        super(name);
        this.rrwLock = rrwLock;
    }

    @Override
    public void run() {

        logger.info(Thread.currentThread().getName() + " trying to lock");
        try{
            rrwLock.readLock().lock();
            logger.info(Thread.currentThread().getName() + " lock successfully");
            Thread.sleep(100 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            rrwLock.readLock().unlock();
            logger.info(Thread.currentThread().getName() + " unlock successfully");
        }

    }
}
