package com.ailami.tuomatuo.executors;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by xjk on 2016/12/6.
 */
public class DirectExecutorService implements ExecutorService {

    private static final Logger logger = Logger.getLogger(DirectExecutorService.class);

    private volatile boolean stopped;

    @Override
    public void shutdown() {
        stopped = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        stopped = true;
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return stopped;
    }

    @Override
    public boolean isTerminated() {
        return stopped;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
       throw new InterruptedException();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return new DirectFutureTask<T>(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return new DirectFutureTask<T>(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return new DirectFutureTask<>(task, null);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return Collections.emptyList();
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new InterruptedException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new InterruptedException();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new InterruptedException();
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
