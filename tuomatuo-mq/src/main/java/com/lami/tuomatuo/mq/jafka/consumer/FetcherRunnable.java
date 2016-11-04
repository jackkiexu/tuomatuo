package com.lami.tuomatuo.mq.jafka.consumer;

import com.github.zkclient.ZkClient;
import com.lami.tuomatuo.mq.jafka.api.FetchRequest;
import com.lami.tuomatuo.mq.jafka.api.MultiFetchRequest;
import com.lami.tuomatuo.mq.jafka.api.MultiFetchResponse;
import com.lami.tuomatuo.mq.jafka.api.OffsetRequest;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;
import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkGroupTopicDirs;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by xjk on 10/31/16.
 */
@ClientSide
public class FetcherRunnable extends Thread {

    private static final Logger logger = Logger.getLogger(FetcherRunnable.class);

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
        this.simpleConsumer = new SimpleConsumer(broker.host, broker.port, config.getSocketTimeoutMs(), config.getSocketBufferSize());
    }

    public void shutdown() throws InterruptedException{
        stopped = true;
        this.interrupt();
        shutdownLatch.await();
    }

    @Override
    public void run() {
        for(PartitionTopicInfo partitionTopicInfo : partitionTopicInfos){
            logger.info(getName() + " start fetching topic: " + partitionTopicInfo + ", from " + broker);
        }

        try {
            while(!stopped){
                if(fetchOnce() == 0){ // read empty bytes
                    logger.info("backing off " + config.getFetchBackOffMs() + " ms");
                    Thread.sleep(config.getFetchBackOffMs());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(stopped){
                logger.info("FetcherRunnable " + this + " interrupted");
            }else{
                logger.info("error in FetcherRunnable ", e);
            }
        }

        logger.info("stopping fetcher " + getName() + " to host " + broker);
        Closer.closeQuietly(simpleConsumer);
        shutdownComplate();
    }

    private long fetchOnce() throws IOException, InterruptedException{
        List<FetchRequest> fetches = new ArrayList<FetchRequest>();
        for(PartitionTopicInfo info : partitionTopicInfos){
            fetches.add(new FetchRequest(info.topic, info.partition.partId, info.getFetchOffset(), config.getFetchSize()));
        }
        MultiFetchResponse response = simpleConsumer.multifetch(fetches);
        int index = 0;
        long read = 0l;
        for(ByteBufferMessageSet messages : response){
            PartitionTopicInfo info = partitionTopicInfos.get(index);
            try {
                read += processMessages(messages, info);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (Exception e){
                if(!stopped){
                    logger.info("error in FetcherRunnable for " + info, e);
                    info.enqueueError(e, info.getFetchOffset());
                }
            }

            index++;
        }
        return read;
    }

    private long processMessages(ByteBufferMessageSet messages, PartitionTopicInfo info) throws IOException, InterruptedException{
        boolean done = false;
        if(messages.getErrorCode() == ErrorMapping.OffsetOutOfRangeCode){
            logger.info("offset for " + info + " out of range, now we fix it");
            long resetOffset = resetConsumerOffsets(info.topic, info.partition);
            if(resetOffset >= 0){
                info.resetFetchOffset(resetOffset);
                info.resetConsumerOffset(resetOffset);
                done = true;
            }
        }
        if(!done){
            return info.enqueue(messages, info.getFetchOffset());
        }
        return 0;
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

}
