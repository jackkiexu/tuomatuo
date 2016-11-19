package com.lami.tuomatuo.search.base.concurrent.completefuture;

import org.apache.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        CompletableFuture<Integer> f = compute();
        class Client extends Thread{
            CompletableFuture<Integer> f;

            public Client(String threadName, CompletableFuture<Integer> f) {
                super(threadName);
                this.f = f;
            }

            @Override
            public void run() {
                try {
                    logger.info("ThreadName:" + this.getName() + " : " + f.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        new Client("Client1", f).start();
        new Client("Client2", f).start();

        f.complete(100);
        System.in.read();

    }


}
