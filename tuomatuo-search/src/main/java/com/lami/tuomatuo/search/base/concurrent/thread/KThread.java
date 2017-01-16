package com.lami.tuomatuo.search.base.concurrent.thread;

/**
 * Created by xjk on 1/12/17.
 */
public class KThread {
//    /** 等待 */
//    Object.wait();
//    /** 通知 */
//    Object.notify();
//    /** 悬挂 */
//    Thread.suspend();
//    /** 重用 */
//    Thread.resume();
//    /** 等待x线程执行后, 当前线程再执行 */
//    Thread.join();
//    /**
//     * A hint to the scheduler that the current thread is willing to yield
//     * its current use of a processor. The scheduler is free to ignore this
//     * hint.
//     * 这段英语大体意思: 给调度器发送信息, 当前线程推出CPU调度(这个不是指当前线程不执行任务)
//     * 调用这个方法后, 当前线程会先推出任务调度, 然后再重新抢夺CPU, 但能不能抢到就不一定了
//     * 通产用于, 当前线程占用较多资源, 但任务又不紧急的情况(concurrent包中的源码会提及)
//     */
//    Thread.yield();

    /** 线程中断 */
    public static void main(String[] args) {
        /**
         * 作用: 中断线程,
         * 若线程 sleep 或 wait 时调用此方法,
         * 则抛出 InterruptedException 异常, 并且会清除中断标记
         * (ps 重点来了, 若通过 LockSupport阻塞线程, 则不会抛出异常, 并且不会清除线程的中断标记, 这在 concurrent 包里面充分利用了这个机制)
         *
         * 1.先通过 LockSupport.park(this) 来中断, 而后其他线程释放lock时, 唤醒这个线程, 这时再调用 Thread.interrupted() 返回中断标示(调用此方法会清除中断标示)
         *  这时外面的函数会根据 parkAndCheckInterrupt() 函数的返回值判断线程的唤醒是被 interrupted 还是正常的唤醒(LockSupport.unpark()) 来决定后续的策略
         * private final boolean parkAndCheckInterrupt() {
         *     LockSupport.park(this);
         *     return Thread.interrupted();
         * }
         */
        Thread.currentThread().interrupt();

        /**
         * 判断当前的线程是否中断, 返回 true/false
         */
        Thread.currentThread().isInterrupted();

        /**
         * 判断当前的线程是否中断, 并且清除中断标示(注意这里是 interrupted, 和上面的 interrupt是不一样的)
         */
        Thread.currentThread().interrupted();
    }
}
