package com.lami.tuomatuo.search.base.Disruptor;

import com.lmax.disruptor.RingBuffer;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

/**
 * Created by xujiankang on 2016/4/20.
 */
public class LongEventProducer {

    private static final Logger logger = Logger.getLogger(LongEventProducer.class);

    private RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * onData 用来发布事件, 每调用就发布一次事件
     * 它的参数通过事件传递给消费者
     */
    public void onData(ByteBuffer bb){
        // 可以把 RingBuffer 看成是一个事件队列, 那么 next 就是得到下面一个事件槽
        long sequence = ringBuffer.next();

        try {
            // 用上面的索引取出一个空的事件用于填充
            LongEvent event = ringBuffer.get(sequence); // for the sequence
            logger.info("event:"+event);
            event.setValue(bb.getLong(0));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 发布事件
            ringBuffer.publish(sequence);
        }
    }
}
