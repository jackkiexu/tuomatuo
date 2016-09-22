package com.lami.tuomatuo.mq.redis.lettuce.protocol;

/**
 * Created by xjk on 9/16/16.
 */

import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import lombok.Data;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * State machine that decode redis server response encoded according to the
 * <a href="http://redis.io/topics/protocol">Unfield Request protocol</>
 */
public class RedisStateMachine {

    private Logger logger = Logger.getLogger(RedisStateMachine.class);

    private static final ByteBuffer QUEUED = ByteBuffer.wrap("QUEUED".getBytes());

    @Data
    static class State {
        enum Type { SINGLE, ERROR, INTEGER, BULK, MULTI, BYTES }
        Type type  = null; // msg type
        int  count = -1; // msg total segement
    }

    private LinkedList<State> stack;

    /**
     * Initialize a new instance.
     */
    public RedisStateMachine() {
        stack = new LinkedList<State>();
    }

    /**
     * Attempt to decode a redis response and return a flag indicating whether a complete
     * response was read.
     *
     * @param buffer    Buffer containing data from the server.
     * @param output    Current command output.
     *
     * @return true if a complete response was read.
     */
    public boolean decode(ChannelBuffer buffer, CommandOutput<?> output) {
        int length, end;
        ByteBuffer bytes;

        logger.info("decode:"+stack);

        if (stack.isEmpty()) {
            stack.add(new State());
        }

        loop:

        while (!stack.isEmpty()) {
            State state = stack.peek();
            logger.info("state:"+state);
            if (state.type == null) {
                logger.info("buffer.readable():" + buffer.readable());
                if (!buffer.readable()){
                    break;
                }
                try {
                    state.type = readReplyType(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logger.info("state:"+state);
                buffer.markReaderIndex();
            }

            switch (state.type) {
                case SINGLE:
                    bytes = readLine(buffer);
                    logger.info("SINGLE bytes : " + bytes);
                    if (bytes == null) {
                        break loop;
                    }
                    if (!QUEUED.equals(bytes)) {
                        output.set(bytes);
                    }
                    break;
                case ERROR:
                    bytes = readLine(buffer);
                    if (bytes == null) {
                        break loop;
                    }
                    output.setError(bytes);
                    break;
                case INTEGER:
                    end = findLineEnd(buffer);
                    if (end == -1) {
                        break loop;
                    }
                    long readLong = readLong(buffer, buffer.readerIndex(), end);
                    output.set(readLong);
                    break;
                case BULK:
                    end = findLineEnd(buffer);
                    if (end == -1){
                        break loop;
                    }
                    length = (int) readLong(buffer, buffer.readerIndex(), end);
                    if (length == -1) {
                        output.set(null);
                    } else {
                        state.type = RedisStateMachine.State.Type.BYTES;
                        state.count = length + 2;
                        buffer.markReaderIndex();
                        continue loop;
                    }
                    break;
                case MULTI:
                    if (state.count == -1) {
                        if ((end = findLineEnd(buffer)) == -1) break loop;
                        length = (int) readLong(buffer, buffer.readerIndex(), end);
                        state.count = length;
                        buffer.markReaderIndex();
                    }

                    if (state.count == -1) {
                        output.set(null);
                        break;
                    } else if (state.count == 0) {
                        break;
                    } else {
                        state.count--;
                        stack.addFirst(new State());
                    }
                    continue loop;
                case BYTES:
                    if ((bytes = readBytes(buffer, state.count)) == null) break loop;
                    output.set(bytes);
            }
            // Marks the current readerIndex in this buffer
            buffer.markReaderIndex();
            stack.remove();
            output.complete(stack.size());
        }
        logger.info("output:"+output.get() + ", stack.isEmpty():"+stack);
        return stack.isEmpty();
    }

    private int findLineEnd(ChannelBuffer buffer) {
        int start = buffer.readerIndex();
        // locate the first occurrence of the specified value in this buffer
        int index = buffer.indexOf(start, buffer.writerIndex(), (byte) '\n');
        StringBuilder temp = new StringBuilder();
        for(byte b : buffer.array()){
            temp.append(b+",");
        }
        logger.info("StringBuilder temp :" + temp);
        logger.info("findLineEnd readerIndex:"+start+ "writerIndex :" + buffer.writerIndex() + ", index:"+index +", buffer :" +buffer);
        return (index > 0 && buffer.getByte(index - 1) == '\r') ? index : -1;
    }

    private State.Type readReplyType(ChannelBuffer buffer) {
        switch (buffer.readByte()) {
            case '+': return RedisStateMachine.State.Type.SINGLE;
            case '-': return RedisStateMachine.State.Type.ERROR;
            case ':': return RedisStateMachine.State.Type.INTEGER;
            case '$': return RedisStateMachine.State.Type.BULK;
            case '*': return RedisStateMachine.State.Type.MULTI;
            default:  throw new RedisException("Invalid first byte");
        }
    }

    private long readLong(ChannelBuffer buffer, int start, int end) {
        long value = 0;

        boolean negative = buffer.getByte(start) == '-';
        int offset = negative ? start + 1 : start;
        while (offset < end - 1) {
            int digit = buffer.getByte(offset++) - '0';
            value = value * 10 - digit;
        }
        if (!negative) value = -value;
        buffer.readerIndex(end + 1);

        return value;
    }

    private ByteBuffer readLine(ChannelBuffer buffer) {
        ByteBuffer bytes = null;
        int end = findLineEnd(buffer);
        logger.info("buffer:" + buffer + ", readLine : end:" + end);
        if (end > -1) {
            int start = buffer.readerIndex();
            bytes = buffer.toByteBuffer(start, end - start - 1);
            // set the readerIndex of this buffer
            buffer.readerIndex(end + 1);
        }
        return bytes;
    }

    private ByteBuffer readBytes(ChannelBuffer buffer, int count) {
        ByteBuffer bytes = null;
        if (buffer.readableBytes() >= count) {
            bytes = buffer.toByteBuffer(buffer.readerIndex(), count - 2);
            logger.info("toByteBuffer : " + new String(bytes.array()));

            try {
                ByteBuffer bytesT = buffer.toByteBuffer(buffer.readerIndex(), count );
                logger.info("toByteBuffer : " + new String(bytesT.array()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            buffer.readerIndex(buffer.readerIndex() + count);
        }
        return bytes;
    }
}
