package com.lami.tuomatuo.mq.netty.util.concurrent;


import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.function.Consumer;

/**
 * {@link EventExecutor} implementation which makes no guarantees about the ordering of the task execution that
 * are submitted because there may be multiple threads executing these tasks
 *
 * <strong>
 *     Because it provides no ordering care should be taken when using it!
 * </strong>
 *
 * Created by xjk on 12/18/16.
 */
public class UnorderedThreadPoolEventExecutor extends ScheduledThreadPoolExecutor implements EventExecutor {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(
            UnorderedThreadPoolEventExecutor.class);

    private final Promise<?> terminationFuture = GlobalEventExecutor.INSTANCE.newPromise();
    private final Set<EventExecutor> executorSet = Collections.singleton((EventExecutor)this);

    /**
     * Calls {@link UnorderedThreadPoolEventExecutor#UnorderedThreadPoolEventExecutor(int, RejectedExecutionHandler)}
     * @param corePoolSize
     */
    public UnorderedThreadPoolEventExecutor(int corePoolSize) {
        this(corePoolSize, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class));
    }

    /**
     * See {@link ScheduledThreadPoolExecutor#ScheduledThreadPoolExecutor(int, ThreadFactory)}
     * @param corePoolSize
     * @param threadFactory
     */
    public UnorderedThreadPoolEventExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    /**
     * Calls {@link UnorderedThreadPoolEventExecutor#UnorderedThreadPoolEventExecutor(int, RejectedExecutionHandler)}
     * @param corePoolSize
     * @param handler
     */
    public UnorderedThreadPoolEventExecutor(int corePoolSize, RejectedExecutionHandler handler){
        this(corePoolSize, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class), handler);
    }

    /**
     * See {@link ScheduledThreadPoolExecutor#ScheduledThreadPoolExecutor(int, ThreadFactory, RejectedExecutionHandler)}
     * @param corePoolSize
     * @param threadFactory
     * @param handler
     */
    public UnorderedThreadPoolEventExecutor(int corePoolSize, ThreadFactory threadFactory, java.util.concurrent.RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    @Override
    public EventExecutor next() {
        return this;
    }

    @Override
    public EventExecutorGroup parent() {
        return this;
    }

    @Override
    public boolean inEventLoop() {
        return false;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return false;
    }


    @Override
    public <V> Promise<V> newPromise() {
        return new DefaultPromise<V>(this);
    }



    @Override
    public <V> ProgressivePromise<V> newProgressivePromise() {
        return new DefaultProgressivePromise<V>(this);
    }


    @Override
    public <V> Future<V> newSucceededFuture(V result) {
        return new SucceededFuture<V>(this, result);
    }

    @Override
    public <V> Future<V> newFailedFuture(Throwable cause) {
        return new FailedFuture<V>(this, cause);
    }

    @Override
    public boolean isShuttingDown() {
        return isShutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        List<Runnable> tasks = super.shutdownNow();
        terminationFuture.trySuccess(null);
        return tasks;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        terminationFuture.trySuccess(null);
    }

    @Override
    public Future<?> shutdownGracefully() {
        return shutdownGracefully(2, 15, TimeUnit.SECONDS);
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        // TODO: At the moment this just calls shutdown but we may be able to do something more smart here which
        // respects the quietPeriod and timeout
        shutdown();
        return terminationFuture();
    }

    @Override
    public Future<?> terminationFuture() {
        return terminationFuture;
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return executorSet.iterator();
    }


    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
        return new RunnableScheduledFutureTask<V>(this, callable, task);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        return new RunnableScheduledFutureTask<V>(this, runnable, task);
    }

    @Override
    public java.util.concurrent.ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return (ScheduledFuture<?>) super.schedule(command, delay, unit);
    }

    @Override
    public java.util.concurrent.ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return (ScheduledFuture<?>) super.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public java.util.concurrent.ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return (ScheduledFuture<?>) super.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return (Future<?>)super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return (Future<T>)super.submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return (Future<T>)super.submit(task);
    }

    static final class RunnableScheduledFutureTask<V> extends PromiseTask<V> implements RunnableScheduledFuture<V>, ScheduledFuture<V>{

        private RunnableScheduledFuture<V> future;

        RunnableScheduledFutureTask(EventExecutor executor, Runnable runnable, RunnableScheduledFuture<V> future) {
            super(executor, runnable, null);
            this.future = future;
        }

        RunnableScheduledFutureTask(EventExecutor executor, Callable<V> callable, RunnableScheduledFuture<V> future) {
            super(executor, callable);
            this.future = future;
        }

        @Override
        public void run() {
            if(!isPeriodic()){
                super.run();
            }else if(!isDone()){
                try{
                    // It's a period task so we need to ignore the return value
                    task.call();
                }catch (Throwable cause){
                    if(!tryFailureInternal(cause)){
                        logger.warn("Failure during execution of task", cause);
                    }
                }
            }
        }

        @Override
        public boolean isPeriodic() {
            return future.isPeriodic();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return future.getDelay(unit);
        }

        @Override
        public int compareTo(Delayed o) {
            return future.compareTo(o);
        }
    }


}
