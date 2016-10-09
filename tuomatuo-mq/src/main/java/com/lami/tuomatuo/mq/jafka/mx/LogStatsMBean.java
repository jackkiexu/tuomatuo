package com.lami.tuomatuo.mq.jafka.mx;

/**
 * Created by xjk on 2016/10/9.
 */
public interface LogStatsMBean {

    String getName();

    long getSize();

    int getNumberOfSegments();

    long getCurrentOffset();

    long getNumAppendedMessages();
}
