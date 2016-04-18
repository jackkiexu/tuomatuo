package com.lami.tuomatuo.search.base.curator.barrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/4/15.
 */
public class DistributedBarrierExample {

    private static final Logger logger = Logger.getLogger(DistributedBarrierExample.class);

    private static int  QTY = 5;
    private static String PATH = "/example/barrier";

    public static void main2(String[] args) throws Exception{
        TestingServer server = new TestingServer();
        CuratorFramework client =  CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();

        ExecutorService service = Executors.newFixedThreadPool(QTY);
        DistributedBarrier controlBarrier = new DistributedBarrier(client, PATH);
        controlBarrier.setBarrier();

        for(int i = 0; i < QTY; i++){
            final DistributedBarrier barrier = new DistributedBarrier(client, PATH);
            final int index = i;
            Callable<Void> task = new Callable<Void>() {
                public Void call() throws Exception {
                    Thread.sleep((long)(3*Math.random()));
                    logger.info("Client # " + index + " waits on Barrier");
                    barrier.waitOnBarrier();
                    logger.info("Client # " + index + " begins");
                    return null;
                }
            };
            service.submit(task);
        }

        Thread.sleep(10*1000);
        logger.info("all Barrier instance should wait the condition");

        controlBarrier.removeBarrier();
        service.shutdown();
        service.awaitTermination(10, TimeUnit.MINUTES);
    }

    public static void main(String[] args) {
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();

        ExecutorService service = Executors.newFixedThreadPool(QTY);
        for(int i = 0; i < QTY; ++i){
            final DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, PATH, QTY);
            final  int index = i;
            Callable<Void> task = new Callable<Void>() {
                public Void call() throws Exception {
                    Thread.sleep((long)(3 * Math.random()));
                    logger.info("Client # " + index + " enters");
                    barrier.enter();
                    logger.info("Client # " + index + " begins");
                    Thread.sleep((long)(3 * Math.random()));
                    barrier.leave();
                    logger.info("Client # " + index + " left");
                    return null;
                }
            };
            service.submit(task);
        }
    }
}
