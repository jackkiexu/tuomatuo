package com.lami.tuomatuo.mq.redis.lettuce.protocol;

import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;

import java.nio.ByteBuffer;

/**
 * Created by xjk on 9/16/16.
 */
public abstract class CommandOutput<T> {

    protected RedisCodec<?, ?> codec;
    protected String error;

    /**
     * Initialize a new instance that encodes and decodes the keys and
     * value using the supplied codec
     * @param codec
     */
    public CommandOutput(RedisCodec<?, ?> codec) {
        this.codec = codec;
    }

    /**
     * Get the command output
     * @return
     * @throws RedisException
     */
    public abstract T get() throws RedisException;

    /**
     * Set the command output to a sequence of bytes, or null. Concrete
     * {@link CommandOutput} implementations must override this method
     * unless they only receive an integer value which cannot be null
     * @param bytes
     */
    public void set(ByteBuffer bytes) {
        throw new IllegalStateException();
    }

    /**
     * Set the command output to a 64-bit signed integer. Concrete
     * {@link CommandOutput} implementation must override this method
     * unless they only receive a byte array value
     * @param integer
     */
    public void set(long integer){ throw new IllegalStateException();}

    /**
     * check if the redis server returned an error and if so throw a
     * {@link RedisException} containingthe error message
     * @throws RedisException
     */
    protected void errorCheck() throws RedisException{
        if(error != null) throw new RedisException(error);
    }

    /**
     * Set command output to an error message from the server
     * @param error
     */
    public void setError(ByteBuffer error){
        this.error = decodeAscii(error);
    }

    /**
     * Set command output to an error message fron the client
     * @param error
     */
    public void setError(String error){
        this.error = error;
    }

    public void complete(int depth){
        // nothing to do by default
    }

    protected String decodeAscii(ByteBuffer bytes){
        char[] chars = new char[bytes.remaining()];
        for(int i = 0; i < chars.length; i++){
            chars[i] = (char)bytes.get();
        }
        return new String(chars);
    }
}
