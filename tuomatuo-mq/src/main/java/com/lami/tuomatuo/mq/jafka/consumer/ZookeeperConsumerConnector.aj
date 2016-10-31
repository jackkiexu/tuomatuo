package com.lami.tuomatuo.mq.jafka.consumer;

import com.github.zkclient.ZkClient;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Decoder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xjk on 10/31/16.
 */
public class ZookeeperConsumerConnector implements ConsumerConnector {

    private static final Logger logger = Logger.getLogger(ZookeeperConsumerConnector.class);

    public static final FetchedDataChunk SHUTDOWN_COMMAND = new FetchedDataChunk(null, null, -1);

    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    private final Object rebalanceLock = new Object();

    private Fetcher fetcher;

    private ZkClient zkClient;


    public ZookeeperConsumerConnector(ConsumerConfig config) {
        this(config, true);
    }

    public ZookeeperConsumerConnector(ConsumerConfig config, boolean enableFetcher) {

    }


    public <T> Map<String, List<MessageStream<T>>> createMessageStreams(Map<String, Integer> topicCountMap, Decoder<T> decoder) {
        return null;
    }

    public void commitOffsets() {

    }

    public void close() throws IOException {

    }
}
