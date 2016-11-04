package com.lami.tuomatuo.mq.jafka.producer.async;

import java.util.List;
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

    /**
     * Callback to process the data before it enters the batching queue of
     * the asynchronous producer
     * @param data
     * @return
     */
    QueueItem<T> beforeEnqueue(QueueItem<T> data);

    /**
     * Callback to process the data right after it enters the batching
     * queue of the asynchronous producer
     * @param data
     * @param added
     * @return
     */
    QueueItem<T> afterEnqueue(QueueItem<T> data, boolean added);

    /**
     *Callback to process the batched data right before it is being sent
     * by the handle API of the vent handler
     * @param data the batched data received by the event handler
     * @return the processed batched data that gets sent by the handle()
     *              API of the event handler
     */
    List<QueueItem<T>> afterDequeuingExistingData(QueueItem<T> data);

    /**
     * Callback to process the batched data right before it is being sent
     * by the handle API of the vent handler
     *
     * @param data the batched data received by the vent handler
     * @return the processed batched data that gets sent by the handle() API of the event handler
     */
    List<QueueItem<T>> beforeSendingData(List<QueueItem<T>> data);

    /**
     * Callback to process the last batch of the data right before the producer
     * send threads is shutdown
     *
     * @return the last batch of data that is sent to the eventHandler
     */
    List<QueueItem<T>> lastBatchBeforeClose();

    /**
     * Clean up shuts down the callback handler
     */
    void close();

}
