package com.lami.tuomatuo.search.base.curator.count;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/4/18.
 */
public class DistributedAtomicLongExample {

    private static final Logger logger = Logger.getLogger(DistributedAtomicLongExample.class);

    private static int QTY = 5;
    private static final String PATH = "/examples/counter";

    public static void main(String[] args) {
        try {
            CuratorFramework client =  CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
            client.start();

            List<DistributedAtomicLong> examples = Lists.newArrayList();
            ExecutorService service = Executors.newFixedThreadPool(QTY);
            for(int i = 0; i < QTY; ++i){
                final DistributedAtomicLong count = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 10));
                examples.add(count);

                Callable<Void> task = new Callable<Void>() {
                    public Void call() throws Exception {
                        AtomicValue<Long> value = count.increment();
                        logger.info("succeed: " + value.succeeded());
                        if (value.succeeded()){
                            logger.info("Increment: from " + value.preValue() + " to " + value.postValue());
                        }
                        return null;
                    }
                };
                service.submit(task);
            }

            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }
    }

}
