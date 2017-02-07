package com.lami.tuomatuo.search.base.concurrent.reentrantreadritelock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by xjk on 2/7/17.
 */
public class CacheData {

    Object data; // 正真的数据
    volatile boolean cacheValid; // 缓存是否有效
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    void processCacheDate(){
        rwl.readLock().lock(); // 1. 先获取 readLock
        if(!cacheValid){       // 2. 发现数据不有效
            // Must release read lock before acquiring write lock
            rwl.readLock().unlock(); // 3. 释放 readLock
            rwl.writeLock().lock();  // 4. 获取 writeLock
            try{
                // Recheck state because another thread might have
                // acquired write lock and changed state before we did
                if(!cacheValid){            // 5. 重新确认数据是否真的无效
                    // data = ...           // 6. 进行数据 data 的重新赋值
                    cacheValid = true;      // 7. 重置标签 cacheValid
                }
                // Downgrade by acquiring read lock before releasing write lock
                rwl.readLock().lock();      // 8. 在获取 writeLock 的前提下, 再次获取 readLock
            }finally{
                rwl.writeLock().unlock(); // Unlock write, still hold read // 9. 释放 writeLock, 完成锁的降级
            }
        }

        try{
            // use(data);
        }finally{
            rwl.readLock().unlock(); // 10. 释放 readLock
        }
    }

}
