package com.lami.tuomatuo.mq.redis.lettuce.pubsub;

import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;

/**
 * One element of redis pub/sub stream. May be a message or notification
 * of subscription details
 *
 * Created by xjk on 9/16/16.
 */
public class PubSubOutput<V> extends CommandOutput<V> {

    enum Type { message, pmessage, psubscribe, punsubscribe, subscribe,unsubscribe }

    private Type type;
    private String channel;
    private String pattern;
    private long count;
    private V message;


    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public PubSubOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    public Type type() { return type; }
    public String channel() { return channel; }
    public String pattern() { return pattern; }
    public long count(){ return count; }

    @Override
    public V get() throws RedisException {
        return message;
    }

    public void set(ByteBuffer bytes){
        if(type == null){
            type = Type.valueOf(decodeAscii(bytes));
            return;
        }

        switch (type){
            case pmessage:
                if(pattern == null){
                    pattern = decodeAscii(bytes);
                    break;
                }
            case message:
                if(channel == null){
                    channel = decodeAscii(bytes);
                    break;
                }
                message = (V)codec.decodeValue(bytes);
                break;
            case psubscribe:
            case punsubscribe:
                pattern = decodeAscii(bytes);
                break;
            case subscribe:
            case unsubscribe:
                channel = decodeAscii(bytes);
                break;
        }
    }
    public void set(long integer){
        count = integer;
    }
}


