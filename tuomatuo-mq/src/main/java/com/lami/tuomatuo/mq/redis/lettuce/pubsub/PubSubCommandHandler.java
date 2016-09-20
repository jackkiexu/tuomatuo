package com.lami.tuomatuo.mq.redis.lettuce.pubsub;

import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.output.PubSubOutput;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.Command;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandHandler;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandOutput;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;

import java.util.concurrent.BlockingQueue;

/**
 * Created by xjk on 9/16/16.
 */
public class PubSubCommandHandler<K, V> extends CommandHandler {

    private RedisCodec<K, V> codec;

    /**
     * Initialize a new instance.
     *
     * @param queue Command queue.
     * @param codec Codec.
     */
    public PubSubCommandHandler(BlockingQueue<Command<?>> queue, RedisCodec<K, V> codec) {
        super(queue);
        this.codec = codec;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ChannelBuffer buffer) throws InterruptedException {
        while (!queue.isEmpty()) {
            CommandOutput<?> output = queue.peek().getOutput();
            if (!rsm.decode(buffer, output)) return;
            queue.take().complete();
            if (output instanceof PubSubOutput) {
                logger.info(output);
                Channels.fireMessageReceived(ctx, output);
            }
        }

        PubSubOutput<V> output = new PubSubOutput<V>(codec);
        while (rsm.decode(buffer, output)) {
            Channels.fireMessageReceived(ctx, output);
            output = new PubSubOutput<V>(codec);
        }
    }
}
