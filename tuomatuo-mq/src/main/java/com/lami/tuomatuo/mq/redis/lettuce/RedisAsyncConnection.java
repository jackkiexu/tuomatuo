package com.lami.tuomatuo.mq.redis.lettuce;

import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.Command;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandArgs;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by xjk on 9/17/16.
 */
public class RedisAsyncConnection<K, V> extends RedisConnection<K, V>{

    private RedisConnection<K, V> parent;
    private List<Command<?>> pipeline;


    /**
     * Initialize a new connection.
     *
     * @param queue   Command queue.
     * @param codec   Codec used to encode/decode keys and values.
     * @param timeout Maximum time to wait for a responses.
     * @param unit    Unit of time for the timeout.
     */
    public RedisAsyncConnection(BlockingQueue<Command<?>> queue, RedisCodec<K, V> codec, int timeout, TimeUnit unit) {
        super(queue, codec, timeout, unit);
    }

    /**
     * Wait for completion of all commands executed since the last flush
     * and return their outputs.
     *
     * @return The command outputs.
     */
    public List<Object> flush() {
        List<Object> list = new ArrayList<Object>(pipeline.size());
        for (Command<?> cmd : pipeline) {
            list.add(parent.getOutput(cmd));
        }
        pipeline.clear();
        return list;
    }

    @Override
    public String discard() {
        return parent.discard();
    }

    @Override
    public List<Object> exec() {
        return parent.exec();
    }

    @Override
    public String multi() {
        return parent.multi();
    }

    @Override
    public <T> Command<T> dispatch(CommandType type, CommandOutput<T> output, CommandArgs<K, V> args) {
        Command<T> cmd = parent.dispatch(type, output, args);
        pipeline.add(cmd);
        return cmd;
    }

    @Override
    public <T> T getOutput(Command<T> cmd) {
        return null;
    }
}
