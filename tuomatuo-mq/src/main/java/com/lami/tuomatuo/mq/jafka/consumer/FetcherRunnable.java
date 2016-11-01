package com.lami.tuomatuo.mq.jafka.consumer;

import com.github.zkclient.ZkClient;
import com.lami.tuomatuo.mq.jafka.api.OffsetRequest;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkGroupTopicDirs;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by xjk on 10/31/16.
 */
@ClientSide
public class FetcherRunnable extends Thread {

    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private SimpleConsumer simpleConsumer;

    private volatile boolean stopped = false;

    private ConsumerConfig config;

    private Broker broker;

    private ZkClient zkClient;

    private List<PartitionTopicInfo> partitionTopicInfos;


    public FetcherRunnable(String name, ZkClient zkClient, ConsumerConfig config, Broker broker,  List<PartitionTopicInfo> partitionTopicInfos) {
        super(name);
        this.zkClient = zkClient;
        this.config = config;
        this.broker = broker;
        this.partitionTopicInfos = partitionTopicInfos;
        this.simpleConsumer = new com.lami.tuomatuo.mq.jafka.consumer.SimpleConsumer(broker.host, broker.port, config.getSocketTimeoutMs(), config.getSocketBufferSize());
    }

    public void shutdown() throws InterruptedException{
        stopped = true;
        this.interrupt();
        shutdownLatch.await();
    }

    private void shutdownComplate(){
        this.shutdownLatch.countDown();
    }

    private long resetConsumerOffsets(String topic, Partition partition) throws IOException{
        long offset = -1;
        String autoOffsetReset = config.getAutoOffsetReset();
        if(OffsetRequest.SmallestTimeString.equals(autoOffsetReset)){
            offset = OffsetRequest.EarliesTime;
        }else if(OffsetRequest.LargestTimeString.equals(autoOffsetReset)){
            offset = OffsetRequest.LatestTime;
        }

        final ZkGroupTopicDirs topicDirs = new ZkGroupTopicDirs(config.getGroupId(), topic);
        long[] offsets = simpleConsumer.getOffsetsBefore(topic, partition.partId, offset, 1);
        ZkUtils.updatePersistentPath(zkClient, topicDirs.consumerOffsetDir + "/" + partition.getName(), "" + offsets[0]);
        return offsets[0];
    }

    private long processMessages(ByteBufferMessageSet messages, PartitionTopicInfo info) throws IOException, InterruptedException{
        return 0;
    }

}
