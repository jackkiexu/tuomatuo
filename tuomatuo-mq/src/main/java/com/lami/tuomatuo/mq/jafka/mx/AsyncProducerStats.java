package com.lami.tuomatuo.mq.jafka.mx;

import com.lami.tuomatuo.mq.jafka.utils.Utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xjk on 2016/10/11.
 */
public class AsyncProducerStats implements AsyncProducerStatsMBean, IMBeanName {

    final AtomicInteger droppedEvents = new AtomicInteger();

    final AtomicInteger numEvents = new AtomicInteger();

    public int getAsyncProducerEvents() {
        return numEvents.get();
    }

    public int getAsyncProducerDroppedEvents() {
        return droppedEvents.get();
    }

    public String getMbeanName() {
        return "jafka.producer.Producer:type=AsyncProducerStats";
    }

    private static class AsyncProducerStatsHolder{
        static AsyncProducerStats instance = new AsyncProducerStats();
        static {
            Utils.registerMBean(instance);
        }
    }

    public static void recordDroppedEvents(){
        AsyncProducerStatsHolder.instance.droppedEvents.incrementAndGet();
    }

    public static void recordEvent(){
        AsyncProducerStatsHolder.instance.numEvents.incrementAndGet();
    }
}
