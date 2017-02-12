package com.lami.tuomatuo.search.base.concurrent.cyclicbarrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CyclicBarrier test demo
 *
 * Created by xjk on 2/12/17.
 */
public class Solver {

    final int N;
    final float[][] data;
    final CyclicBarrier barrier;

    public Solver(float[][] matrix) {
        data = matrix;
        N = matrix.length;
        Runnable barrierAction =
                new Runnable() {
                    @Override
                    public void run() {
                        // mergeRows(...);
                    }
                };
        barrier = new CyclicBarrier(N, barrierAction);

        List<Thread> threads = new ArrayList<>(N);
        for(int i = 0; i < N; i++){
            Thread thread = new Thread(new Worker(i));
            threads.add(thread);
            thread.start();
        }

        // wait until done
        for(Thread thread : threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Worker implements Runnable{

        AtomicInteger count = new AtomicInteger();
        int myRow;

        public Worker(int myRow) {
            this.myRow = myRow;
        }

        private boolean done(){
            return count.incrementAndGet() >= 1000;
        }

        private void processRow(int myRow){
            count.incrementAndGet();
        }

        @Override
        public void run() {
            while(!done()){
                processRow(myRow);

                try{
                    barrier.await();
                }catch (InterruptedException ex){
                    return;
                }catch (BrokenBarrierException ex){
                    return;
                }
            }
        }
    }

}
