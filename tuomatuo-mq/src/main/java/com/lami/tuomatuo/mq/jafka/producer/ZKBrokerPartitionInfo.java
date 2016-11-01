package com.lami.tuomatuo.mq.jafka.producer;

import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkStateListener;
import com.github.zkclient.ZkClient;
import com.lambdaworks.redis.cluster.models.partitions.Partitions;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.utils.ZkConfig;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkStringSerializer;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

import java.util.*;

/**
 * Created by xjk on 2016/11/1.
 */
public class ZKBrokerPartitionInfo implements BrokerPartitionInfo {

    private static final Logger logger = Logger.getLogger(ZKBrokerPartitionInfo.class);

    public ZkConfig zkConfig;

    public Callback callback;

    private final Object zkWatcherLock = new Object();

    private ZkClient zkClient;

    private Map<String, SortedSet<Partition>> topicBrokerPartitions;

    private Map<Integer, Broker> allBrokers;

    private BrokerTopicsListener brokerTopicsListener;


    public ZKBrokerPartitionInfo(ZkConfig zkConfig, Callback callback) {
        this.zkConfig = zkConfig;
        this.callback = callback;

        this.zkClient = new ZkClient(zkConfig.getZkConnect(),
                zkConfig.getZkSessionTimeoutMs(),
                zkConfig.getZkConnectionTimeoutMs(),
                ZkStringSerializer.getInstance()
                );

        this.topicBrokerPartitions = getZKTopicPartitionInfo();
        this.allBrokers = getZKBrokerInfo();
        // use just the brokerTopicListener for all watchers
        this.brokerTopicsListener = new BrokerTopicsListener(this.topicBrokerPartitions, this.allBrokers);

        // register listner for change of brokers for each topic to keep topicsBrokerPartitions updated
        for(String topic : this.topicBrokerPartitions.keySet()){
            zkClient.subscribeChildChanges(ZkUtils.BrokerTopicsPath + "/" + topic, this.brokerTopicsListener);
        }

        // register listener for new broker
        zkClient.subscribeChildChanges(ZkUtils.BrokerIdsPath, this.brokerTopicsListener);

        // register listener for session expired event
        zkClient.subscribeStateChanges(new ZKSessionExpirationListener());
    }

    /**
     * Generate a sequence of (brokerId, numPartitions) for all topics
     * registered in zookeeper
     * @return
     */
    private Map<String, SortedSet<Partition>> getZKTopicPartitionInfo(){
        Map<String, SortedSet<Partition>> brokerPartitionsPerTopic = new HashMap<String, SortedSet<Partition>>();
        ZkUtils.makeSurePersistentPathExists(zkClient, ZkUtils.BrokerTopicsPath);
        List<String> topics = ZkUtils.getChildrenParentMayNotExist(zkClient, ZkUtils.BrokerTopicsPath);
        for(String topic : topics){
            // find the number of broker partitions registered for this topic
            String brokerTopicPath = ZkUtils.BrokerTopicsPath + "/" + topic;
            List<String> brokerList = ZkUtils.getChildrenParentMayNotExist(zkClient, brokerTopicPath);

            final SortedSet<Partition> sortedBrokerPartitions = new TreeSet<Partition>();
            for(String bid : brokerList){
                final String numPath = brokerTopicPath + "/" + bid;
                final Integer numPartition = Integer.valueOf(ZkUtils.readData(zkClient, numPath));
                final int ibid = Integer.parseInt(bid);
                for(int i = 0; i < numPartition.intValue(); i++){
                    sortedBrokerPartitions.add(new Partition(ibid, i));
                }
            }
            logger.info("Broker ids and # of partitions on each for topic :" + topic + " = " + sortedBrokerPartitions);
            brokerPartitionsPerTopic.put(topic, sortedBrokerPartitions);
        }
        return brokerPartitionsPerTopic;
    }


    private Map<Integer, Broker> getZKBrokerInfo(){
        Map<Integer, Broker> brokers = new HashMap<Integer, Broker>();
        List<String> allBrokerIds = ZkUtils.getChildrenParentMayNotExist(zkClient, ZkUtils.BrokerIdsPath);
        if(allBrokerIds != null){
            logger.info("read all brokers count:" + allBrokerIds.size());
            for(String brokerId : allBrokerIds){
                String brokerInfo = ZkUtils.readData(zkClient, ZkUtils.BrokerIdsPath + "/" + brokerId);
                Broker createBroker = Broker.createBroker(Integer.valueOf(brokerId), brokerInfo);
                brokers.put(Integer.valueOf(brokerId), createBroker);
                logger.info("Loading Broker " + createBroker);
            }
        }
        return brokers;
    }

    public SortedSet<Partition> getBrokerPartitionInfo(String topic) {
        synchronized (zkWatcherLock){
            SortedSet<Partition> brokerPartitions = topicBrokerPartitions.get(topic);
            if(brokerPartitions == null || brokerPartitions.size() == 0){
                brokerPartitions = bootstrapWithexistingBrokers(topic);
                topicBrokerPartitions.put(topic, brokerPartitions);
                return brokerPartitions;
            }else{
                return new TreeSet<Partition>(brokerPartitions);
            }
        }
    }

    private SortedSet<Partition> bootstrapWithexistingBrokers(String topic){
        TreeSet<Partition> partitions = new TreeSet<Partition>();
        for(String brokerId : ZkUtils.getChildrenParentMayNotExist(zkClient, ZkUtils.BrokerIdsPath)){
            partitions.add(new Partition(Integer.valueOf(brokerId), 0));
        }
        return partitions;
    }

    public Broker getBrokerInfo(int brokerId) {
        return null;
    }

    public Map<Integer, Broker> getAllBrokerInfo() {
        return null;
    }

    public void updateInfo() {
        synchronized (this.zkWatcherLock){
            this.topicBrokerPartitions = getZKTopicPartitionInfo();
            this.allBrokers = getZKBrokerInfo();
        }
    }

    public void close() {
        this.zkClient.close();
    }


    class BrokerTopicsListener implements IZkChildListener{

        private Map<String, SortedSet<Partition>> originalBrokerTopicsPartitions;

        private Map<Integer, Broker> originBrokerIds;

        public BrokerTopicsListener(Map<String, SortedSet<Partition>> originalBrokerTopicsPartitions, Map<Integer, Broker> originBrokerIds) {
            super();
            this.originalBrokerTopicsPartitions = originalBrokerTopicsPartitions;
            this.originBrokerIds = originBrokerIds;

            logger.info("[BrokerTopicsListener] Creating broker topics listener to watch the folling paths - /broker/topics, /broker/topics/topic, /broker/ids");
            logger.info("[BrokerTopicsListener] Initialized this broker topics listener with initial mapping of broker id to partition id per topic with " + originalBrokerTopicsPartitions);
        }

        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            final List<String> curChilds = currentChilds != null ? currentChilds : new ArrayList<String>();
            synchronized (zkWatcherLock){
                if(ZkUtils.BrokerTopicsPath.equals(parentPath)){
                    Iterator<String> updateTopics = curChilds.iterator();
                    while(updateTopics.hasNext()){
                        String t = updateTopics.next();
                        if(originalBrokerTopicsPartitions.containsKey(t)){
                            updateTopics.remove();
                        }
                    }
                    for(String addedTopic : curChilds){
                        String path = ZkUtils.BrokerTopicsPath + "/" + addedTopic;
                        List<String> brokerList = ZkUtils.getChildrenParentMayNotExist(zkClient, path);
                        processNewBrokerInExistingTopic(addedTopic, brokerList);
                        zkClient.subscribeChildChanges(ZkUtils.BrokerTopicsPath + "/" + addedTopic, brokerTopicsListener);
                    }
                }else if(ZkUtils.BrokerIdsPath.equals(parentPath)){
                    processBrokerChange(parentPath, curChilds);
                }else{
                    // check path: /brokers/topics/topicname
                    String[] ps = parentPath.split("/");
                    if(ps.length == 4 && "topics".equals(ps[2])){
                        logger.info("[BrokerTopicsListener] list of broker changed at " + parentPath + " Currently registered list of brokers " + curChilds + " for topic -> " + ps[3]);
                        processNewBrokerInExistingTopic(ps[3], curChilds);
                    }
                }

                // update the data structures tracking older state values
                resetState();
            }
        }

        private void processBrokerChange(String parentPath, List<String> curChilds){
            final Map<Integer, Broker> oldBrokerIdMap = new HashMap<Integer, Broker>(originBrokerIds);
            for(int i = curChilds.size() - 1; i >= 0; i--){
                Integer brokerId = Integer.valueOf(curChilds.get(i));
                if(oldBrokerIdMap.remove(brokerId) != null){
                    curChilds.remove(i);
                }
            }

            // now curChilds are all new brokers
            // oldBrokerIdMap are all dead brokers
            for(String newBroker : curChilds){
                final String brokerInfo = ZkUtils.readData(zkClient, ZkUtils.BrokerIdsPath + "/" + newBroker);
                String[] brokerHostPort = brokerInfo.split(":"); // format creatorId:hosts:port
                final Integer newBrokerId = Integer.valueOf(newBroker);
                final Broker broker = new Broker(newBrokerId.intValue(), brokerHostPort[1], brokerHostPort[1], Integer.parseInt(brokerHostPort[2]));
                allBrokers.put(newBrokerId, broker);
                callback.producerCbk(broker.id, broker.host, broker.port);
            }

            // remove all dead broker and remove all broker-partition from topic list
            for(Map.Entry<Integer, Broker> deadBroker : oldBrokerIdMap.entrySet()){
                // remove dead broker
                allBrokers.remove(deadBroker.getKey());

                // remove dead broker-partition from topic
                for(Map.Entry<String, SortedSet<Partition>> topicPartition : topicBrokerPartitions.entrySet()){
                    Iterator<Partition> partitions = topicPartition.getValue().iterator();
                    while(partitions.hasNext()){
                        Partition p = partitions.next();
                        if(deadBroker.getKey().intValue() == p.brokerId){
                            partitions.remove();
                        }
                    }
                }
            }
        }

        private void processNewBrokerInExistingTopic(String topic, List<String> brokerList){
            SortedSet<Partition> updateBrokerParts = getBrokerPartitions(zkClient, topic, brokerList);
            SortedSet<Partition> oldBrokerParts = topicBrokerPartitions.get(topic);
            SortedSet<Partition> mergeBrokerparts = new TreeSet<Partition>();
            if(oldBrokerParts != null){
                mergeBrokerparts.addAll(oldBrokerParts);
            }
            // Override old parts or add new parts
            mergeBrokerparts.addAll(updateBrokerParts);
            // Keep only brokers that are alive
            Iterator<Partition> iter = mergeBrokerparts.iterator();
            while(iter.hasNext()){
                if(!allBrokers.containsKey(iter.next().brokerId)){
                    iter.remove();
                }
            }

            topicBrokerPartitions.put(topic, mergeBrokerparts);
            logger.info("[BrokerTopicslistener] list of broker partitions for topic: " + topic + " are " + mergeBrokerparts);
        }

        private void resetState(){
            logger.info("[BrokerTopicsListener] Before resseting broker topic partitions state " + this.originalBrokerTopicsPartitions);
            this.originalBrokerTopicsPartitions = new HashMap<String, SortedSet<Partition>>(topicBrokerPartitions);
            logger.info("[BrokerTopicListener] After reseting broker topic partitions state " + originalBrokerTopicsPartitions);

            logger.info("[BrokerTopicListener] Before reseting broker id map state " + originBrokerIds);
            this.originBrokerIds = new HashMap<Integer, Broker>(allBrokers);
            logger.info("[BrokerTopicsListener] After reseting broker id map state " + originBrokerIds);
        }
    }

    class ZKSessionExpirationListener implements IZkStateListener{

        public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {

        }

        public void handleNewSession() throws Exception {
            /**
             * When we get a SessionExpired event, we lost all ephemeral
             * nodes and zkClient has reestablished a connection for us
             */
            logger.info("ZK expired; release old list of broker partitions for topics");
            topicBrokerPartitions = getZKTopicPartitionInfo();
            allBrokers = getZKBrokerInfo();
            brokerTopicsListener.resetState();

            /**
             *  register listener for change of brokers for each topic to keep topicsBrokerPartitions updated
             *  NOTE: this probably not required here. Since when we read from getZKTopicPartitionInfo() above,
             *  it automatically recreates the watchers there itself
             */
            for(String topic : topicBrokerPartitions.keySet()){
                zkClient.subscribeChildChanges(ZkUtils.BrokerTopicsPath + "/" + topic, brokerTopicsListener);
            }

            // there is no need to re-register other listeners as they are listening on the child changes of
            // permanent nodes

        }
    }


    /**
     * Generate a mapping from broker id to (brokerId, numPartition) for
     * the list of brokers specified
     *
     * @param zkClient
     * @param topic the topic to which the brokers have registered
     * @param brokerList the list of brokers for which the partitions info is to be generated
     * @return a sequence of (brokerId, numPartitions) for brokers in brokerList
     */
    private static SortedSet<Partition> getBrokerPartitions(ZkClient zkClient, String topic, List<?> brokerList){
        String brokerTopicPath = ZkUtils.BrokerTopicsPath + "/" + topic;
        SortedSet<Partition> brokerParts = new TreeSet<Partition>();
        for(Object brokerId : brokerList){
            final Integer bid = Integer.valueOf(brokerId.toString());
            final Integer numPartition = Integer.valueOf(ZkUtils.readData(zkClient, brokerTopicPath + "/" + bid));
            for(int i = 0; i < numPartition.intValue(); i++){
                brokerParts.add(new Partition(bid, i));
            }
        }
        return brokerParts;
    }

}
