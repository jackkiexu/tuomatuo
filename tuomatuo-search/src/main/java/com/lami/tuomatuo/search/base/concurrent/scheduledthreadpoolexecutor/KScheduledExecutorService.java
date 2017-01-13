package com.lami.tuomatuo.search.base.concurrent.scheduledthreadpoolexecutor;


import com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors.KExecutorService;

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
}
