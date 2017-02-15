package com.lami.tuomatuo.search.base.concurrent.cyclicbarrier;

import io.netty.util.Timeout;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * http://www.cnblogs.com/go2sea/p/5615531.html
 *
 * A synchronization aid that allows a set of threads to all wait for
 * each other to reach a common barrier point. CyclicBarriers are
 * useful in programs involving a fixed sized party of threads that
 * must occasionally wait for each other. The barrier is called
 * <em>cyclic</em> because it can be re-used after the waiting threads
 * are released
 *
 * <p>
 *     A {@code KCyclicBarrier} supports an optional {@link Runnable} command
 *     that is run once per barrier point, after the last thread in the party
 *     arrives, but before any threads are released
 *     This <em>barrier action</em> is useful
 *     for updating shared-state before any of the parties continue
 * </p>
 *
 * <p>
 *     Sample usage. Here is an example of using a barrier in a parallel decomposition design
 * </p>
 *
 * <pre>
 *     {@code
 *          class Solver {
 *              final int N;
 *              final float[][] data;
 *              final CyclicBarrier barrier;
 *
 *              class Worker implement Runnable {
 *                  int myRow;
 *
 *                  Worker(int row) {
 *                      myRow = row;
 *                  }
 *
 *                  public void run(){
 *                      while(!done()){
 *                          processRow(myRow);
 *
 *                          try{
 *
 *                          }catch(InterryptedException ex){
 *                              return;
 *                          }catch(BrokenBarrierException ex){
 *                              return;
 *                          }
 *                      }
 *                  }
 *              }
 *
 *              public Solver(float[][] matrix){
 *                  data = matrix;
 *                  N = matrix.length;
 *                  Runnable barrierAction =
 *                      new Runnable() {public void run() {mergeRows(....); }}
 *                  barrier = new CyclicBarrier(N, barrierAction);
 *
 *                  List<Thread> threads = new ArryaList<Thread>(N);
 *
 *                  for(int i = 0; i < N ; i++){
 *                      Thread thread = new Thread(new Worker(i));
 *                      thread.add(thread);
 *                      thread.start();
 *                  }
 *
 *                  // wait until done
 *                  for(Thread thread : threads){
 *                      thread.join();
 *                  }
 *              }
 *          }
 *     }
 * </pre>
 *
 * Here, each worker thread processes a row of the matrix then waits at the
 * barrier until all rows have been processed. When all rows are processed
 * the supplied {@link Runnable} barrier action is executed and merges the
 * rows. If the marger
 *
 * determines that a solution has been found then {@code done()} will return
 * {@code true} and each worker will terminate
 *
 * <p>
 *     If the barrier action does not rely on the parties being suspended when
 *     it is executed. then any of the threads in the party could execute that
 *     action when it is released. To facilitate this, each invocation of
 *     {@link #await} returns the arrival index of that thread at the barrier
 *     You can then choose which thread should execute the barrier action, for
 *     eample
 *     <pre>
 *         {@code
 *          if(barrier.await() == ){
 *              // log the completion of this iteration
 *          }
 *         }
 *     </pre>
 * </p>
 *
 * <p>
 *     The {@code CyclicBarrier} uses an all-or-none breakage model
 *     for failed synchronization attempts; If a thread leaves a barrier
 *     point prematurely because of interruption, failure, or timeout, all
 *     other threads waiting at that barrier point will also leave
 *     abnormally via {@link BrokenBarrierException} (or
 *     {@link InterruptedException}) if they too were interrupted at about
 *     the same time
 * </p>
 *
 * <p>
 *     Memory consistency effect: Actions in a thread prior calling
 *     {@code await()}
 *     happen-before
 *     action that are part of the barrier action, which in turn
 *     <i>happen-before</i> action following a successful return from the
 *     corresponding {@code await()} in other threads
 * </p>
 *
 * Created by xjk on 1/29/17.
 */
public class KCyclicBarrier {

    /**
     * Each use of barrier is represented as a generation instance.
     * The generation chenges whenever the barrier is tripped, or
     * is reset. There can be many generations associated with threads
     * using the barrier - due to the non-deterministic way the lock
     * can be active at a time (the one to which {@code count} applies)
     * and all the rest are either broken or tripped
     * There need not be an active generation if there has been a break
     * but no subsequent reset
     */
    /**
     * 每个 Generation 代表的是一个等待事件,
     * 当每次所有线程都到达 barrier/ 或其中的线程被中断/ 其中某个线程等待超时,
     * 则 broken 有可能会改变, 并且会生成新的 Generation
     */
    private static class Generation{
        boolean broken = false;
    }

    /** The lock for guarding barrier entry */
    /** 全局的重入 lock */
    private final ReentrantLock lock = new ReentrantLock();
    /** Condition to wait on until tripped */
    /** 控制线程等待  */
    private final Condition trip = lock.newCondition();
    /** The number of parties */
    /** 参与到这次 barrier 的参与者个数 */
    private final int parties;
    /** The command to run when tripped */
    /** 到达 barrier 时执行的command */
    private final Runnable barrierCommand;
    /** The current generation */
    /** 初始化 generation */
    private Generation generation = new Generation();

    /**
     * Number of parties still waiting, Counts down from parties to 0
     * on each generation. It is reset to parties on each new
     * generation or when broken.
     */
    /** 还没到达 barrier 的参与者线程个数, 每次所有线程到达后, 或其中有个线程等待超时或被中断, 则count重新赋值 */
    private int count;

    /**
     * Updates state on barrier trip and wakes up everyone.
     * Called only while holding lock.
     */
    /** 生成下一个 generation */
    private void nextGeneration(){
        // signal completion of last generation
        // 唤醒所有等待的线程来获取 AQS 的state的值
        trip.signalAll();
        // set up next generation
        // 重新赋值计算器
        count = parties;
        // 重新初始化 generation
        generation = new Generation();
    }


    /**
     * Sets current barrier generation as broken and wakes up everyone
     * Called only while holding lock
     */
    /** 当某个线程被中断 / 等待超时 则将 broken = true, 并且唤醒所有等待中的线程 */
    private void breakBarrier(){
        generation.broken = true;
        count = parties;
        trip.signalAll();
    }


    /**
     * Main barrier code, covering the various policies
     */
    /**
     * CyclicBarrier 的核心方法, 主要是所有线程都获取一个 ReeantrantLock 来控制
     * 主要步骤:
     *      1)
     *      2)
     */
    private int dowait(boolean timed, long nanos)throws InterruptedException, BrokenBarrierException, TimeoutException{
        final ReentrantLock lock = this.lock;
        lock.lock();                            // 1. 获取 ReentrantLock
        try{
            final Generation g = generation;

            if(g.broken){                       // 2. 判断 generation 是否已经 broken
                throw new BrokenBarrierException();
            }

            if(Thread.interrupted()){           // 3. 判断线程是否中断, 中断后就 breakBarrier
                breakBarrier();
                throw new InterruptedException();
            }

            int index = --count;                // 4. 更新已经到达 barrier 的线程数
            if(index == 0){ // triped           // 5. index == 0 说明所有线程到达了 barrier
                boolean ranAction = false;
                try{
                    final Runnable command = barrierCommand;
                    if(command != null){        // 6. 最后一个线程到达 barrier, 执行 command
                        command.run();
                    }
                    ranAction = true;
                    nextGeneration();           // 7. 更新 generation
                    return 0;
                }finally {
                    if(!ranAction){
                        breakBarrier();
                    }
                }
            }

            // loop until tripped, broken, interrupted, or timed out
            for(;;){
                try{
                    if(!timed){
                        trip.await();           // 8. 没有进行 timeout 的 await
                    }else if(nanos > 0L){
                        nanos = trip.awaitNanos(nanos); // 9. 进行 timeout 方式的等待
                    }
                }catch (InterruptedException e){
                    if(g == generation && !g.broken){ // 10. 等待的过程中线程被中断, 则直接唤醒所有等待的 线程, 重置 broken 的值
                        breakBarrier();
                        throw e;
                    }else{
                        /**
                         * We're about to finish waiting even if we had not
                         * been interrupted, so this interrupt is deemed to
                         * "belong" to subsequent execution
                         */
                        /**
                         * 情况
                         *  1. await 抛 InterruptedException && g != generation
                         *      所有线程都到达 barrier(这是会更新 generation), 并且进行唤醒所有的线程; 但这时 当前线程被中断了
                         *      没关系, 当前线程还是能获取 lock, 但是为了让外面的程序知道自己被中断过, 所以自己中断一下
                         *  2. await 抛 InterruptedException && g == generation && g.broken = true
                         *      其他线程触发了 barrier broken, 导致 g.broken = true, 并且进行 signalALL(), 但就在这时
                         *      当前的线程也被 中断, 但是为了让外面的程序知道自己被中断过, 所以自己中断一下
                         *
                         */
                        Thread.currentThread().interrupt();
                    }
                }



                if(g.broken){                       // 11. barrier broken 直接抛异常
                    throw new BrokenBarrierException();
                }

                if(g != generation){                 // 12. 所有线程到达 barrier 直接返回
                    return index;
                }

                if(timed && nanos <= 0L){           // 13. 等待超时直接抛异常, 重置 generation
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        }finally {
            lock.unlock();                          // 14. 调用 awaitXX 获取lock后进行释放lock
        }
    }


    /**
     * Creates a new {@code KCyclicBarrier} that will trip when the
     * given number of parties (threads) are waiting upon it, and which
     * will execute the given barrier action when the barrier is trriped.
     *  performed by the last thread entering the barrier
     *
     * @param parties the number of threads that must invoke {@code #await}
     * @param barrierCommand the command to execute when the barrier is
     *                       tripped, or {@code null} if there is no action
     * @throws IllegalArgumentException if {@code parties} is less than 1
     */

    /**
     * 指定 barrierCommand 的构造 KCyclicBarrier
     */
    public KCyclicBarrier(int parties, Runnable barrierCommand) {
        if(parties <= 0) throw new IllegalArgumentException();
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierCommand;
    }


    /**
     * Creates a new {@code KCyclicBarrier} that will trip then the
     * given number of parties (threads) are waiting upon it, and
     * does not perform a predefined action when the barrier is tripped
     *
     * @param parties the number of threads that must invoke {@link #"awiat}
     *                before the barrier is tripped
     * @throws IllegalArgumentException if {@code parties} is less than 1
     */
    /**
     * 构造 CyclicBarrier
     */
    public KCyclicBarrier(int parties){
        this(parties, null);
    }

    /**
     * Returns the number of parties required to trip this barrier
     *
     * @return the number of partis required to trip this barrier
     */
    /**
     * 返回 barrier 的参与者数目
     */
    public int getParties(){
        return parties;
    }


    /**
     * Waits until all {@link #getParties() parties} have invoked
     * {@code await} on this barrier
     *
     * <p>
     *     If the current thread is not the last to arrive then it is
     *     disabled for thread scheduling purposes and lies dormant until
     *     one of the following things happens:
     *     <li>
     *         The last thread arrives; or
     *         Some other thread {@link Thread#interrupt() interrupts}
     *         the current thread; or
     *         Some other thread {@link Thread#interrupt() interrupts}
     *         one of the other waiting threads; or
     *         Some other thread times out while waiting for barrier; or
     *         Some other thread invokes {@link #"reset} on this barrier.
     *     </li>
     * </p>
     *
     * <p>
     *      If the current thread:
     *      has its interrupted status set on entry to this method; or
     *      is {@link Thread#interrupt() interrupted} while waiting
     *      then {@link InterruptedException} is thrown and current thread's
     *      interrupted status is cleared
     * </p>
     *
     * <p>
     *     If any current thread is the last thread to arrive, and a
     *     non-null barrier action was supplied in the constructor, then the
     *     current thread runs the action before allowing the other threads to
     *     continue.
     *     If an exception occurs during the barrier action then that exception
     *     will be propagated in the current thread and the barrier is placed in
     *     the broken state.
     * </p>
     *
     * @return the arrival index of the current thread, where index
     *          {@code getParties() -1} indicates the first
     *          to arrive and zero indicates the last to arrive
     * @throws InterruptedException ig the current thread was interrupted while waiting
     * @throws BrokenBarrierException if <em>another</em> thread was
     *                  interrupted or timed out while the current thread was
     *                  waiting, or the barrier was reset, or the barrier was
     *                  broken when {@code await} was called, or the barrier
     *                  action (if present) failed due to an exception
     */
    /**
     * 进行等待所有线程到达 barrier
     * 除非: 其中一个线程被 inetrrupt
     */
    public int await() throws InterruptedException, BrokenBarrierException{
        try{
            return dowait(false, 0L);
        }catch (TimeoutException toe){
            throw new Error(toe); // cannot happen
        }
    }

    /**
     * Waits until all {@link #getParties()} have invoked
     * {@code await} on this barrier, or the specified waiting time elapses
     *
     * <p>
     *     If the current thread is not the last to arrive then it is
     *     disable for thread scheduling purposes and lies dormant until
     *     one of the following things happens
     * </p>
     *
     * <p>
     *     <li>
     *         The last thread arrives; or
     *         The specified timeout elapses; or
     *         Some other thread {@link Thread#interrupt() interrupts}
     *         the current thread; or
     *         Some other thread {@link Thread#interrupt() interrupts}
     *         one of the other waiting threads; or
     *         Some other thread times out while waiting for barrier; or
     *         Some other thread invokes {@link #reset()} on this barrier
     *     </li>
     * </p>
     *
     * <p>
     *     If the current thread:
     *     has its interrupted status set on entry to this method; or
     *     is {@link Thread#interrupt() interrupted} while waiting
     *     then {@link Thread#interrupt() interrupted} while waiting
     *     then {@link InterruptedException} is thrown and the current thread's
     *     interrupted status is cleared
     * </p>
     *
     * <p>
     *     if the specified waiting time elapses then {@link TimeoutException}
     *     is thrown. If the time is less than or equal to zero, the method will not wait at all
     * </p>
     *
     * <p>
     *     If the barrier is {@link #reset} while any thread is wating
     *     or if the barrier {@link #isBroken() is broken} when
     *     {@code await} is invoked, or while any thread is waiting, then
     *     {@link BrokenBarrierException} is thrown
     * </p>
     *
     * <p>
     *     If any thread is {@link Thread#interrupt() interrupted} while
     *     waiting, then all other waiting threads will throw {@link BrokenBarrierException}
     *     and the barrier is placed in the broken state
     * </p>
     *
     * <p>
     *     If the current thread is the last thread to arrive, and a non-null
     *     barrier action was supplied in the constructor, then the
     *     current thread runs the action before allowing the other threads to cintinue
     *
     *     If an exception occurs during the barrier action then that exception
     *     will be propagated in the current thread and the barrier is placed in
     *     the broken state
     * </p>
     *
     *
     * @param timeout the time to wait for the barrie
     * @param unit the time unit of the timeout parameter
     * @return the arrival index of the current thread, where index
     *              {@code getParties -1} indicates the first
     *              to arrive and zero indicates the last to arrive
     * @throws Exception InterruptedException if the current thread was interrupted
     *              while waiting
     * @throws TimeoutException if the specified timeout elapses.
     *              In this case the barrier will be broken
     * @throws BrokenBarrierException if <em>another</em> thread was
     *              interrupted or timed out while the current thread wad
     *              waiting, or the barrier was reset, or the barrier was broken
     *              when {@code await} was called, or the barrier action (if
     *              present) failed due to an exception
     */
    /**
     * 进行等待所有线程到达 barrier
     * 除非: 等待超时
     */
    public int await(long timeout, TimeUnit unit) throws Exception{
        return dowait(true, unit.toNanos(timeout));
    }

    /**
     * Queries if this barrier is in a broken state
     *
     * @return {@code true} if one or more parties broken put of this
     *          barrier due to interruption or timeout since
     *          construction or the last reset, or a barrier action
     *          failed due to an exception; {@code false} otherwise
     */
    /**
     * 判断 barrier 是否 broken = true
     */
    public boolean isBroken(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return generation.broken;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Resets the barrier to its initial state, If any parties are
     * currently waiting at the barrier, they will return with a
     * {@link BrokenBarrierException}. Note that resets <em>after</em>
     * a breakage has occurred for other reasons can be complicated to
     * carry out; threads needs to re-synchronize in some other way
     * and choose one to perform the reset. It may be preferable to
     * instead create to a new barrier for subsequent use.
     */
    // 重置 barrier
    public void reset(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            breakBarrier();  // break the current generation
            nextGeneration(); // start a new generation
        }finally {
            lock.unlock();
        }
    }

    /**
     * Returns the number of parties currently waiting at the barrier
     * This method is primarily useful for debugging and assertions
     *
     * @return the numbver of parties currently blocked in {@link #await()}s
     */
    /**
     * 获取等待中的线程
     */
    public int getNumberWaiting(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try{
            return parties - count;
        }finally {
            lock.unlock();
        }
    }
}
