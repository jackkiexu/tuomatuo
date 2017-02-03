package com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by xujiankang on 2017/2/3.
 */
public class CachedData {

    private Object data;
    volatile boolean cacheValid;
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    public void processCachedData(){
        rwl.readLock().lock();
        if(!cacheValid){
            // Must release read lock before acquiring write lock
            rwl.readLock().unlock();
            rwl.writeLock().lock();

            try{
                // Recheck state because another thread might have
                // acquired write lock and changed state before we did
                if(!cacheValid){
                    // data = .....
                    cacheValid = true;
                }
                // Downgrade by acquiring read lock before releasing write lock
                rwl.readLock().lock();
            }finally {
                rwl.writeLock().unlock(); // Unlock write, still hold read
            }
        }

        try {
            // use(data)...
        }finally {
            rwl.readLock().unlock();
        }
    }

}
