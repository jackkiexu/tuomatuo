package com.lami.tuomatuo.mq.jafka.message;

/**
 * Created by xjk on 9/25/16.
 */
public class MessageAndOffset {

    public Message message;

    public long offset;

    public MessageAndOffset(Message message, long offset) {
        this.message = message;
        this.offset = offset;
    }
}
