package com.lami.tuomatuo.mq.jafka.mx;

import com.lami.tuomatuo.mq.jafka.utils.Pool;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xjk on 2016/10/10.
 */
public class BrokerTopicStat implements BrokerTopicStatMBean, IMBeanName {

    static class BrokerTopicStatHolder{
        static BrokerTopicStat allTopicState = new BrokerTopicStat();

        static Pool<String, BrokerTopicStat> states = new Pool<String, BrokerTopicStat>();
        static {
            allTopicState.mBeanName = "jafka:type=jafka.BrokerAllTopicStat";
            Utils.registerMBean(allTopicState);
        }
    }

    public static BrokerTopicStat getBrokerAllTopicStat(){
        return BrokerTopicStatHolder.allTopicState;
    }

    public static BrokerTopicStat getBrokerTopicStat(String topic){
        BrokerTopicStat state = BrokerTopicStatHolder.states.get(topic);
        if(state == null){
            state = new BrokerTopicStat();
            state.mBeanName = "jafka:type=jafka.BrokerTopicStat." + topic;
            if(null == BrokerTopicStatHolder.states.putIfNotExists(topic, state)){
                Utils.registerMBean(state);
            }
            state = BrokerTopicStatHolder.states.get(topic);
        }

        return state;
    }



    private String mBeanName;

    private final AtomicLong numCumulatedBytesIn = new AtomicLong(0);

    private final AtomicLong numCumulatedBytesOut = new AtomicLong(0);

    private final AtomicLong numCumulatedFailedFetchRequests = new AtomicLong(0);

    private final AtomicLong numCumulatedFailedProduceRequests = new AtomicLong(0);

    private final AtomicLong numCumulatedMessagesIn = new AtomicLong(0);

    public BrokerTopicStat() {
    }

    public long getMessagesIn() {
        return numCumulatedMessagesIn.get();
    }

    public long getBytesIn() {
        return numCumulatedBytesIn.get();
    }

    public long getBytesOut() {
        return numCumulatedBytesOut.get();
    }

    public long getFailedProducerequest() {
        return numCumulatedFailedProduceRequests.get();
    }

    public long getFailedFetchRequest() {
        return numCumulatedFailedFetchRequests.get();
    }

    public String getMbeanName() {
        return mBeanName;
    }


    public void recordBytesIn(long nBytes){
        numCumulatedBytesIn.getAndAdd(nBytes);
    }

    public void recordBytesOut(long nBytes){
        numCumulatedBytesOut.getAndAdd(nBytes);
    }

    public void recordFailedFetchRequest(){
        numCumulatedFailedFetchRequests.getAndIncrement();
    }

    public void recordFailedProduceRequest(){
        numCumulatedFailedProduceRequests.getAndIncrement();
    }

    public void recordMessagesIn(int nMessages){
        numCumulatedBytesIn.getAndAdd(nMessages);
    }
}
