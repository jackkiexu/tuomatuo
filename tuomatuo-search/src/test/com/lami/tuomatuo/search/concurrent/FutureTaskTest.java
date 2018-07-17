package com.lami.tuomatuo.search.concurrent;

import com.lami.tuomatuo.search.base.concurrent.future.KFutureTask;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by xjk on 12/18/16.
 */
public class FutureTaskTest {

    @Test
    public void enqueue(){
        KFutureTask<?> futureTask = new KFutureTask<>();

        futureTask.enqueue(new KFutureTask.WaiterNode(Thread.currentThread()));
    }

    @Test
    public void testThrowException(){

        Integer a = null;
        if(a > 0){
            System.out.println(" a > 0");
        } else {
            System.out.println(" a > 0 == false");
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();

        List<Future> futureList = new ArrayList<>();
        for(int i = 0; i < 1; i++){
            Future  future = executor.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    int a = 100;
                    if(a < 1000){
                        System.out.println("System.out.println");
                        throw new Exception("My First Exception");
                    }
                    return null;
                }
            });

            futureList.add(future);
        }

        executor.shutdown();
        for(Future future : futureList){
            try {
                System.out.println("Future.get() begin .............");
                System.out.println("future.get():" + future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
