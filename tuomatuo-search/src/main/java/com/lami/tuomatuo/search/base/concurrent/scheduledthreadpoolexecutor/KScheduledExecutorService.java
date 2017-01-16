package com.lami.tuomatuo.search.base.concurrent.scheduledthreadpoolexecutor;


import com.lami.tuomatuo.search.base.concurrent.executors.schedule.ScheduledFuture;
import com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors.KExecutorService;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * An {@link KExecutorService} that can schedule commands to run after a given
 * delay, or to execute periodically
 *
 * <p>
 *     The {@code schedule} methods create tasks with various delays
 *     and return a task object that can be used to cancel or check
 *     execution. The {@code scheduleAtFixedRate} and
 *     {@code scheduleWithFixedDelay} methods create and execute tasks
 *     that run periodically until cancelled
 * </p>
 *
 * <p>
 *     Commands submitted using the {@link com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors.KExecutor#execute(runnable)}
 *     and {@link KExecutorService} {@code submit} methods are scheduled
 *     with a required delay of zero. Zero and negative delays (but not
 *     periods) are also allowed in {@code schedule} methods, and are
 *     treated as requests for immediate execution
 * </p>
 *
 * <p>
 *     All {@code schedule} method accept <em>relative</em> delays and
 *     periods as arguments, not absolute time or dates. It is a simple
 *     matter to transform an absolute time represented as a {@link Date}
 *     to the required form, Form example, to schedule at
 *     a certain future {@code date}, you can use: {@code schedule(task, date.getTime() - System.currentTimeMillis()), TimeUnit.MILLISECONDS}
 *     Beware however that expiration of a relative delay need not coincide with the current {@code Date}
 *     at which the task is enabled due to network time synchronization
 *     protocols, clock drift, or other factors
 * </p>
 *
 * <p>
 *      The {@link Executors} class provides convenient factory methods for
 *      the ScheduledExecutorService implementations provided in this package
 *      <h3>Usage Example</h3>
 * </p>
 *
 * Here is a class with a method that sets up a ScheduledExecutorService
 * to beep every ten seconds for an hour:
 *
 * <pre>
 *     class BeeperControl{
 *         private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
 *
 *         public void beepForAnHour(){
 *             final Runnable beeper = new Runnable(){
 *                 public void run(){
 *                     System.out.println("beep")
 *                 }
 *             }
 *         }
 *
 *         final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
 *         scheduler.schedule(new Runnable(){
 *             public void run(){ beeperHandle.cancel(true);}
 *         }, 60 * 60 , SECONDS)
 *     }
 * </pre>
 *
 *
 * Created by xujiankang on 2017/1/12.
 */
public interface KScheduledExecutorService extends KExecutorService {

    /**
     * Creates and executes a noe-shot action that becomes enabled
     * after the given delay
     *
     * @param command the task to execute
     * @param delay the time from now to delay execution
     * @param unit the time unit of the delay parameter
     * @return a KScheduledFuture representing pending completion of
     *          the task and whose {@code get()} method will return
     *          {@code null} upon completion
     * @throws java.util.concurrent.RejectedExecutionException if the task cannot be
     *          scheduled for execution
     * @throws NullPointerException if command is null
     */
    KScheduledFuture<?> submit(Runnable command, long delay, TimeUnit unit);

    /**
     * Creates and executes a KScheduledFuture that becomes enabled after the
     * given delay
     *
     * @param callable the function to execute
     * @param delay the time from now to delay execution
     * @param unit  the time unit of the delay parameter
     * @param <V> the type of the callable's result
     * @return a KScheduledFuture that can be used extract result or cancel
     * @throws java.util.concurrent.RejectedExecutionException if the task cannot be
     *          scheduled for execution
     * @throws NullPointerException if callable is null
     */
    <V> KScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    /**
     * Creates and executes a periodic action that becomes enabled first
     * after the given initial delay, and subsequently with the given period;
     * that is executions will commence after
     * {@code initialDelay} then {@code initialDelay+period}, then
     * {@code initialDelay + 2 * period}, and so on.
     *
     * @param command the task to execute
     * @param initialDelay the time to delay first execution
     * @param period the period between successive executions
     * @param unit the time unit of the initialDelay and period parameters
     * @return a KScheduledFuture representing pending completion of
     *          the task, and whose {@code get()} method will throw an
     *          exception upon cancellation
     * @throws java.util.concurrent.RejectedExecutionException if the task cannot be
     *          scheduled for execution
     * @throws NullPointerException if command is null
     * @throws IllegalArgumentException if period less than or equal to zero
     */
    KScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

    /**
     * Creates and executes a periodic action that becomes enabled first
     * after the given initial delay, and subsequently with the
     * given delay between the termination of one execution and the
     * commencement of the next. If any execution of the task
     * encounters an exception, subsequent executions are suppressed.
     * Other, the task will only terminate via cancellation or
     * termination of the executor.
     *
     * @param command the task to execute
     * @param initialDelay the time to delay first execution
     * @param delay the delay between the termination of one
     *              execution and the commencement of the next
     * @param unit the time unit of the initialDelay and delay parameters
     * @return a KScheduledFuture representing pending completion of
     *          the task, and whose {@code get()} method will throw an
     *          exception upon cancellation
     * @throws java.util.concurrent.RejectedExecutionException if the task cannot be
     *          scheduled for execution
     * @throws NullPointerException if command is null
     * @throws IllegalArgumentException if delay less than or equal to zero
     */
    KScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit);
}
