package com.lami.tuomatuo.mq.jafka.producer.async;

import java.util.Properties;

/**
 * Callback handler APIs for use in the async producer. The purpose is to
 * give the user some callback handles to insert custom functionality at
 * various stages as the data flows through the pipeline of the async
 * producer
 *
 * Created by xjk on 2016/10/19.
 */
public interface CallbackHandler<T> {

    /**
     * Initializes the callback handler using a Properties object
     * @param properties
     */
    void init(Properties properties);

    QueueItem<T> beforeEnqueue(QueueItem<T> data);

}
