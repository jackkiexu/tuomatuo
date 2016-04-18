package com.lami.tuomatuo.search.base.curator.lock;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by xujiankang on 2016/4/15.
 */
public class DistributedLockExample {

    private static final Logger logger = Logger.getLogger(DistributedLockExample.class);

    private static CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
    private static final String PATH = "/tmp/2016041401";

    // 进程内部(可重入)读写锁
    private static InterProcessReadWriteLock lock;
    // 读锁
    private static InterProcessLock readLock;
    // 写锁
    private static InterProcessLock writeLock;

    static {
        client.start();
        lock = new InterProcessReadWriteLock(client, PATH);
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    public static void main(String[] args) {
        try {
            logger.info("client.isStarted():"+client.isStarted());
            List<Thread> jobs = Lists.newArrayList();
            for(int i = 0; i < 10; i++){
                Thread t = new Thread(new ParallelJob("Parallel任务-"+i, readLock));
                jobs.add(t);
            }
            for(int i = 0; i < 10; i++){
                Thread t = new Thread(new MutexJob("Mutex任务-"+i, writeLock));
                jobs.add(t);
            }

            for(Thread t : jobs){
                t.start();
            }
            Thread.sleep(60*1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
