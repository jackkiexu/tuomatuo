package com.lami.tuomatuo.search.base.curator.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.DistributedPriorityQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2016/4/14.
 */
public class queue {

    private static final Logger logger = Logger.getLogger(queue.class);

    private static final String PATH = "/tmp/2016041801";

    public static void main(String[] args) throws Exception{
        CuratorFramework client = null;
        DistributedPriorityQueue<String> queue = null;

        try {
            client = CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
            client.getCuratorListenable().addListener(new CuratorListener() {
                public void eventReceived(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                    logger.info("CuratorEvent:" + curatorEvent.getType().name());
                }
            });
            client.start();
            QueueConsumer<String> consumer = createQueueConsumer();
            QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, createQueueSerializer(), PATH);
            queue = builder.buildPriorityQueue(0);
            queue.start();

            for(int i=0; i < 10; i++){
                int priority = (int)(Math.random()*100);
                logger.info("test - " + i + " priority:" + priority);
                queue.put("test-" + i, priority);
                Thread.sleep((long)(50*Math.random()));
            }
            Thread.sleep(20000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(queue);
            CloseableUtils.closeQuietly(client);
        }
    }

    private static QueueSerializer<String> createQueueSerializer(){
        return new QueueSerializer<String>() {
            public byte[] serialize(String s) {
                return s.getBytes();
            }

            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        };
    }

    private static QueueConsumer<String> createQueueConsumer(){
        return new QueueConsumer<String>() {
            public void consumeMessage(String s) throws Exception {
                logger.info("consume one message :" + s);
            }

            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                logger.info("connection new state:" + connectionState);
            }
        };
    }
}
