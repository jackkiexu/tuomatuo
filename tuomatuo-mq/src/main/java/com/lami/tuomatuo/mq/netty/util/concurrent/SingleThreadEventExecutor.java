package com.lami.tuomatuo.mq.netty.util.concurrent;

import com.lami.tuomatuo.mq.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Abstract base class for {@link OrderedEventExecutor}'s that execute all its submitted tasks in a single thread
 *
 * Created by xjk on 12/18/16.
 */
public abstract class SingleThreadEventExecutor extends AbstractScheduledEventExecutor implements OrderedEventExecutor{

    static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16,
            SystemPropertyUtil.getInt("io.netty.eventexecutor.maxPendingTasks", Integer.MAX_VALUE));

    private static final Logger logger = Logger.getLogger(SingleThreadEventExecutor.class);

    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final int ST_TERMINATED = 5;

    private static final Runnable WAKEUP_TASK = () -> {};
    private static AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATER;

    static {
        AtomicIntegerFieldUpdater<SingleThreadEventExecutor> updater =
                PlatformDependent.newAtomicIntegerFieldUpdater(SingleThreadEventExecutor.class, "state");
        if(updater == null){
            updater = AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");
        }
        STATE_UPDATER = updater;
    }

    private EventExecutorGroup parent;
    private Queue<Runnable> taskQueue;
    private Thread thread;
    private ThreadProperties threadProperties;
    private Semaphore threadLock = new Semaphore(0);
    private Set<Runnable> shutdownHooks = new LinkedHashSet<>();
    private boolean addTaskWakeUp;
    private int maxPendingTasks;
    private RejectedExecutionHandler rejectedExecutionHandler;

    private long lastExecutionTime;

    private volatile int state = ST_NOT_STARTED;

    private volatile long gracefulShutdownQuietPeriod;
    private volatile long gracefulShutdownTimeout;
    private long gracefulShutdownStartTime;

    private final Promise<?> terminationFuture = new DefaultPromise<Void>(GlobalEventExecutor.INSTANCE);


    protected SingleThreadEventExecutor(
            EventExecutorGroup parent, ThreadFactory threadFactory, boolean addTaskWakesUp, int maxPendingTasks,
            RejectedExecutionHandler rejectedHandler
    ){
        if(threadFactory == null){
            throw new NullPointerException("threadFactory");
        }

        this.parent = parent;
        this.addTaskWakeUp = addTaskWakesUp;

        thread = threadFactory.newThread(() -> {
            boolean success = false;
            updateLastExecutionTime();

            try{
                SingleThreadEventExecutor.this.run();
                success = true;
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }

        });

        threadProperties = new DefaultThreadProperties(thread);
        this.maxPendingTasks = Math.max(16, maxPendingTasks);
        taskQueue = newTaskQueue();
        rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
    }

    protected Queue<Runnable> newTaskQueue(){
        return null;
    }

    protected boolean runAllTasks(){
        return true;
    }

    private boolean runShutdownHooks(){
        return false;
    }

    protected abstract void run();

    @Override
    public EventExecutorGroup parent() {
        return null;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return false;
    }


    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return null;
    }

    @Override
    public Future<?> terminationFuture() {
        return terminationFuture;
    }

    @Deprecated
    @Override
    public void shutdown() {
        if(isShutdown()){
            return;
        }

        boolean inEventLoop = inEventLoop();
        boolean wakeup;
        int oldState;
        for(;;){
            if(isShuttingDown()){
                return;
            }

            int newState;
            wakeup = true;
            oldState = STATE_UPDATER.get(this);
            if(inEventLoop){
                newState = ST_SHUTDOWN;
            }else{
                switch (oldState) {
                    case ST_NOT_STARTED:
                    case ST_STARTED:
                    case ST_SHUTTING_DOWN:
                        newState = ST_SHUTDOWN;
                        break;
                    default:
                        newState = oldState;
                        wakeup = false;
                }
            }

            if(STATE_UPDATER.compareAndSet(this, oldState, newState)){
                break;
            }
        }

        if(oldState == ST_NOT_STARTED){
            thread.start();
        }

        if(wakeup){
            wakeup(inEventLoop);
        }
    }

    final boolean offerTask(Runnable task){
        if(isShutdown()){
            reject();
        }
        return taskQueue.offer(task);
    }


    protected void wakeup(boolean inEventLoop){
        if(!inEventLoop || STATE_UPDATER.get(this) == ST_SHUTTING_DOWN){
            /**
             * Use offer as we actually only need this to unblock the thread and if offer fails we do not care as there
             * is already something in the queue
             */
            taskQueue.offer(WAKEUP_TASK);
        }
    }

    /**
     * Updates the internal timestamp that tells when a submitted task wa executed most recently
     * {@link #runAllTasks()} and {@link #runAllTasks(long)} updates this timestamp automatically, and thus there's
     * usually no need to call this method. However, if you take the tasks manualyy using {@link #takeTask()} or
     * {@link }, you have to call this method at the end of the task execution loop for accurate quiet period
     * checks
     */
    protected void updateLastExecutionTime(){
        lastExecutionTime = ScheduledFutureTask.nanoTime();
    }

    @Override
    public boolean isShuttingDown() {
        return STATE_UPDATER.get(this) >= ST_SHUTTING_DOWN;
    }

    protected Runnable takeTask(){
        return null;
    }

    @Override
    public boolean isShutdown() {
        return STATE_UPDATER.get(this) >= ST_SHUTDOWN;
    }

    @Override
    public boolean isTerminated() {
        return STATE_UPDATER.get(this) == ST_TERMINATED;
    }

    /**
     * Confirm that the shutdown if the instance should be done now!
     * @return
     */
    protected boolean confirmShutdown(){
        if(!isShuttingDown()){
            return false;
        }

        if(!inEventLoop()){
            throw new IllegalStateException("must be invoked from an event loop");
        }

        cancelScheduledTasks();

        if(gracefulShutdownStartTime == 0){
            gracefulShutdownStartTime = ScheduledFutureTask.nanoTime();
        }

        if(runAllTasks() || runShutdownHooks()){
            if(isShutdown()){
                // Executor shut down - no new tasks anymore
                return true;
            }

            /**
             * There were tasks in the queue. Wait a little bit more until no tasks are queued for the quiet period or
             * terminate if the quiet period is 0.
             * See https://github.com/netty/netty/issues/4241
             */
            if(gracefulShutdownQuietPeriod == 0){
                return true;
            }

            wakeup(true);
            return false;
        }

        final long nanoTime = ScheduledFutureTask.nanoTime();

        if(isShutdown() || nanoTime - gracefulShutdownStartTime > gracefulShutdownTimeout){
            return true;
        }

        if(nanoTime - lastExecutionTime <= gracefulShutdownQuietPeriod){
            // Check if any tasks were added to queue every 100ms
            // TODO: Change the behavior of taskTask() so that it returns on timeout
            wakeup(true);

            try{
                Thread.sleep(100);
            }catch (InterruptedException e){
                // Ignore
            }
            return false;
        }

        /**
         * No tasks were added for last quiet period - hopefully safe to shut down
         * (Hopefully because we really cannot make a guarantee that there will be no executed() calls by a user)
         */
        return true;

    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if(unit == null){
            throw new NullPointerException("unit");
        }

        if(inEventLoop()){
            throw new IllegalStateException("cannot await termination of the current thread");
        }

        if(threadLock.tryAcquire(timeout, unit)){
            threadLock.release();
        }

        return isTerminated();
    }

    protected void addTask(Runnable task){

    }

    protected boolean wakesUpForTask(Runnable task){
        return true;
    }

    protected boolean removeTask(Runnable task){
        if(task == null){
            throw new NullPointerException("task");
        }
        return taskQueue.remove(task);
    }

    @Override
    public void execute(Runnable task){
        if(task == null){
            throw new NullPointerException("task");
        }

        boolean inEventLoop = inEventLoop();
        if(inEventLoop){
            addTask(task);
        }else{
            startThread();
            addTask(task);
            if(isShutdown() && removeTask(task)){
                reject();
            }
        }

        if(!addTaskWakeUp && wakesUpForTask(task)){
            wakeup(inEventLoop);
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throwIfInEventLoop("invokeAny");
        return super.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throwIfInEventLoop("invokeAny");
        return super.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throwIfInEventLoop("invokeAll");
        return super.invokeAll(tasks);
    }

    @Override
    public <T> List<java.util.concurrent.Future<T>> invokeAll(
            Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return super.invokeAll(tasks, timeout, unit);
    }

    private void throwIfInEventLoop(String method){
        if(inEventLoop()){
            throw new RejectedExecutionException("Calling " + method + " from within the EventLoop is not allowed");
        }
    }

    /**
     * Return the {@link ThreadProperties} of the {@link Thread} that powers the {@link SingleThreadEventExecutor}
     * @return
     */
    public final ThreadProperties threadProperties(){
        return threadProperties;
    }

    protected boolean wakeUpForTask(Runnable task){
        return true;
    }

    protected static void reject(){
        throw new RejectedExecutionException("event executor terminated");
    }

    // ScheduledExecutorService implementation
    private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1);

    private void startThread(){
        if(STATE_UPDATER.get(this) == ST_NOT_STARTED){
            if(STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)){
                thread.start();
            }
        }
    }

    static final class DefaultThreadProperties implements ThreadProperties{

        private Thread t;

        public DefaultThreadProperties(Thread t) {
            this.t = t;
        }

        @Override
        public Thread.State state() {
            return t.getState();
        }

        @Override
        public boolean isInterrupted() {
            return t.isInterrupted();
        }

        @Override
        public boolean isDaemon() {
            return t.isDaemon();
        }

        @Override
        public String name() {
            return t.getName();
        }

        @Override
        public long id() {
            return t.getId();
        }

        @Override
        public StackTraceElement[] stackTrace() {
            return t.getStackTrace();
        }

        @Override
        public boolean isAlive() {
            return t.isAlive();
        }
    }

}
