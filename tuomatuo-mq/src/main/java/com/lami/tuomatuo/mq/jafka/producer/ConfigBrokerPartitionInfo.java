package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.cluster.Partition;
import com.lami.tuomatuo.mq.jafka.common.InvalidConfigException;
import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by xjk on 2016/11/4.
 */
@ClientSide
public class ConfigBrokerPartitionInfo implements BrokerPartitionInfo{

    private ProducerConfig producerConfig;

    private SortedSet<Partition> brokerPartitions;

    private Map<Integer, Broker> allBrokers;

    public ConfigBrokerPartitionInfo(ProducerConfig producerConfig) {
        this.producerConfig = producerConfig;
        this.brokerPartitions = getConfigTopicPartitionInfo();
        this.allBrokers = getConfigBrokerInfo();
    }

    public SortedSet<Partition> getBrokerPartitionInfo(String topic) {
        return brokerPartitions;
    }

    public Broker getBrokerInfo(int brokerId) {
        return allBrokers.get(brokerId);
    }

    public Map<Integer, Broker> getAllBrokerInfo() {
        return allBrokers;
    }

    public void updateInfo() {

    }

    public void close() {

    }

    private SortedSet<Partition> getConfigTopicPartitionInfo(){
        String[] brokerInfoList = producerConfig.getBrokerList().split(",");
        if(brokerInfoList.length == 0){
            throw new InvalidConfigException("broker.list is empty");
        }
        TreeSet<Partition> brokerParts = new TreeSet<Partition>();
        for(String bInfo : brokerInfoList){
            brokerParts.add(new Partition(Integer.parseInt(bInfo.split(":")[0]), 0));
        }
        return brokerParts;
    }

    private Map<Integer, Broker> getConfigBrokerInfo(){
        Map<Integer, Broker> brokerInfo = new HashMap<Integer, Broker>();
        String[] brokerInfoList = producerConfig.getBrokerList().split(",");
        for(String bInfo : brokerInfoList){
            String[] idHostPort = bInfo.split(":");
            int id = Integer.parseInt(idHostPort[0]);
            brokerInfo.put(id, new Broker(id, idHostPort[1], idHostPort[1], Integer.parseInt(idHostPort[2])));
        }
        return brokerInfo;
    }
}
