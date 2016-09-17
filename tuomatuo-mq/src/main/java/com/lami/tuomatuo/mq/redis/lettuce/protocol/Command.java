package com.lami.tuomatuo.mq.redis.lettuce.protocol;

import com.lami.tuomatuo.mq.redis.lettuce.RedisCommandInterruptedException;
import com.lami.tuomatuo.mq.redis.lettuce.RedisException;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.concurrent.*;

/**
 * Created by xjk on 9/16/16.
 */
public class Command<T> implements Future<T> {

    protected static final byte[] CRLF = "\r\n".getBytes();

    protected CommandType type;
    protected CommandArgs args;
    protected CommandOutput<T> output;
    protected CountDownLatch latch;

    /**
     * Create a new command with the supplied type and args
     * @param type
     * @param output
     * @param args
     */
    public Command(CommandType type, CommandOutput<T> output, CommandArgs args) {
        this.type = type;
        this.output = output;
        this.args = args;
        this.latch = new CountDownLatch(1);
    }

    /**
     *
     * @param mayInterruptIfRunning
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean canceled = false;
        if(latch.getCount() == 1){
            latch.countDown();
            canceled = true;
        }
        return canceled;
    }

    public boolean isCancelled() {
        return latch.getCount() == 0 && output == null;
    }

    public boolean isDone() {
        return latch.getCount() == 0;
    }

    public T get() {
        try {
            latch.await();
            return output.get();
        } catch (Exception e) {
            throw new RedisCommandInterruptedException(e);
        }
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return latch.await(timeout, unit)?output.get():null;
    }

    public boolean await(long timeout, TimeUnit unit){
        try {
            return latch.await(timeout, unit);
        } catch (InterruptedException e) {
            throw new RedisCommandInterruptedException(e);
        }
    }

    /**
     * Get the object that holds this command's output
     * @return
     */
    public CommandOutput<T> getOutput() { return output; }

    /**
     * Mark this command complete and notify all waiting threads
     */
    public void complete(){
        latch.countDown();
    }

    /**
     * Encode and write this command to the supplied buffer using the new
     * <a href="http://redis.io/topics/protocol">Unified Request Protocol</a>
     * @param buf
     */
    void encode(ChannelBuffer buf){
        buf.writeByte('*');
        writeInt(buf, 1 + (args != null ? args.count() : 0));
        buf.writeBytes(CRLF);
        buf.writeByte('$');
        writeInt(buf, type.bytes.length);
        buf.writeBytes(CRLF);
        buf.writeBytes(type.bytes);
        buf.writeBytes(CRLF);
        if(args != null){
            buf.writeBytes(args.buffer());
        }
    }

    protected static void writeInt(ChannelBuffer buf, int value){
        if(value >= 0 && value <= 9){
            buf.writeByte('0' + value);
            return;
        }

        if(value < 0){
            value = -value;
            buf.writeByte('-');
        }

        StringBuilder sb = new StringBuilder(8);
        while(value > 0){
            int digit = value % 10;
            sb.append((char)('0' + digit));
            value /= 10;
        }

        for(int i = sb.length() - 1; i >= 0; i--){
            buf.writeByte(sb.charAt(i));
        }
    }
}
