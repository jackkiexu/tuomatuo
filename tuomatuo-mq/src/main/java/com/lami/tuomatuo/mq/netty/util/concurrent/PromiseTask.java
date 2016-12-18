package com.lami.tuomatuo.mq.netty.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;

/**
 * Created by xjk on 12/17/16.
 */
public class PromiseTask<V> extends DefaultPromise<V> implements RunnableFuture<V> {

    static <T> Callable<T> toCallable(Runnable runnable, T result) { return new RunnableAdapter<T>(runnable, result);}

    static final class RunnableAdapter<T> implements Callable<T>{
        Runnable task;
        T result;

        public RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }

        @Override
        public T call() throws Exception {
            task.run();
            return result;
        }

        @Override
        public String toString() {
            return "RunnableAdapter{" +
                    "task=" + task +
                    ", result=" + result +
                    '}';
        }
    }

    protected Callable<V> task;

    PromiseTask(EventExecutor executor, Callable<V> callable){
        super(executor);
        task = callable;
    }

    public PromiseTask(EventExecutor executor, Runnable runnable, V result) {
        this(executor, toCallable(runnable, result));
    }


    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public void run() {
        try{
            if(setUncancellableInternal()){
                V result = task.call();
                setSuccessInternal(result);
            }
        }catch (Throwable e){
            setFailureInternal(e);
        }
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
       throw new IllegalStateException();
    }

    protected final Promise<V> setFailureInternal(Throwable cause){
        super.setFailure(cause);
        return this;
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        return false;
    }

    protected final boolean tryFailureInternal(Throwable cause){
        return super.tryFailure(cause);
    }

    @Override
    public Promise<V> setSuccess(V result) {
        throw new IllegalStateException();
    }

    protected final Promise<V> setSuccessInternal(V result){
        super.setSuccess(result);
        return this;
    }

    @Override
    public boolean trySuccess(V result) {
        return false;
    }

    protected final boolean trySuccessInternal(V result){
        return super.trySuccess(result);
    }

    @Override
    public boolean setUncancellable() {
        throw new IllegalStateException();
    }

    protected final boolean setUncancellableInternal(){
        return super.setUncancellable();
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        buf.setCharAt(buf.length() - 1, ',');

        return buf.append(" task: ")
                .append(task)
                .append(')');
    }
}
