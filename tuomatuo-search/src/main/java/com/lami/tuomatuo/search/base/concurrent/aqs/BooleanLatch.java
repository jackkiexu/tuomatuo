package com.lami.tuomatuo.search.base.concurrent.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 下面这个类其实就是 CountDownLatch 的 state = 1 的版本
 * Created by xujiankang on 2017/1/20.
 */
public class BooleanLatch {

    /** 初始时 AQS 的 state 为 0 */
     private static class Sync extends AbstractQueuedSynchronizer {
         /** 第一次进行调用时 getState == 0 */
         boolean isSignalled() { return getState() != 0; }

        /** 判断 state 是否不为 0, 不然的话直接将当前的线程封装成 Node 交由 AQS 的 Sync Queue 里面 等待获取ss  */
         protected int tryAcquireShared(int ignore){
             return isSignalled()? 1 : -1;
         }
         /** 释放 等待中的线程 */
         protected boolean tryReleaseShared(int ignore){
             setState(1);
             return true;
         }
     }

    private final Sync sync = new Sync();
    public boolean isSignalled() { return sync.isSignalled(); }
    public void signal()         { sync.releaseShared(1); }
    public void await() throws InterruptedException {
           sync.acquireSharedInterruptibly(1); // 现调用 AQS.acquireSharedInterruptibly 再调用 子类的 tryAcquireShared -> isSignalled
    }

}
