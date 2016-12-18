package com.lami.tuomatuo.mq.netty.util.concurrent;

import com.lami.tuomatuo.mq.netty.util.internal.ObjectUtil;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base class for {@link EventExecutor}s that want to support scheduling
 * Created by xjk on 12/18/16.
 */
public abstract class AbstractScheduledEventExecutor extends AbstractEventExecutor {

    Queue<ScheduledFutureTask<?>> scheduledTaskQueue;

    protected static long nanoTime(){
        return ScheduledFutureTask.nanoTime();
    }

    Queue<ScheduledFutureTask<?>> scheduledTaskQueue(){
        if(scheduledTaskQueue == null){
            scheduledTaskQueue = new PriorityQueue<ScheduledFutureTask<?>>();
        }
        return scheduledTaskQueue;
    }

    private static boolean isNullOrEmpty(Queue<ScheduledFutureTask<?>> queue){
        return queue == null || queue.isEmpty();
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(command, "command");
        ObjectUtil.checkNotNull(unit, "unit");

        if(initialDelay < 0){
            throw new IllegalArgumentException(String.format("initialDelay: %d (expect: >= 0)", initialDelay));
        }

        if(delay < 0){
            throw new IllegalArgumentException(String.format("delay: %d (expect: >= 0)", delay));
        }

        return schedule(new ScheduledFutureTask<Void>(this, Executors.callable(command, null),
                ScheduledFutureTask.deadlineNanos(unit.toNanos(initialDelay)), -unit.toNanos(delay)));
    }

    <V> ScheduledFuture<V> schedule(final ScheduledFutureTask<V> task){
        if(inEventLoop()){
            scheduledTaskQueue.add(task);
        }else{
            execute(() -> { scheduledTaskQueue().add(task);});
        }

        return task;
    }

    final void removeScheduled(final ScheduledFutureTask<?> task){
        if(inEventLoop()){
            scheduledTaskQueue().remove(task);
        }else{
            execute(() -> {removeScheduled(task);});
        }
    }

    protected void cancelScheduledTasks(){

    }

}
