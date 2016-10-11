package com.lami.tuomatuo.mq.jafka.mx;

/**
 * Created by xjk on 2016/10/11.
 */
public interface AsyncProducerStatsMBean {

    int getAsyncProducerEvents();

    int getAsyncProducerDroppedEvents();
}
