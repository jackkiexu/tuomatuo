package com.lami.tuomatuo.search.base.concurrent.countdownlatch;

import java.util.concurrent.*;

/**
 * Created by xjk on 2/12/17.
 */
public class KFuture<V> implements Future<V> {

    private CountDownLatch latch; // 控制获取值等待
    private volatile V result; // 执行结果

    public KFuture() {
        latch = new CountDownLatch(1);
    }

    /**
     * cancel 执行计划
     */
    public boolean cancel(boolean ignored) {
        boolean cancelled = false;
        if (latch.getCount() == 1) {
            latch.countDown();
            cancelled = true;
        }
        return cancelled;
    }

    /**
     * 判断是否 cancel
     */
    public boolean isCancelled() {
        return latch.getCount() == 0 && result == null;
    }

    /**
     * 判断任务是否执行
     */
    @Override
    public boolean isDone() {
        return result != null;
    }


    /**
     * 获取 result
     */
    public V get() throws InterruptedException {
        try {
            latch.await();
            return result;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    /**
     * 定时获取 result
     */
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            latch.await(timeout, unit);
            return result;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }
}
