package com.lami.tuomatuo.mq.jafka.producer.async;

import com.lami.tuomatuo.mq.jafka.common.IllegalQueueStateException;
import com.lami.tuomatuo.mq.jafka.producer.SyncProducer;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Encoder;
import com.lami.tuomatuo.mq.jafka.utils.Time;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 2016/11/4.
 */
public class ProducerSendThread<T> extends Thread {

    private static final Logger logger = Logger.getLogger(ProducerSendThread.class);

    public String threadname;

    public BlockingQueue<QueueItem<T>> queue;

    public Encoder<T> serializer;

    public SyncProducer underlyingProducer;

    public EventHandler<T> eventHandler;

    public CallbackHandler<T> callbackHandler;

    public long queueTime;

    public long batchSize;

    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    private volatile boolean shutdown = false;

    public ProducerSendThread(String threadname,
                              BlockingQueue<QueueItem<T>> queue,
                              Encoder<T> serializer,
                              SyncProducer underlyingProducer,
                              EventHandler<T> eventHandler,
                              CallbackHandler<T> callbackHandler,
                              long queueTime,
                              long batchSize) {
        super();
        this.threadname = threadname;
        this.queue = queue;
        this.serializer = serializer;
        this.underlyingProducer = underlyingProducer;
        this.eventHandler = eventHandler;
        this.callbackHandler = callbackHandler;
        this.queueTime = queueTime;
        this.batchSize = batchSize;
    }

    @Override
    public void run() {
        try {
            List<QueueItem<T>> remainingEvents = processEvents();
            // handle remainging events
            if(remainingEvents.size() > 0){
                logger.info(String.format("Dispatching last batch of %d events to the event handler", remainingEvents.size()));
                tryToHandle(remainingEvents);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            shutdownLatch.countDown();
        }
    }

    public void awaitShutdown(){
        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown(){
        shutdown = true;
        eventHandler.close();
        logger.info("Shutdown thread complete");
    }

    private List<QueueItem<T>> processEvents(){
        long lastSend = Time.SystemTime.milliseconds();
        List<QueueItem<T>> events = new ArrayList<QueueItem<T>>();
        boolean full = false;

        while(!shutdown){
            try {
                QueueItem<T> item = queue.poll(Math.max(0, (lastSend + queueTime) - Time.SystemTime.milliseconds()), TimeUnit.MILLISECONDS);
                long elapsed = Time.SystemTime.milliseconds() - lastSend;
                boolean expired = item == null;
                if(item != null){
                    if(callbackHandler != null){
                        events.addAll(callbackHandler.afterDequeuingExistingData(item));
                    }else{
                        events.add(item);
                    }
                    full = events.size() >= batchSize;
                }

                if(full || expired){
                    if(expired){
                        logger.info(elapsed + " ms elapsed. Queue time reached. Sending ....");
                    }else{
                        logger.info(String.format("Batch(%d) full. Sending..", batchSize));
                    }
                }
                tryToHandle(events);
                lastSend = Time.SystemTime.milliseconds();
                events.clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(queue.size() > 0){
            throw new IllegalQueueStateException("Invalid queue state! After queue shutdown, " + queue.size() + " remaining items in the queue");
        }
        if(this.callbackHandler != null){
            events.addAll(callbackHandler.lastBatchBeforeClose());
        }
        return events;
    }

    private void tryToHandle(List<QueueItem<T>> events){
        logger.info("handling " + events.size() + " events");

        if(events.size() > 0){
            try {
                this.eventHandler.handle(events, underlyingProducer, serializer);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("Error in handling batch of " + events.size() + " events ", e);
            }
        }
    }
}
