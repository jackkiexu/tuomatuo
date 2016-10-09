package com.lami.tuomatuo.mq.jafka.mx;

/**
 * Created by xjk on 2016/10/9.
 */
public class LogStats implements LogStatsMBean, IMBeanName{


    public String getMbeanName() {
        return null;
    }

    public String getName() {
        return null;
    }

    public long getSize() {
        return 0;
    }

    public int getNumberOfSegments() {
        return 0;
    }

    public long getCurrentOffset() {
        return 0;
    }

    public long getNumAppendedMessages() {
        return 0;
    }
}
