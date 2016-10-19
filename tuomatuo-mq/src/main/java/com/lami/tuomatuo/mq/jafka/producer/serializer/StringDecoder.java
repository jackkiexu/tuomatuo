package com.lami.tuomatuo.mq.jafka.producer.serializer;

import com.lami.tuomatuo.mq.jafka.message.Message;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 2016/10/19.
 */
public class StringDecoder implements Decoder<String> {

    public String toEvent(Message message) {
        ByteBuffer buf = message.payload();
        byte[] b = new byte[buf.remaining()];
        buf.get(b);
        return Utils.fromBytes(b);
    }

}
