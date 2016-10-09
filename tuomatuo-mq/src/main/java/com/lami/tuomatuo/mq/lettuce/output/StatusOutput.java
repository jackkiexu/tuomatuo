package com.lami.tuomatuo.mq.lettuce.output;

import com.lami.tuomatuo.mq.lettuce.protocol.CommandOutput;
import com.lami.tuomatuo.mq.lettuce.RedisException;
import com.lami.tuomatuo.mq.lettuce.codec.RedisCodec;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 9/16/16.
 */
public class StatusOutput extends CommandOutput<String> {

    private static final ByteBuffer OK = ByteBuffer.wrap("OK".getBytes());
    private String status;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     *
     * @param codec
     */
    public StatusOutput(RedisCodec<?, ?> codec) {
        super(codec);
    }

    @Override
    public String get() throws RedisException {
        errorCheck();
        return status;
    }

    public void set(ByteBuffer bytes){
        if(bytes == null) return;
        status = OK.equals(bytes) ? "OK" : decodeAscii(bytes);
    }
}
