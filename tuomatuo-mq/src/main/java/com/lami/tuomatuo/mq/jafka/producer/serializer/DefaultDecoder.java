package com.lami.tuomatuo.mq.jafka.producer.serializer;

import com.lami.tuomatuo.mq.jafka.message.Message;

/**
 * Created by xjk on 2016/10/19.
 */
public class DefaultDecoder implements Decoder<Message> {
    public Message toEvent(Message message) {
        return message;
    }
}
