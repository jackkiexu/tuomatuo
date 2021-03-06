package com.lami.tuomatuo.mq.jafka.consumer;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkStateListener;
import com.github.zkclient.ZkClient;
import com.github.zkclient.exception.ZkInterruptedException;
import com.lami.tuomatuo.mq.jafka.api.OffsetRequest;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Cluster;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.ConsumerRebalanceFailedException;
import com.lami.tuomatuo.mq.jafka.common.InvalidConfigException;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Decoder;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import com.lami.tuomatuo.mq.jafka.utils.KV;
import com.lami.tuomatuo.mq.jafka.utils.Pool;
import com.lami.tuomatuo.mq.jafka.utils.Scheduler;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkGroupDirs;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkGroupTopicDirs;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkStringSerializer;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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

    public ConsumerConfig config;

    public boolean enableFetcher;

    public ZookeeperConsumerConnector(ConsumerConfig config) {
        this(config, true);
    }

    public ZookeeperConsumerConnector(ConsumerConfig config, boolean enableFetcher) {
        this.config = config;
        this.enableFetcher = enableFetcher;
        //
        this.topicRegistry = new Pool<String, Pool<Partition, PartitionTopicInfo>>();
        this.queues = new Pool<KV<String, String>, BlockingQueue<FetchedDataChunk>>();
        //

        connectZk();
        createFetcher();
        if(this.config.isAutoCommit()){
            logger.info("starting auto committer every " + config.getAutoCommitIntervalMs() + " ms");
            scheduler.scheduleWithRate(new AutoCommitTask(), config.getAutoCommitIntervalMs(), config.getAutoCommitIntervalMs());
        }

    }

    private void createFetcher(){
        if(enableFetcher){
            this.fetcher = new Fetcher(config, zkClient);
        }
    }


    class AutoCommitTask implements Runnable{

        public void run() {
            try {
                commitOffsets();
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("exception during autocommit : " , e);
            }
        }
    }

    public <T> Map<String, List<MessageStream<T>>> createMessageStreams(Map<String, Integer> topicCountMap, Decoder<T> decoder) {
        return consume(topicCountMap, decoder);
    }

    private <T> Map<String, List<MessageStream<T>>> consume(Map<String, Integer> topicCountMap, Decoder<T>decoder){
        if(topicCountMap == null){
            throw new IllegalArgumentException("topicCountMap is null");
        }
        ZkGroupDirs dirs = new ZkGroupDirs(config.getGroupId());
        Map<String, List<MessageStream<T>>> ret = new HashMap<String, List<MessageStream<T>>>();
        String consumerUuid = config.getConsumerId();
        if(consumerUuid == null){
            consumerUuid = generateConsumerId();
        }
        final String consumerIdString = config.getGroupId() + "_" + consumerUuid;
        final TopicCount topicCount = new TopicCount(consumerIdString, topicCountMap);
        for(Map.Entry<String, Set<String>> e : topicCount.getConsumerThreadIdsPerTopic().entrySet()){
            final String topic = e.getKey();
            final Set<String> threadIdSet = e.getValue();
            final List<MessageStream<T>> streamList = new ArrayList<MessageStream<T>>();
            for(String threadId : threadIdSet){
                LinkedBlockingQueue<FetchedDataChunk> stream = new LinkedBlockingQueue<FetchedDataChunk>();
                queues.put(new KV<String, String>(topic, threadId),stream);
                streamList.add(new MessageStream<T>(topic, stream, config.getConsumerTimeoutMs(), decoder));
            }
            ret.put(topic, streamList);
            logger.info("adding topic " + topic + " and stream to map");

        }


        // listener to consumer and partition changes
        ZKRebalanceListener<T> loadBalancerListener = new ZKRebalanceListener<T>(config.getGroupId(), consumerIdString, ret);
        loadBalancerListener.start();
        registerConsumerInZK(dirs, consumerIdString, topicCount);

        // register listener for session expired event
        zkClient.subscribeStateChanges(new ZKSessionExpireListener<T>(dirs, consumerIdString, topicCount, loadBalancerListener));
        zkClient.subscribeChildChanges(dirs.consumerRegistryDir, loadBalancerListener);
        for(String topic : ret.keySet()){
            // register on broker partition path changes
            final String partitionPath = ZkUtils.BrokerTopicsPath + "/" + topic;
            zkClient.subscribeChildChanges(partitionPath, loadBalancerListener);
        }

        // explicitly trigger load balancing for this consumer
        loadBalancerListener.syncedRebalance();
        return ret;
    }


    private String generateConsumerId(){
        UUID uuid = UUID.randomUUID();
        try {
            return String.format("%s-%d-%s", InetAddress.getLocalHost().getHostName(), System.currentTimeMillis(), Long.toHexString(uuid.getMostSignificantBits()).substring(0, 8));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("can not generate consume id by auto, set the 'consumerid' parameter to fix this");
        }
    }

    public void commitOffsets() {
        if(zkClient == null){
            logger.info("zk client is null, Cannot commit offsets");
            return;
        }
        for(Map.Entry<String, Pool<Partition, PartitionTopicInfo>> e : topicRegistry.entrySet()){
            ZkGroupTopicDirs topicDirs = new ZkGroupTopicDirs(config.getGroupId(), e.getKey());
            //
            for(PartitionTopicInfo info : e.getValue().values()){
                final long newOffset = info.getConsumedOffset();
                final String path = topicDirs.consumerOffsetDir + "/" + info.partition.getName();
                try {
                    ZkUtils.updatePersistentPath(zkClient, path, "" + newOffset);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    logger.info("exception during commitOffsets, path= " + path + ", offset=" + newOffset, e1);
                }finally {
                    logger.info("Commited [" + path + "] for topic " + info);
                }
            }
        }
    }

    public void close() throws IOException {
        if(isShuttingDown.compareAndSet(false, true)){
            logger.info("ZkConsumerConnector shuting down");
            try {
                scheduler.shutdown();
                if(fetcher != null){
                    fetcher.stopConnectionsToAllBrokers();
                }
                sendShutdownToAllQueues();
                if(config.isAutoCommit()){
                    commitOffsets();
                }
                if(this.zkClient != null){
                    this.zkClient.close();
                    zkClient = null;
                }
            } catch (ZkInterruptedException e) {
                e.printStackTrace();
                logger.info("error during consumer connector shutdown", e);
            }
            logger.info("ZkConsumerConnector shut down completed");
        }
    }

    private void sendShutdownToAllQueues(){
        for(BlockingQueue<FetchedDataChunk> queue : queues.values()){
            queue.clear();
            try {
                queue.put(SHUTDOWN_COMMAND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectZk(){
        logger.info("Connecting to zookeeper instance at " + config.getZkConnect());
        this.zkClient = new ZkClient(config.getZkConnect(), config.getZkSessionTimeoutMs(), config.getZkConnectionTimeoutMs(), ZkStringSerializer.getInstance());
        logger.info("Connected to zookeeper at " + config.getZkConnect());
    }

    class ZKRebalanceListener<T> implements IZkChildListener, Runnable{

        public  String group;

        public String consumerIdString;

        Map<String, List<MessageStream<T>>> messagesStreams;

        private boolean isWatcherTriggered = false;

        private final ReentrantLock lock = new ReentrantLock();

        private final Condition cond = lock.newCondition();

        private Thread watcherExecutorThread;

        public ZKRebalanceListener(String group, String consumerIdString, Map<String, List<MessageStream<T>>> messagesStreams) {
            super();
            this.group = group;
            this.consumerIdString = consumerIdString;
            this.messagesStreams = messagesStreams;

            this.watcherExecutorThread = new Thread(this, consumerIdString + "_watcher_executor");
        }

        public void start(){
            this.watcherExecutorThread.start();
        }

        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            try {
                lock.lock();
                isWatcherTriggered = true;
                cond.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public void run() {
            logger.info("staring watcher executor thread for consumer " + consumerIdString);
            boolean doRebalance = false;
            while(!isShuttingDown.get()){
                try {
                    lock.lock();
                    try {
                        if(!isWatcherTriggered){
                            cond.await(1000, TimeUnit.MILLISECONDS); // wake up periodically so that it can check the shutdown flag
                        }
                    } finally {
                        doRebalance = isWatcherTriggered;
                        isWatcherTriggered = false;
                        lock.unlock();
                    }
                    if(doRebalance){
                        syncedRebalance();;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.info("error during syncedRebalance", e);
                }
            }

            logger.info("stoping watcher executor thread for consumer " + consumerIdString);
        }

        public void syncedRebalance(){
            synchronized (rebalanceLock){
                for(int i = 0; i < config.getMaxRebalanceRetries(); i++){
                    logger.info("begin rebalancing consumer " + consumerIdString + " try # " + i );
                    boolean done = true;
                    Cluster cluster = ZkUtils.getCluster(zkClient);
                    try {
                        done = rebalance(cluster);
                    } catch (Exception e) {
                        e.printStackTrace();
                        /**
                         * occasionally, we may hit ZK exception because
                         * the ZK state is changing while we are iterating
                         * For example, a Zk node can disappear between the
                         * time we get all children and the time we try to
                         * get the value of a child. Just let this go since
                         * another rbalance will be triggered
                         */
                        logger.info("exception during rebalance ", e);
                    }
                    logger.info("end rebalance consumer " + consumerIdString + " try # " + i);

                    if(done){
                        return;
                    }else{
                        /**
                         * Here the cache is at a risk of being stable. To take future rebalancing decisions correctly, we should
                         * clear the cache
                         */
                        logger.info("Rebalancing attempt failed, Clearing the cache before the next rebalancing operation is triggered");
                    }

                    closeFetchersForQueues(cluster, messagesStreams, queues.values());
                    try {
                        Thread.sleep(config.getRebalanceBackoffMs());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            throw new ConsumerRebalanceFailedException(consumerIdString + " can't rebalance after " + config.getMaxRebalanceRetries() + " retries");
        }

        private boolean rebalance(Cluster cluster){
            Map<String, Set<String>> myTopicThreadIdsMap = ZkUtils.getTopicCount(zkClient, group, consumerIdString).getConsumerThreadIdsPerTopic();
            Map<String, List<String>> consumersPerTopicMap = ZkUtils.getConsumersPerTopic(zkClient, group);
            Map<String, List<String>> partitionPerTopicMap = ZkUtils.getPartitionsForTopics(zkClient, myTopicThreadIdsMap.keySet());

            /**
             * fetchers must be stopped to avoid data duplication, since if
             * the current rebalancing attempt fails, the partitions that
             * are released could be owned by another consumer. But if we
             * don't stop the fetchers first, this consumer would continue
             * returing data for released partitions in parallel. So, not
             * stopping the fetchers leads to duplicate data
             */

            closeFetchers(cluster, messagesStreams, myTopicThreadIdsMap);
            releasePartitionOwnership(topicRegistry);

            Map<KV<String, String>, String> partitionOwnershipDecision = new HashMap<KV<String, String>, String>();
            Pool<String, Pool<Partition, PartitionTopicInfo>> currentTopicRegistry = new Pool<String, Pool<Partition, PartitionTopicInfo>>();
            for(Map.Entry<String, Set<String>> e : myTopicThreadIdsMap.entrySet()){
                String topic = e.getKey();
                currentTopicRegistry.put(topic, new Pool<Partition, PartitionTopicInfo>());

                //
                ZkGroupTopicDirs topicDirs = new ZkGroupTopicDirs(group, topic);
                List<String> curConsumers = consumersPerTopicMap.get(topic);
                List<String> curPartitions = partitionPerTopicMap.get(topic);

                final int nPartsPerConsumer = curPartitions.size() / curConsumers.size();
                final int nConsumersWithExtraPart = curPartitions.size() % curConsumers.size();

                logger.info("Consumer " + consumerIdString + " rebalancing the following partitions : " +
                curPartitions + " for topic " + topic + " with consumers " + curConsumers);

                for(String consumerThreadId : e.getValue()){
                    final int myConsumerPosition = curConsumers.indexOf(consumerThreadId);
                    assert (myConsumerPosition >= 0);
                    final int startPart = nPartsPerConsumer * myConsumerPosition + Math.min(myConsumerPosition, nConsumersWithExtraPart);
                    final int nParts = nPartsPerConsumer + ((myConsumerPosition + 1 > nConsumersWithExtraPart)? 0:1);

                    /**
                     * Range-partition the sorted partitions to consumers
                     * for better locality. The first few consumers pick up
                     * an extra partition, if any
                     */

                    if(nParts <= 0){
                        logger.info("No broker partitions consumed by consumer thread " + consumerThreadId + " for topic " + topic);
                    }else{
                        for(int i = startPart; i < startPart + nParts; i++){
                            String partition = curPartitions.get(i);
                            logger.info(consumerThreadId + " attempting to claim partition " + partition);
                            addPartitionTopicInfo(currentTopicRegistry, topicDirs, partition, topic, consumerThreadId);
                            // record the partition ownership decision
                            partitionOwnershipDecision.put(new KV<String, String>(topic, partition), consumerThreadId);
                        }
                    }


                }

            }

            /**
             * move the partition ownership here, since that can be used to
             * indicate a truly successful rebalancing attempt A
             * rebalancing attempt is completed successfully only after the
             * fetcher have been started correctly
             */
            if(reflectPartitionOwnershipDecision(partitionOwnershipDecision)){
                logger.info("Updating the cache");
                logger.info("Partitions per topic cache " + partitionPerTopicMap);
                logger.info("Consumers per topic cache " + consumersPerTopicMap);
                topicRegistry = currentTopicRegistry;
                updateFetcher(cluster, messagesStreams);
                return true;
            }else{
                return false;
            }
        }

        private void updateFetcher(Cluster cluster, Map<String, List<MessageStream<T>>> messagesStreams2 ){
            if(fetcher != null){
                List<PartitionTopicInfo> allPartitionInfos = new ArrayList<PartitionTopicInfo>();
                for(Pool<Partition, PartitionTopicInfo> p : topicRegistry.values()){
                    allPartitionInfos.addAll(p.values());
                }
                fetcher.startConnections(allPartitionInfos, cluster, messagesStreams2);
            }
        }

        private boolean reflectPartitionOwnershipDecision(Map<KV<String, String>, String> partitionOwnershipDecision){
            List<KV<String, String>> successfullyOwnerdPartitions = new ArrayList<KV<String, String>>();
            int hasPartitionOwnershipFailed = 0;
            for(Map.Entry<KV<String, String>, String> e : partitionOwnershipDecision.entrySet()){
                String topic = e.getKey().k;
                String partition = e.getKey().v;
                String consumerThreadid = e.getValue();
                ZkGroupTopicDirs topicDirs = new ZkGroupTopicDirs(group, topic);
                String partitionOwnerPath = topicDirs.consumerOwnerDir + "/" + partition;
                try {
                    ZkUtils.createEphemeralPathExpectConflict(zkClient, partitionOwnerPath, consumerThreadid);
                    successfullyOwnerdPartitions.add(new KV<String, String>(topic, partition));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    logger.info("waiting for the partition ownership to be deleted " + partition);
                    hasPartitionOwnershipFailed++;
                }
            }

            if(hasPartitionOwnershipFailed > 0){
                for(KV<String, String> topicAndPartition : successfullyOwnerdPartitions){
                    deletePartitionOwnershipFromZK(topicAndPartition.k, topicAndPartition.v);
                }
                return false;
            }
            return true;
        }

        private void addPartitionTopicInfo(Pool<String, Pool<Partition, PartitionTopicInfo>> currentTopicRegistry, ZkGroupTopicDirs topicDirs,
                                           String partitionString, String topic, String consumerThreadId){
            Partition partition = Partition.parse(partitionString);
            Pool<Partition, PartitionTopicInfo> partitionTopicInfoMap = currentTopicRegistry.get(topic);

            final String znode = topicDirs.consumerOffsetDir + "/" + partition.getName();
            String offsetString = ZkUtils.readDataMayBeNull(zkClient, znode);
            // If first time starting a consumer, set the initial offset based on the config

            long offset = 0l;
            if(offsetString == null){
                if(OffsetRequest.SmallestTimeString.equals(config.getAutoOffsetReset())){
                    earliestOrLatestOffset(topic, partition.brokerId, partition.partId, OffsetRequest.EarliesTime);
                }else if(OffsetRequest.LargestTimeString.equals(config.getAutoOffsetReset())){
                    earliestOrLatestOffset(topic, partition.brokerId, partition.brokerId, OffsetRequest.LatestTime);
                }else{
                    throw new InvalidConfigException("Wrong value in autoOffsetReset in ConsumerConfig");
                }
            }else{
                offset = Long.parseLong(offsetString);
            }

            BlockingQueue<FetchedDataChunk> queue = queues.get(new KV<String, String>(topic, consumerThreadId));
            AtomicLong consumerOffset = new AtomicLong(offset);
            AtomicLong fetchedOffset = new AtomicLong(offset);
            PartitionTopicInfo partitionTopicInfo = new PartitionTopicInfo(topic,
                    partition.brokerId,
                    partition,
                    queue,
                    consumerOffset,
                    fetchedOffset
                    );
            partitionTopicInfoMap.put(partition, partitionTopicInfo);
            logger.info(partitionTopicInfo + " selected new offset " + offset);
        }

        private long earliestOrLatestOffset(String topic, int brokerId, int partitionId, long earliestOrLatest){
            SimpleConsumer simpleConsumer = null;
            long producedOffset = -1;
            try {
                Cluster cluster = ZkUtils.getCluster(zkClient);
                Broker broker = cluster.getBroker(brokerId);
                if(broker == null){
                    throw new IllegalStateException("Broker " + brokerId + " is unavailable. Cannot issue getOffsetsBefore request");
                }

                // using default value
                simpleConsumer = new SimpleConsumer(broker.host, broker.port, config.getSocketTimeoutMs(), config.getSocketBufferSize());
                long[] offsets = simpleConsumer.getOffsetsBefore(topic, partitionId, earliestOrLatest, 1);
                producedOffset = offsets[0];
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("error in earliestOrLatestOffset()", e);
            } finally {
                if(simpleConsumer != null){
                    Closer.closeQuietly(simpleConsumer);
                }
            }

            return producedOffset;
        }

        private void releasePartitionOwnership(Pool<String, Pool<Partition, PartitionTopicInfo>> localTopicRegistry){
            logger.info("Releasing partition ownership");
            for(Map.Entry<String, Pool<Partition, PartitionTopicInfo>> e : localTopicRegistry.entrySet()){
                for(Partition partition : e.getValue().keySet()){
                    deletePartitionOwnershipFromZK(e.getKey(), partition);
                }
            }
            localTopicRegistry.clear();
        }

        private void deletePartitionOwnershipFromZK(String topic, String partitionStr){
            ZkGroupTopicDirs topicDirs = new ZkGroupTopicDirs(group, topic);
            String znode = topicDirs.consumerOwnerDir + "/" + partitionStr;
            ZkUtils.deletePath(zkClient, znode);
            logger.info("Consumer " + consumerIdString + " releasing " + znode);
        }

        private void deletePartitionOwnershipFromZK(String topic, Partition partition){
            this.deletePartitionOwnershipFromZK(topic, partition.toString());
        }

        private void closeFetchers(Cluster cluster, Map<String, List<MessageStream<T>>> messagesStreams2, Map<String, Set<String>> myTopicThreadIdsMap){
            // topicRegistry.values()
            List<BlockingQueue<FetchedDataChunk>> queuesToBeCleared = new ArrayList<BlockingQueue<FetchedDataChunk>>();
            for(Map.Entry<KV<String, String>, BlockingQueue<FetchedDataChunk>> e : queues.entrySet()){
                if(myTopicThreadIdsMap.containsKey(e.getKey().k)){
                    queuesToBeCleared.add(e.getValue());
                }
            }
            closeFetchersForQueues(cluster, messagesStreams2, queuesToBeCleared);
        }

        private void closeFetchersForQueues(Cluster cluster, Map<String, List<MessageStream<T>>> messagesStreams,
                                            Collection<BlockingQueue<FetchedDataChunk>> queuesTobeCleared){
            if(fetcher == null){
                return;
            }
            fetcher.stopConnectionsToAllBrokers();
            fetcher.clearFetcherQueues(queuesTobeCleared, messagesStreams.values());
        }

        private void resetState(){
            topicRegistry.clear();
        }
    }

    class ZKSessionExpireListener<T> implements IZkStateListener{

        private ZkGroupDirs zkGroupDirs;

        private String consumerIdString;

        private TopicCount topicCount;

        private ZKRebalanceListener<T> loadRebalancerListener;

        public ZKSessionExpireListener(ZkGroupDirs zkGroupDirs, String consumerIdString, TopicCount topicCount, ZKRebalanceListener<T> loadRebalancerListener) {
            super();
            this.zkGroupDirs = zkGroupDirs;
            this.consumerIdString = consumerIdString;
            this.topicCount = topicCount;
            this.loadRebalancerListener = loadRebalancerListener;
        }

        public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {

        }

        public void handleNewSession() throws Exception {
            /** Called after the zookeeper session has expired and a new session has been created. You would have to re-create
             * any ephemeral nodex here
             *
             *
             * When we get a SessionExpired event, we lost all ephemeral
             * nodes and zkClient has reestablished a connection for us. We
             * need to release the ownership of the current consumer and
             * re-register this consumer in the consumer register and
             * trigger a rebalance
             */
            logger.info("Zk expired; release old broker partition ownership; re-register consumer " + consumerIdString);
            loadRebalancerListener.resetState();
            registerConsumerInZK(zkGroupDirs, consumerIdString, topicCount);
            // explicity trigger  load balancing for this consumer
            loadRebalancerListener.syncedRebalance();

            /**
             * There is no need to resubscribe to child and state changes
             * The child change watchers will be set inside rebalance when we read the children list
             */
        }
    }

    public void registerConsumerInZK(ZkGroupDirs zkGroupDirs, String consumerIdString, TopicCount topicCount){
        String path = zkGroupDirs.consumerRegistryDir + "/" + consumerIdString;
        String data = topicCount.toJsonString();
        logger.info(String.format("register consumer in zookeeper [%s] => [%s]", path, data));
        ZkUtils.createEphemeralPathExpectConflict(zkClient, path, data);

    }
}
