package com.lami.tuomatuo.mq.redis.lettuce.output;

import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by xjk on 9/16/16.
 */

public class MultiOutput extends CommandOutput<List<Object>>{

    private Queue<CommandOutput<?>> queue;
    private List<Object> values;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public MultiOutput(RedisCodec<?, ?> codec) {
        super(codec);
        this.queue = new LinkedList<CommandOutput<?>>();
        this.values = new ArrayList<Object>();
    }

    public void add(CommandOutput<?> cmd){ queue.add(cmd); }

    @Override
    public List<Object> get() throws RedisException {
        return values;
    }

    public void set(long integer){
        queue.peek().set(integer);
    }

    public void set(ByteBuffer bytes){
        queue.peek().set(bytes);
    }

    public void setError(ByteBuffer error){
        queue.peek().setError(error);
    }

    public void complete(int depth){
        if(depth == 1){
            CommandOutput<?> output = queue.remove();
            values.add(output.get());
        }
    }

}
