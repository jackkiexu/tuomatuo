package com.lami.tuomatuo.mq.jafka.mx;

import com.lami.tuomatuo.mq.jafka.utils.Pool;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

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

    private String mBeanName;

    public long getMessagesIn() {
        return 0;
    }

    public long getBytesIn() {
        return 0;
    }

    public long getBytesOut() {
        return 0;
    }

    public long getFailedProducerequest() {
        return 0;
    }

    public long getFailedFetchRequest() {
        return 0;
    }

    public String getMbeanName() {
        return null;
    }
}
