package com.lami.tuomatuo.search.base.curator.count;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2016/4/18.
 */
public class SharedCounterExample implements SharedCountListener {

    private static final Logger logger = Logger.getLogger(SharedCounterExample.class);
    private static final int QTY = 5;
    private static final String PATH = "/examples/counter";

    public static void main(String[] args) throws Exception{
        final Random random = new Random();
        SharedCounterExample example = new SharedCounterExample();
        CuratorFramework client =  CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();

        SharedCount baseCount = new SharedCount(client, PATH, 0);
        baseCount.addListener(example);
        baseCount.start();

        List<SharedCount> examples = Lists.newArrayList();
        ExecutorService service = Executors.newFixedThreadPool(QTY);
        for(int i = 0; i < QTY; ++i){
            final SharedCount count = new SharedCount(client, PATH, 0);
            examples.add(count);
            Callable<Void> task = new Callable<Void>() {
                public Void call() throws Exception {
                    count.start();
                    Thread.sleep(random.nextInt(10000));
                    logger.info("Increment:" + count.trySetCount(count.getVersionedValue(), count.getCount() + random.nextInt(10)));
                    return null;
                }
            };
            service.submit(task);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.MINUTES);

        for(int i = 0; i < QTY; ++i){
            examples.get(i).close();
        }
        baseCount.close();
    }

    public void countHasChanged(SharedCountReader sharedCountReader, int i) throws Exception {
        logger.info("Counter's value is changed to " + i);
    }

    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        logger.info("State changed:" + connectionState.toString());
    }
}
