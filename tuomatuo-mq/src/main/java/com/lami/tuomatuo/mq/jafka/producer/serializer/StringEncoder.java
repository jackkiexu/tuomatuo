package com.lami.tuomatuo.mq.jafka.producer.serializer;

import com.lami.tuomatuo.mq.jafka.message.Message;
import com.sohu.jafka.utils.Utils;

/**
 * Created by xjk on 2016/10/19.
 */
public class StringEncoder implements Encoder<String> {
    public Message toMessage(String event) {
        return new Message(Utils.getBytes(event, "UTF-8"));
    }
}
