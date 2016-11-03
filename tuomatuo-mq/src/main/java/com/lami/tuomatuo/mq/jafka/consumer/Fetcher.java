package com.lami.tuomatuo.mq.jafka.consumer;

import com.github.zkclient.ZkClient;
import com.lami.tuomatuo.mq.jafka.cluster.Cluster;
import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by xjk on 10/31/16.
 */
@ClientSide
public class Fetcher {

    private static final Logger logger = Logger.getLogger(Fetcher.class);

    private ConsumerConfig config;
    private ZkClient zkClient;

    private volatile List<FetcherRunnable> fetcherThreads = new ArrayList<FetcherRunnable>(0);

    public Fetcher(ConsumerConfig config, ZkClient zkClient) {
        super();
        this.config = config;
        this.zkClient = zkClient;
    }

    public void stopConnectionsToAllBrokers(){
        // shutdown the old fetcher threads, if any
        List<FetcherRunnable> threads = this.fetcherThreads;
        for(FetcherRunnable fetcherThread : threads){
            try {
                fetcherThread.shutdown();
            }catch (InterruptedException e){
                logger.warn(e.getMessage(), e);
            }
        }
        this.fetcherThreads = new ArrayList<FetcherRunnable>(0);
    }

    public <T> void startConnections(Iterable<PartitionTopicInfo> topicInfos, Cluster cluster, Map<String, List<MessageStream<T>>> messageStreams){
        if(topicInfos == null){
            return;
        }

        // re-arrange by broker id
        Map<Integer, List<PartitionTopicInfo>> m = new HashMap<Integer, List<PartitionTopicInfo>>();
        for(PartitionTopicInfo info : topicInfos){
            if(cluster.getBroker(info.brokerId) == null){
                throw new IllegalStateException("Broker " + info.brokerId + " is unavailable, fetchers could not be started");
            }
            List<PartitionTopicInfo> list = m.get(info.brokerId);
            if(list == null){
                list = new ArrayList<PartitionTopicInfo>();
                m.put(info.brokerId, list);
            }
            list.add(info);
        }

        final List<FetcherRunnable> fetcherThreads = new ArrayList<FetcherRunnable>();
        for(Map.Entry<Integer, List<PartitionTopicInfo>> e : m.entrySet()){
            FetcherRunnable fetcherThread = new FetcherRunnable("Fetchrunnable-",
                    zkClient,
                    config,
                    cluster.getBroker(e.getKey().intValue()),
                    e.getValue());
            fetcherThreads.add(fetcherThread);
            fetcherThread.start();
        }

    }

    public <T> void clearFetcherQueues(Collection<BlockingQueue<FetchedDataChunk>> queuesToBeCleared, Collection<List<MessageStream<T>>> messageStreamsList){
        for(BlockingQueue<FetchedDataChunk> q : queuesToBeCleared){
            q.clear();
        }

        for(List<MessageStream<T>> messageStreams : messageStreamsList){
            for(MessageStream<T> ms : messageStreams){
                ms.clear();
            }
        }
    }
}
