package com.lami.tuomatuo.search.base.concurrent.completefuture;

import org.apache.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xjk on 11/19/16.
 */
public class CompletableFutureTest {

    static Logger logger = Logger.getLogger(CompletableFutureTest.class);

    public static CompletableFuture<Integer> compute(){
        final CompletableFuture<Integer> future = new CompletableFuture<>();
        return future;
    }

    public static void main(String[] args) throws Exception{
        ExecutorService service = Executors.newCachedThreadPool();
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> System.out.println("running async task"), service);
        //utility testing method
        pauseSeconds(1);
        System.out.printf(" result:" + runAsync.isDone());

    }

    private static void pauseSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
