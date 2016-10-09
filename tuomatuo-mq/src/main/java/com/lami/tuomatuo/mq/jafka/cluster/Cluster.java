package com.lami.tuomatuo.mq.jafka.cluster;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The set of active brokers in the cluster
 *
 * Created by xjk on 2016/10/9.
 */
public class Cluster {

    private Map<Integer, Broker> brokers = new LinkedHashMap<Integer, Broker>();

    public Cluster() {
    }

    public Cluster(List<Broker> brokerList) {
        for(Broker broker : brokerList){
            brokers.put(broker.id, broker);
        }
    }

    public Broker getBroker(Integer id){
        return brokers.get(id);
    }

    public void add(Broker broker){
        brokers.put(broker.id, broker);
    }

    public void remove(Integer id){
        brokers.remove(id);
    }

    public int size(){
        return brokers.size();
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "brokers=" + brokers +
                '}';
    }
}
