package com.lami.tuomatuo.mq.jafka.mx;

import com.lami.tuomatuo.mq.jafka.utils.Pool;
import com.lami.tuomatuo.mq.jafka.utils.Utils;
import com.qiniu.util.StringMap;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xjk on 2016/10/10.
 */
public class ConsumerTopicStat implements ConsumerTopicStatMBean, IMBeanName{

    private static final Pool<String, ConsumerTopicStat> instances = new Pool<String, ConsumerTopicStat>();

    private final AtomicLong numCumulatedMessagesPerTopic = new AtomicLong(0);

    public long getMessagesPerTopic(){
        return numCumulatedMessagesPerTopic.get();
    }

    public void recordMessagePerTopic(int nMessage){
        numCumulatedMessagesPerTopic.addAndGet(nMessage);
    }

    public String getMbeanName() {
        return mBeanName;
    }

    private String mBeanName;

    public static ConsumerTopicStat getComsumerTopicStat(String topic){
        ConsumerTopicStat stat = instances.get(topic);
        if(stat == null){
            stat = new ConsumerTopicStat();
            stat.mBeanName = "jafka:type=jafka.ComsumerTopicStat."+topic;
            if(instances.putIfNotExists(topic, stat) == null){
                Utils.registerMBean(stat);
            }else{
                stat = instances.get(topic);
            }
        }
        return stat;
    }
}
