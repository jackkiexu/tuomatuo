package com.lami.tuomatuo.search.base.concurrent.threadpoolexecutors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xjk on 11/12/16.
 */
public class KThreadPoolExecutor extends AbstractExecutorService {

    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING      = -1 << COUNT_BITS;
    private static final int SHUTDOWN     = 0 << COUNT_BITS;
    private static final int STOP         = 1 << COUNT_BITS;
    private static final int TIDYING      = 2 << COUNT_BITS;
    private static final int TERMINATED   = 3 << COUNT_BITS;

    // packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }

    private static final boolean ONLY_ONE = true;

    private static boolean runStateLessThan(int c, int s) { return c < s; }

    private static boolean runStateAtLeast(int c, int s) { return c >= s; }

    private static boolean isRunning(int c) { return c < SHUTDOWN; }

    private boolean comparedAndIncrementWorkerCount(int expect) { return ctl.compareAndSet(expect, expect + 1); }

    private boolean compareAndDecrementWorkerCount(int expect) { return ctl.compareAndSet(expect, expect - 1); }

    private void decrementWorkerCount(){
        do{}while(!compareAndDecrementWorkerCount(ctl.get()));
    }

    private BlockingQueue<Runnable> workQueue;

    private final ReentrantLock mainLock = new ReentrantLock();

    private final HashSet<Worker> workers = new HashSet<Worker>();

    private final Condition termination = mainLock.newCondition();

    private int largestPoolSize;

    private long completedTaskCount;

    private volatile ThreadFactory threadFactory;

    private volatile RejectedExecutionHandler handler;

    private volatile long keepAliveTime;

    private volatile boolean allowCoreThreadTimeOut;

    private volatile int corePoolSize;

    private volatile int maximumPoolSize;

    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();

    private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");

    public interface RejectedExecutionHandler {
        void rejectedExecution(Runnable r, AbstractExecutorService executor);
    }


    private final class Worker extends AbstractQueuedSynchronizer implements Runnable{

        private static final long serialVersionUID = 6138294804551838833L;

        Thread thread;

        Runnable firstTask;

        volatile long completedTasks;

        public Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        public void run() {
            runWorker(this);
        }

        protected boolean isHeldExclusively(){
            return getState() != 0;
        }

        protected boolean tryAcquire(int unused){
            if(compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused){
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()          { acquire(1); }
        public boolean tryLock()    { return tryAcquire(1); }
        public void unlock()        { release(1); }
        public boolean isLocked()   { return isHeldExclusively(); }

        void interruptedIfStarted(){
            Thread t;
            if(getState() >= 0 && (t = thread) != null && !t.isInterrupted()){
                try {
                    t.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ThreadFactory getThreadFactory(){
        return threadFactory;
    }

    private void advanceRunState(int targetState){
        for(;;){
            int c = ctl.get();
            if(runStateAtLeast(c, targetState) || ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c)))){
                break;
            }
        }
    }

    final void tryTerminate(){
        for(;;){
            int c = ctl.get();
            if(isRunning(c) || runStateAtLeast(c, TIDYING) || (runStateOf(c) == SHUTDOWN && !workQueue.isEmpty())){
                return;
            }
            if(workerCountOf(c) != 0){
                interruptIdleWorkers(ONLY_ONE);
                return;
            }

            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();

            try{
                if(ctl.compareAndSet(c, ctlOf(TIDYING, 0))){
                    try{
                        terminated();
                    }finally {
                        ctl.set(ctlOf(TERMINATED, 0));
                        termination.signalAll();
                    }
                    return;
                }
            }finally {
                mainLock.unlock();
            }

        }
    }

    private void checkShutdownAccess(){
        SecurityManager security = System.getSecurityManager();
        if(security != null){
            security.checkPermission(shutdownPerm);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try{
                for(Worker w : workers){
                    security.checkAccess(w.thread);
                }
            }finally {
                mainLock.unlock();
            }
        }
    }

    private void interruptWorker(){
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for(Worker w : workers){
                w.interruptedIfStarted();
            }
        }finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers(boolean onlyOne){
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try{
            for(Worker w : workers){
                Thread t = w.thread;
                if(!t.isInterrupted() && w.tryLock()){
                    try {
                        t.interrupt();
                    }catch (Exception e){

                    }finally {
                        w.unlock();
                    }
                }

                if(onlyOne) break;
            }
        }finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers(){
        interruptIdleWorkers(false);
    }

    void reject(Runnable command){
        handler.rejectedExecution(command, this);
    }

    boolean isRunnableOrShutdown(boolean shutdownOK){
        int rs = runStateOf(ctl.get());
        return rs == RUNNING || (rs == SHUTDOWN && shutdownOK);
    }

    private List<Runnable> drainQueue(){
        BlockingQueue<Runnable> q = workQueue;
        ArrayList<Runnable> taskList = new ArrayList<Runnable>();
        q.drainTo(taskList);
        if(!q.isEmpty()){
            for(Runnable r : q.toArray(new Runnable[0])){
                if(q.remove(r)){
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }

    private boolean addWorker(Runnable firstTask, boolean core){
        retry:
        for(;;){
            int c = ctl.get();
            int rs = runStateOf(c);

            if(rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())){
                return false;
            }

            for(;;){
                int wc = workerCountOf(c);
                if(wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize)){
                    return false;
                }
                if(comparedAndIncrementWorkerCount(c)){
                    break retry;
                }
                c= ctl.get();
                if(runStateOf(c) != rs){
                    continue  retry;
                }
            }
        }

        boolean workerStated = false;

        boolean workerAdded = false;
        Worker w = null;

        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if(t != null){
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    int rs = runStateOf(ctl.get());
                    if(rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)){
                        if(t.isAlive()){
                            throw new IllegalThreadStateException();
                        }
                        workers.add(w);
                        int s = workers.size();
                        if(s > largestPoolSize){
                            largestPoolSize = s;
                        }
                        workerAdded = true;
                    }

                }finally {
                    mainLock.lock();
                }
                if(workerAdded){
                    t.start();
                    workerStated = true;
                }
            }
        }finally {
            if(!workerStated){
                addWorkerFailed(w);
            }
        }

        return workerStated;
    }


    private void addWorkerFailed(Worker w){
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if(w != null){
                workers.remove(w);
            }
            decrementWorkerCount();
            tryTerminate();
        }finally {
            mainLock.unlock();
        }
    }

    protected void terminated() {}

    final void runWorker(Worker w){

    }

    public void shutdown() {

    }

    public List<Runnable> shutdownNow() {
        return null;
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void execute(Runnable command) {

    }

    public static class AbortPolicy implements RejectedExecutionHandler{

        public AbortPolicy(){}

        public void rejectedExecution(Runnable r, AbstractExecutorService executor) {
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
        }
    }


}
