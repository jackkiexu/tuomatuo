package com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors;

import com.lami.tuomatuo.search.base.concurrent.future.FutureTask;
import sun.java2d.pipe.NullPipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * Provides default implementations of {@link KExecutorService}
 * execution methods. This class implements the {@code submit},
 * {@code invokeAny} and {@code invokeAll} methods using a
 * {@link RunnableFuture} returned by {@code newTaskFor}, which defaults
 * to the {@code FutureTask} class provided in this package. For example
 * the implementation of {@code submit(Runnable)} creates an
 * associated {@code RunnableFuture} that is executed and
 * returned {@code RunnableFuture} implementations other than
 * {@code FutureTask}
 *
 * <p>
 *     <b>Extension example</b>. Here is a sketch of a class
 *     that customizes {@link KThreadPoolExecutor} to use
 *     a {@code CustomeTask} class instead of the default {@code FutureTask}
 *
 *     public class CustomeThreadPoolExecutor extends ThreadPoolExecutor{
 *         static class CustomTask<V> implements RunnableFuture<V>{...}
 *
 *         protected <V> RunnableFuture<V> newTaskFor(Callable<V> c){
 *              return new CustomTask<V>(c);
 *         }
 *
 *         protected <V> RunnableFuture<V> newTaskFor(Runnable r, V r){
 *          return new CustomTask<V>(r, v);
 *         }
 *
 *         // ... add constructors, etc
 *     }
 * </p>
 *
 * Created by xujiankang on 2017/1/12.
 */
public abstract class KAbstractExecutorService implements ExecutorService{

    /**
     * Returns a {@code RunnableFuture} for the given runnable and default
     * value
     *
     * @param runnable the runnable task being wrapped
     * @param value the default value for the returned future
     * @param <T> the type of the given value
     * @return a {@code  RunnableFuture} which, when run, will run the
     * underlying runnable and which, as a {@code Future}, will yield
     * the given values as its result an provide for cancellation of
     * the underlying task
     * 这是对于 return 的描述
     * 1. 执行 runnable 里面的方法
     * 2. 提供出给定的值(参数中的 T value)作为执行的结果
     * 3. 提供一个 对底层人物cancellation的机制
     */
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value){
        return new FutureTask<T>(runnable, value);
    }

    /**
     * Return a {@code RunnableFuture} for the given callable task
     *
     * @param callable the callable task being wrapped
     * @param <T> the type of the callable's result
     * @return a {@code RunnableFuture} which, when run, will call the
     * underlying callable and which, as a {@code Future}, will yield
     * the callable's result as its result and provide for
     * cancellation of uderlying task
     */
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable){
        return new FutureTask<T>(callable);
    }

    public Future<?> submit(Runnable task){
        if(task == null) throw new NullPointerException();
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Runnable task, T result){
        if(task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Callable<T> task){
        if(task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

    private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks, boolean timed, long nanos)
        throws InterruptedException, ExecutionException, TimeoutException{
        if(tasks == null){
            throw new NullPointerException();
        }
        int ntasks = tasks.size();
        if(ntasks == 0){
            throw new IllegalArgumentException();
        }
        ArrayList<Future<T>> futures = new ArrayList<>(ntasks);
        ExecutorCompletionService<T> esc = new ExecutorCompletionService<T>(this);

        /**
         * For efficiency, especially in executors with limited
         * parallelism, check to see if previously submitted tasks are
         * done before submitting more of them, This interleaving
         * plus the exception mechanics account for messiness of main
         * loop
         */

        try{
            /**
             * Record exception so that if we fail to obtain any
             * result, we can throw the last exception we got
             */
            ExecutionException ee = null;
            final long deadline = timed ? System.nanoTime() + nanos : 0l;
            Iterator<? extends Callable<T>> it = tasks.iterator();

            // Start one task for sure: the rest incrementally
            futures.add(esc.submit(it.next()));
            --ntasks;
            int active = 1;

            for(;;){
                Future<T> f = esc.poll();
                if(f == null){
                    if(ntasks > 0){
                        --ntasks;
                        futures.add(esc.submit(it.next()));
                        ++active;
                    }
                    else if(active == 0){
                        break;
                    }
                    else if(timed){
                        f = esc.poll(nanos, TimeUnit.NANOSECONDS);
                        if(f == null){
                            throw new TimeoutException();
                        }
                        nanos = deadline - System.nanoTime();
                    }
                }

                if(f != null){
                    --active;
                    try {
                        return f.get();
                    }catch (ExecutionException eex){
                        ee = eex;
                    }catch (RuntimeException rex){
                        ee = new ExecutionException(rex);
                    }
                }
            }

            if(ee == null){
                ee = new ExecutionException(new RuntimeException());
            }
            throw  ee;
        }finally {
            for(int i = 0, size = futures.size(); i < size; i++){
                futures.get(i).cancel(true);
            }
        }
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException{
        return doInvokeAny(tasks, true, unit.toNanos(timeout));
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException{
        if(tasks == null){
            throw new NullPointerException();
        }
        ArrayList<Future<T>> futures = new ArrayList<>(tasks.size());
        boolean done = false;

        try {
            for(Callable<T> t : tasks){
                RunnableFuture<T> f = newTaskFor(t);
                futures.add(f);
                execute(f);
            }
            for(int i = 0, size = futures.size(); i < size; i++){
                Future<T> f = futures.get(i);
                if(!f.isDone()){
                    try {
                        f.get();
                    }catch (Exception e){
                    }
                }
            }
            done = true;
            return futures;
        }finally {
            if(!done){
                for(int i = 0, size = futures.size(); i < size; i++){
                    futures.get(i).cancel(true);
                }
            }
        }
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException{
        if(tasks == null){
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        ArrayList<Future<T>> futures = new ArrayList<>(tasks.size());
        boolean done = false;

        try{
            for(Callable<T> t : tasks){
                futures.add(newTaskFor(t));
            }

            final long deadline = System.nanoTime() + nanos;
            final int size = futures.size();

            // Interleave time checks and calls to execute in case
            // executor doesnt have any/much parallelism
            for(int i = 0; i < size; i++){
                execute((Runnable)futures.get(i));
                nanos = deadline - System.nanoTime();
                if(nanos <= 0L){
                    return futures;
                }
            }


            for(int i = 0; i < size; i++){
                Future<T> f = futures.get(i);
                if(!f.isDone()){
                    if(nanos <= 0L){
                        return futures;
                    }

                    try {
                        f.get(nanos, TimeUnit.NANOSECONDS);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        return futures;
                    }
                    nanos = deadline - System.nanoTime();
                }
            }
            done = true;
            return futures;
        }finally {
            if(!done){
                for(int i = 0, size = futures.size(); i < size; i++){
                    futures.get(i).cancel(true);
                }
            }
        }
    }

}
