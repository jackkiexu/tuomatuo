package com.lami.tuomatuo.mq.jafka.mx;

import com.lami.tuomatuo.mq.jafka.log.Log;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xjk on 2016/10/9.
 */
public class LogStats implements LogStatsMBean, IMBeanName{

    public Log log;

    private AtomicLong numCumulatedMessages = new AtomicLong(0);

    private String mbeanName;

    public LogStats(Log log) {
        this.log = log;
    }

    public String getMbeanName() {
        return mbeanName;
    }

    public String getName() {
        return log.name;
    }

    public long getSize() {
        return log.size();
    }

    public int getNumberOfSegments() {
        return log.getNumberOfSegments();
    }

    public long getCurrentOffset() {
        return log.getHightwaterMark();
    }

    public long getNumAppendedMessages() {
        return numCumulatedMessages.get();
    }

    public void recordAppendedMessages(int nMessages){
        numCumulatedMessages.getAndAdd(nMessages);
    }
}
