package com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors;

/**
 * An object that executes submitted {@link Runnable} tasks. This
 * interface provides a way of decoupling task submission from the
 * mechanics of how each task will be run, including details of thread
 * use, scheduling, etc. An {@code Executor} is normally used
 * instead of explicity creating threads. For example, rather than
 * invoking {@code new Thread(new RunnnableTask()).start()} for each
 * of a set of tasks, you might use
 *
 * <pre>
 *     Executor executor = <em>anExecutor</em>
 *     executor.execute(new RunnableTask1())
 *     executor.execute(new RunnableTask2())
 * </pre>
 *
 * However, the {@code Executor} interface does not strictly
 * require that execution be asynchronous. In the simplest case, an
 * executor can run the submitted task immediately in the caller's
 * thread
 *
 * <pre>
 *     class DirectExecutor implements Executor{
 *         public void execute(Runnable r){
 *             r.run();
 *         }
 *     }
 * </pre>
 *
 * More typically, tasks are executed in some thread other
 * than the caller's thread. The executor below spawns a new thread
 * for each task
 *
 * <pre>
 *     class ThreadPerTaskExecutor implement Executor{
 *         public void execute(Runnable r){
 *             new Thread(r).start();
 *         }
 *     }
 * </pre>
 *
 * Many {@code Executor} implementations impose some sort of
 * limitation on how and when tasks are scheduled. The executor below
 * serializes the submission of tasks to a second executor,
 * illustrating a composite executor
 *
 * <pre>
 *     class SerialExecutor implements Executor {
 *         final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
 *         final Executor executor;
 *         Runnable active;
 *
 *         SerialExecutor(Executor executor){
 *         this.executor = executor;
 *         }
 *
 *         public synchronized void execute(final Runnable r){
 *          tasks.offer(new Runnable(){
 *              public void run(){
 *                  try{
 *                  r.run();
 *                  }finally{
 *                  schedulenext();
 *                  }
 *              }
 *          });
 *         }
 *
 *         protected synchronized void schedulenext(){
 *          if((active = tasks.poll()) != null){
 *            executor.execute(active);
 *          }
 *         }
 *     }
 * </pre>
 *
 * The {@code Executor} implementation provided in this package
 * implement {@link java.util.concurrent.ExecutorService} which is a more extensive
 * interface. The {@link ThreadPoolExecutor} classprovides an
 * extensible thread pool implementation. The {@link Executors} class
 * provides convenient factory methods for these Executors.
 *
 * <p>
 *     Memory consistency effects: Actions in a thread prior to
 *     submitting a {@code Runnable} object to an {@code Executor}
 *     <a href="package-summy.html#MemoryVisibility"><i>happen-before</i></a>
 *     its execution begins, perhaps in another thread
 * </p>
 *
 * Created by xujiankang on 2017/1/11.
 */
public interface KExecutor {

    /**
     * Executes the given command at some time in the future. The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation
     *
     * @param command the runnable task
     * @throws java.util.concurrent.RejectedExecutionException if this task cannot be
     * accepted for execution
     * @throws NullPointerException if command is null;
     */
    void execute(Runnable command);

}
