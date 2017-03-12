package com.apache.catalina.tribes.util;

import com.apache.tomcat.util.res.StringManager;

import java.util.concurrent.*;

/**
 * Created by xjk on 3/12/17.
 */
public class ExecutorFactory {


    protected static final StringManager sm = StringManager.getManager(ExecutorFactory.class.getPackage().getName());

    public static ExecutorService newThreadPool(int minThreads, int maxThreads, long maxIdleTime, TimeUnit unit) {
        TaskQueue taskqueue = new TaskQueue();
        ThreadPoolExecutor service = new TribesThreadPoolExecutor(minThreads, maxThreads, maxIdleTime, unit,taskqueue);
        taskqueue.setParent(service);
        return service;
    }

    public static ExecutorService newThreadPool(int minThreads, int maxThreads, long maxIdleTime, TimeUnit unit, ThreadFactory threadFactory) {
        TaskQueue taskqueue = new TaskQueue();
        ThreadPoolExecutor service = new TribesThreadPoolExecutor(minThreads, maxThreads, maxIdleTime, unit,taskqueue, threadFactory);
        taskqueue.setParent(service);
        return service;
    }

    private static class TribesThreadPoolExecutor extends ThreadPoolExecutor{

        public TribesThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        public TribesThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public TribesThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        public TribesThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }


        @Override
        public void execute(Runnable command) {
            try{
                super.execute(command);
            }catch (RejectedExecutionException rx){
                if(super.getQueue() instanceof TaskQueue){
                    TaskQueue queue = (TaskQueue)super.getQueue();
                    if(!queue.force(command)){
                        throw new RejectedExecutionException(sm.getString("executorFactory.queue.full"));
                    }
                }
            }
        }
    }


    private static class TaskQueue extends LinkedBlockingQueue<Runnable> {

        private static final long serialVersionUID = -6015812018307434952L;

        ThreadPoolExecutor parent = null;

        public TaskQueue() {
            super();
        }

        public void setParent(ThreadPoolExecutor parent) {
            this.parent = parent;
        }


        public boolean force(Runnable o){
            if(parent.isShutdown()) throw new RejectedExecutionException("executorFactory.not.running");
            return super.offer(o); // forces the item onto the queue, to be used if the task is rejected
        }


        @Override
        public boolean offer(Runnable o) {
            // we can't do any checks
            if(parent == null) return super.offer(o);
            // we are maxed out on threads, simply queue the object
            if(parent.getPoolSize() == parent.getMaximumPoolSize()) return super.offer(o);
            // We have idle threads, just add it to the queue
            // this is an approximation, so it could use some tuning
            if(parent.getActiveCount() < parent.getPoolSize()) return super.offer(o);
            // If we have less threads than maximum force creation of a new thread
            if(parent.getPoolSize() < parent.getMaximumPoolSize()) return false;
            // if we reached here, we need to add it to the queue
            return super.offer(o);
        }
    }
}
