package com.lami.tuomatuo.search.base.Disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 2016/4/20.
 */
public class LongEventProducerWithTranslator {

    private static final Logger logger = Logger.getLogger(LongEventProducerWithTranslator.class);

    // 一个translator可以看做是一个事件初始化器, publishEvent方法会调用它
    // 填充 Event

    private static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR =
            new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
                public void translateTo(LongEvent longEvent, long l, ByteBuffer byteBuffer) {
                    logger.info("longEvent:" + longEvent);
                }
            };

    private RingBuffer<LongEvent> ringBuffer;
    public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer bb){
        ringBuffer.publishEvent(TRANSLATOR, bb);
    }

}
