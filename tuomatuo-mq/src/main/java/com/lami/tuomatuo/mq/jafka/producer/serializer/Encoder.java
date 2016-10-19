package com.lami.tuomatuo.mq.jafka.producer.serializer;

import com.lami.tuomatuo.mq.jafka.message.Message;

/**
 * Created by xjk on 2016/10/19.
 */
public interface Encoder<T> {

    Message toMessage(T event);
}
