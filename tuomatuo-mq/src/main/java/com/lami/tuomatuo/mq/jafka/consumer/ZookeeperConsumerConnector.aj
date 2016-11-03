package com.lami.tuomatuo.mq.jafka.consumer;

import com.github.zkclient.ZkClient;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Decoder;
import com.lami.tuomatuo.mq.jafka.utils.KV;
import com.lami.tuomatuo.mq.jafka.utils.Pool;
import com.lami.tuomatuo.mq.jafka.utils.Scheduler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class handles the consumers interaction with zookeeper
 *
 * Directories:
 * <p>
 *     <b>
 *         1. Consumer id registry
 *     </b>
 *
 *     <pre>
 *         /consumers/[group_id]/ids[consumer_id] - > topic1, ...topicN
 *     </pre>
 *
 *     A consumer has a unique consumer id within a consumer group. A consumer
 *     registers its id as an ephemeral znode and puts all topics that it
 *     subscribes to as the value of the znode. The znode is deleted when the
 *     client is gone. A consumer subscribes to event changes of the consumer
 *     id register within its group
 *
 * </p>
 *
 * <p>
 *     The consumer id is picked up from configuration, instead of the
 *     sequential id assigned by ZK. Fenerated sequential ids are hard to
 *     recover during temporary connection loss to ZK, since it's difficult for
 *     the client to figure out whether the creation of a sequential znode has
 *     succeeded or not. More detail can be found at
 *     (http://wiki.apache.org/hadoop/ZooKeeper/ErrorHanding)
 * </p>
 *
 * <p>
 *     <b>
 *         2. Broker node registry
 *     </b>
 * </p>
 *
 * <pre>
 *     /brokers/[0...N] --> {
 *         "host": "host:port",
 *         "topic": {
 *             "topic1" : ["partition1"... "partitionN"],
 *             ...,
 *             "topicN": ["partition1"... "partitionN"]
 *         }
 *     }
 * </pre>
 *
 * This is a list of all present broker brokers. A unique logical node id
 * is configured on each broker node. A broker node registers itself on
 * start-up and creates a znode with the logical node id under / brokers
 *
 * The value of the znode is JSON String that contains
 *
 * <pre>
 *     (1) the host name and the port the broker is listening to,
 *     (2) a list of topics that the broker servers,
 *     (3) a list of logical partitions assigned to each topic on the broker.
 * </pre>
 *
 * A consumer subscribes to event changes of the broker node registry
 *
 * <p>
 *     <b>3. Partition owner registry</b>
 * </p>
 *
 *
 * <pre>
 *     /consumers/[group_id]/owner/[topic]/[broker_id-partition_id] --> consumer_node_id
 * </pre>
 *
 * This stores the mapping before broker partitions and consumers. Each
 * partition is owned by a unique consumer within a consumer group. The
 * mapping is reestablished after each rebalancing
 *
 * <p>
 *     4. Consumer offset tracking:
 *     <pre>
 *         /consumers/[group_id]/offsets/[topic]/[broker_id-partition_id] --> offset_counter_value
 *     </pre>
 *
 *     Each consumer tracks the offset of the latest message consumed for each
 *     partition
 * </p>
 *
 * Created by xjk on 10/31/16.
 */
public class ZookeeperConsumerConnector implements ConsumerConnector {

    private static final Logger logger = Logger.getLogger(ZookeeperConsumerConnector.class);

    public static final FetchedDataChunk SHUTDOWN_COMMAND = new FetchedDataChunk(null, null, -1);

    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    private final Object rebalanceLock = new Object();

    private Fetcher fetcher;

    private ZkClient zkClient;

    private Pool<String, Pool<Partition, PartitionTopicInfo>> topicRegistry;

    private Pool<KV<String, String>, BlockingQueue<FetchedDataChunk>> queues;

    private final Scheduler scheduler = new Scheduler(1, "consumer-autocommit-", false);


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
