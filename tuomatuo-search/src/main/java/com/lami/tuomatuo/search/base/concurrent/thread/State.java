package com.lami.tuomatuo.search.base.concurrent.thread;

/**
 * Created by xjk on 1/12/17.
 */
public enum State {
    /** 线程刚刚创建时的状态, 马上到 RUNNABLE */
    NEW,

    /** 线程初始化OK, 开始执行任务(run) */
    RUNNABLE,

    /**
     * 阻塞状态, 千万别和WAITING状态混淆
     * 这种状态是线程在等待 JVM monitor lock(通俗一点 就是等待执行 synchronous 里面的代码)
     * 这和 LockSupport 没半毛钱关系
     */
    BLOCKED,

    /**
     * 线程的等待状态, 导致线程进入这种状态通常是下面三个方法
     * 1. Object.wait()
     * 2. Thread.join()
     * 3. LockSupport.park()
     */
    WAITING,

    /**
     * 这也是线程的等待状态, 和WAITING差不多, 只是这个有timeout而已, 通常由下面四种方法导致
     * 1. Object.wait(long timeout)
     * 2. Thread.join(long timeout)
     * 3. LockSupport.parkNanos(long timeout)
     * 4. LockSupport.parkUntil(long timeout)
     */
    TIMED_WAITING,

    /**
     * 线程执行ok
     */
    TERMINATED
}