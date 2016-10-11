package com.lami.tuomatuo.search.base.curator.lock;

import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2016/4/15.
 */
public class MutexJob implements Runnable{
    private static final Logger logger = Logger.getLogger(ParallelJob.class);
    private  String name;
    private InterProcessLock lock;

    private int wait_time = 5;

    public MutexJob(String name, InterProcessLock lock) {
        this.name = name;
        this.lock = lock;
    }

    public void doWork() throws Exception{
        if(lock.acquire(wait_time, TimeUnit.SECONDS)){
            try {
                // 模拟job执行时间 0 - 2000 毫秒
                int exeTime = new Random().nextInt(2000);
                logger.info(name + "开始执行, 预执行时间=" + exeTime + "毫秒");
                Thread.sleep(exeTime);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.release();
            }
        }else{
            logger.info(name + "等待" + wait_time + "秒, 仍未能获取到 lock, 准备放弃");
        }
    }

    public void run() {
        try {
            doWork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
