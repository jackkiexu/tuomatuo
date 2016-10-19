package com.lami.tuomatuo.mq.jafka.utils.zookeeper;

import com.github.zkclient.ZkClient;
import com.github.zkclient.exception.ZkNodeExistsException;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Cluster;
import com.lami.tuomatuo.mq.jafka.consumer.TopicCount;

import java.util.*;

/**
 * Created by xjk on 2016/10/19.
 */
public class ZkUtils {

    public static final String ConsumersPath = "/consumers";

    public static final String BrokerIdsPath = "brokers/ids";

    public static final String BrokerTopicsPath = "/brokers/topics";


    public static void makeSurePersistentPathExists(ZkClient zkClient, String path){
        if(!zkClient.exists(path)){
            zkClient.createPersistent(path,true);
        }
    }

    public static List<String> getChildrenParentMayNotExist(ZkClient zkClient, String path){
        try {
            return zkClient.getChildren(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readData(ZkClient zkClient, String path){
        return zkClient.readData(path);
    }

    public static String readDataMayBeNull(ZkClient zkClient, String path){
        return zkClient.readData(path, true);
    }

    public static void updatePersistentPath(ZkClient zkClient, String path, String data){
        try {
            zkClient.writeData(path, data);
        } catch (Exception e) {
            e.printStackTrace();
            createParentPath(zkClient, path);

            try {
                zkClient.createPersistent(path, data);
            } catch (RuntimeException e1) {
                e1.printStackTrace();
                zkClient.writeData(path, data);
            }
        }
    }

    private static void createParentPath(ZkClient zkClient, String path){
        String parentDir = path.substring(0, path.lastIndexOf('/'));
        if(parentDir.length() != 0){
            zkClient.createPersistent(parentDir, true);
        }
    }

    public static Cluster getCluster(ZkClient zkClient){
        Cluster cluster = new Cluster();
        List<String> nodes = getChildrenParentMayNotExist(zkClient, BrokerIdsPath);
        for(String node : nodes){
            final String brokerInfoString = readData(zkClient, BrokerIdsPath + "/" + node);
            cluster.add(Broker.createBroker(Integer.valueOf(node), brokerInfoString));
        }
        return cluster;
    }

    public static TopicCount getTopicCount(ZkClient zkClient, String group, String consumerId){
        ZkGroupDirs dirs = new ZkGroupDirs(group);
        String topicCountJson = ZkUtils.readData(zkClient, dirs.consumerRegistryDir + "/" + consumerId);
        return TopicCount.parse(consumerId, topicCountJson);
    }

    public static Map<String, List<String>> getPartitionsForTopics(ZkClient zkClient, Collection<String> topics){
        Map<String, List<String>> ret = new HashMap<String, List<String>>();
        for(String topic : topics){
            List<String> partList = new ArrayList<String>();
            List<String> brokers = getChildrenParentMayNotExist(zkClient, BrokerTopicsPath + "/" + topic);
            for(String broker : brokers){
                final String parts = readData(zkClient, BrokerIdsPath + "/" + topic + "/" + broker);
                int nParts = Integer.parseInt(parts);
                for(int i = 0; i < nParts; i++){
                    partList.add(broker + "-" + i);
                }
            }
            Collections.sort(partList);
            ret.put(topic, partList);
        }
        return ret;
    }

    public static Map<String, List<String>> getConsumersPerTopic(ZkClient zkClient, String group){
        ZkGroupDirs dirs = new ZkGroupDirs(group);
        List<String> consumers = getChildrenParentMayNotExist(zkClient, dirs.consumerRegistryDir);

        Map<String, List<String>> consumersPerTopicMap = new HashMap<String, List<String>>();
        for(String consumer : consumers){
            TopicCount topicCount = getTopicCount(zkClient, group, consumer);
            for(Map.Entry<String, Set<String>> e : topicCount.getConsumerThreadIdsPerTopic().entrySet()){
                final String topic = e.getKey();
                for(String consumerThreadId : e.getValue()){
                    List<String> list = consumersPerTopicMap.get(topic);
                    if(list == null){
                        list = new ArrayList<String>();
                        consumersPerTopicMap.put(topic, list);
                    }
                    list.add(consumerThreadId);
                }
            }
        }

        for(Map.Entry<String, List<String>> e : consumersPerTopicMap.entrySet()){
            Collections.sort(e.getValue());
        }
        return consumersPerTopicMap;
    }

    public static void deletePath(ZkClient zkClient, String path){
        try {
            zkClient.delete(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createEphemeralPath(ZkClient zkClient, String path, String data){
        try {
            zkClient.createEphemeral(path, data);
        } catch (RuntimeException e) {
            e.printStackTrace();
            createParentPath(zkClient, path);
            zkClient.createEphemeral(path, data);
        }
    }

    public static void createEphemeralPathExpectConflict(ZkClient zkClient, String path, String data){
        try {
            createEphemeralPath(zkClient, path, data);
        } catch (Exception e) {
            e.printStackTrace();
            String storeData = null;

            try {
                storeData = readData(zkClient, path);
            } catch (Exception e1) {
                // ignore
            }

            if(storeData == null || !storeData.equals(data)){
                throw new ZkNodeExistsException("conflict in " + path + ", data :" + data + ", store data: " + storeData);
            }
        }
    }
}
