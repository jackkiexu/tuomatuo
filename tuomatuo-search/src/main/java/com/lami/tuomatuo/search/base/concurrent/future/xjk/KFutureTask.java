package com.lami.tuomatuo.search.base.concurrent.future.xjk;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 实现自己的Future要点
 * 1. 需要一个反应当前future状态的变量
 * 2. 一个指向线程的链表节点即一个链表
 * 3. 实现一个线程安全的链表节点删除方法
 *
 * Created by xjk on 12/17/16.
 */
public class KFutureTask<V> implements Future<V> {

    /**
     *
     */
    private volatile int state;
    private static final int NEW             = 0; // 新建 IFutureTask
    private static final int EXECUTING       = 1; // 开始执行 IFutureTask 中的task
    private static final int COMPLETED       = 2; // 任务执行结束
    private static final int CANCELLED       = 3; // 主动调用future.cancel进行任务取消(前提任务的状态是new)
    private static final int INTERRUPTED     = 4; // 主动调用Thread.interrupted进行线程的中断

    private static Unsafe unsafe;


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }


    /**
     * 链表节点
     */
    static final class WaiterNode{
        Thread thread;
        WaiterNode   nextNode;
    }

    /**
     * 当前线程入队列方法
     * @return
     */
    private WaiterNode enqueue(){
        return null;
    }

    /**
     * 队列中移除指定的 WaiterNode
     */
    private void removeWaiterNode(){

    }

    /**
     * 当任务完成时移除所有的节点
     */
    private void removeAllWaiterNode(){

    }




}
