package com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An {@link Executor} that provides methods to manage termination and
 * methods that can produce a {@link Future} for tracking progress of
 * one or more asynchronous tasks
 *
 * <p>
 *     An {@code ExecutorService} can be shut down, which will cause
 *     it to reject new tasks. Two different methods are provided for
 *     shutting down an {@code ExecutorService}. The {@link #shutdown}
 *     method will allow previusly submitted tasks to execute before terminating,
 *     while the {@link #shutdownNow} method prevents waiting
 *     tasks from starting and attempts to stop currently executing tasks
 *     Upon termination, an executor has no tasks actively executing, no
 *     tasks awaiting execution, and no new tasks can be submitted. An
 *     unused {@code ExecutorService} should be shut down to allow
 *     reclamation of its resources.
 * </p>
 *
 * <p>
 *     Method {@code submit} extends base method {@link Executor#execute(Runnable)}
 *     by creating and returning a {@link Future} that can be used to cancel execution and/or wait for completion.
 *     Methods {@code invokeAny} and {@code invokeAll} perform the most
 *     commonly useful forms of bulk execution, executing a collection of
 *     tasks and then waiting for at least one, or all, to
 *     complete. (Class {@link ExecutorCompletionService}) can be used to
 *     write customed variants of these methods
 * </p>
 *
 * <p>
 *     The {@link Executors} class provides factory methods for the
 *     executor services provided in this package
 * </p>
 *
 * Here is a sketch of a network service in which threads in a thread
 * pool service incoming requests. It uses the preconfigured {@link
 * Executors#newFixedThreadPool} factory method
 *
 * class NetworkService implements Runnable{
 *     private final ServerSocket serverSocket;
 *     private final ExecutorService pool;
 *
 *     public networkService(int port, int poolSize) throws Exception{
 *         serverSocket = new ServerSocket(port);
 *         pool = Executors.newFixedThreadPool(poolSize);
 *     }
 *
 *     public void run(){ // run the service
 *         try{
 *             pool.execute(new Handler(serverSocket.accept()));
 *         }catch(Exception e){
 *             pool.shutdown();
 *         }
 *     }
 *
 *     class Handler implements Runnable {
 *         private final Socket socket;
 *         Handler(Socket socket){ this.socket = socket; }
 *         public void run(){
 *             // read and service request on socket
 *         }
 *     }
 * }
 *
 * The following method shuts down an {@code ExecutorService} in two phases,
 * first by calling {@code shutdown} to reject incoming tasks, and then
 * calling {@code shutdownNow}, if necessary, to cancel any lingering tasks:
 *
 * <pre>
 *     void shutdownAndAwaitTermination(ExecutorService pool){
 *         pool.shutdown(); // Disable new tasks from being shumitted
 *
 *         try{
 *             // Wait a while for existing tasks to terminate
 *             if(!pool.awaitTermination(60, TimeUnit.SECONDS)){
 *                 pool.shutdownNow(); // Cancel currently executing tasks
 *                 // Wait a while for task to respond being cancelled
 *                 if(!pool.awaitTermination(60, TimeUnit.SECONDS)){
 *                     System.err.println("Pool did not terminate");
 *                 }
 *             }
 *         }catch(Exception e){
 *             // (Re-)Cancel if current thread also interrupted
 *             pool.shutdownNow();
 *             // Preserve interrupt status
 *             Thread.currentThread().interrupt();
 *         }
 *     }
 * </pre>
 *
 * <p>
 *     Memory consistency effects: Actions in a thread prior to the
 *     submission of a {@code Runnable} or {@code Callable} task to an
 *     {@code ExecutorService}
 *     <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 *     any action taken by that task, which in turn <i>happen-before</i> the
 *     result is retrieved via {@code Future.get()}
 * </p>
 *
 * Created by xujiankang on 2017/1/11.
 */
public interface ExecutorService extends Executor{

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already sht down.
     *
     * <p>
     *     This method does not wait for previously submitted task to
     *     complete execution. Use {@link #awaitTermination awaitTermination}
     *     to do that
     * </p>
     *
     * @throws SecurityExceptionif a security manager exist and
     * shutting down this ExecutorServicemay manipulate
     * threads that the caller is not permitted to modify
     * because it does not hold {@link RuntimePermission} {@code ("modifyThread")}
     * or the security manager's {@code checkAccess} method
     * denies access
     */
    void shutdown();

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution
     *
     * <p>
     *     This method does not wait for actively executing tasks to
     *     terminate. Use {@link #awaittermination awaittermination} to
     *     do that
     * </p>
     *
     * <p>
     *     There are no guarantees beyond best effort attempts to stop
     *     processing actively executing tasks.
     *     For example, typical implementations will cancel vis {@link Thread#interrupt()}
     *     so any task that fails to respond to interrupts may never terminate
     * </p>
     *
     * @return list of tasks that never commenced execution
     * @throws SecurityException if a security manager exist and
     *      shutting down this ExecutorService may manipulate
     *      threads that the caller is not permitted to modify
     *      bacause it does not hold {@link RuntimePermission} {@code ("modifythread")},
     *      or the security manager's {@code checkAccess} method
     *      denies access.
     */
    List<Runnable> shutdownNow();

    /**
     * Returns {@code true} if this executor has been shut down
     *
     * @return {@code true} if this executor has been shut down
     */
    boolean isShutdown();

    /**
     * Returns {@code true} if all tasks have completed following shut down
     * Note that {@code isTermination} is never {@code true} unless
     * either {@code shutdown} or {@code shutdownNow} was called first
     *
     * @return {@code true} if all tasks have completed following shut down
     */
    boolean isterminated();

    /**
     * Blocking until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or current thread is
     * interrupted, whichever happens first
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return {@code true} if this executor terminated and
     *          {@code false} if the timeout elapsed before termination
     *
     * @throws InterruptedException if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task.
     * The Future's {@code get} method will return the task's result upon
     * successful completion
     *
     * <p>
     *     If you would like to immediately block waiting
     *     for a task, you can use constructions of the form
     *     {@code result = exec.submit(aCallable.get();)}
     * </p>
     *
     * <p>
     *     Note: The {@link Executors} class includes a set of methods
     *     that can convert some other closure-like objects,
     *     for example, {@link java.security.PrivilegedAction} to
     *     {@link Callable} form so they can be submitted
     * </p>
     *
     * @param task the task to submit
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     * @throws java.util.concurrent.RejectedExecutionException if the task cannot be
     * scheduled for execution
     * @throws NullPointerException if the task is null
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing task. The Futures's {@code get} method will
     * return the given result successful completion
     *
     * @param task the task submit
     * @param result the result to return
     * @param <T> the type of the result
     * @return RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException if task is null
     */
    <T> Future<T> submit(Runnable task, T result);

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's {@code get} method will
     * return {@code null} upon <em>successful</em> completion
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws java.util.concurrent.RejectedExecutionException if the task cannot be
     * scheduled for execution
     * @throws NullPointerException if the task is null
     */
    Future<?> submit(Runnable task);

    /**
     * Executes the given tasks, returning a list of Future holding
     * their status and results when all complete.
     * {@link Future#isDone()} is {@code true} for each
     * element of the returned list
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception
     * The result of this method are undefined if the given
     * collection is modified while this operation is in progress
     *
     * @param tasks the collection of tasks
     * @param <T> the type of the values returned from the tasks
     * @return a list of Future representing the tasks, in the same
     *          sequential order as produced by the iterator for the
     *          given task list, each of which has completed
     * @throws InterruptedException if interrupted while waiting , in
     *          which case unfinished tasks are cancelled
     * @throws NullPointerException if tasks or any of its element are {@code null}
     * @throws java.util.concurrent.RejectedExecutionException if any task cannot be
     *          scheduled for execution
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException;

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results
     * when all complete or the timeout expires, which ever happens first.
     * {@link Future#isDone()} is {}
     *
     * @param tasks
     * @param timeout
     * @param unit
     * @param <T>
     * @return
     * @throws InterruptedException
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException;
}
