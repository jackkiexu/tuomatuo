package com.lami.tuomatuo.mq.redis.lettuce.pubsub;

import com.lami.tuomatuo.mq.redis.lettuce.RedisConnection;
import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.Command;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandArgs;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandType;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * An asynchronous thread-safe pub/sub connection to redis server. After one or
 * more channels are subscribed to only pub/sub relate commands or {@link #quit}
 * may be called
 *
 * Incoming message and results of the {@link #subscribe} / {@link #unsubscribe}
 * calls will be passed to all registerd {@link RedisPubSubListener}
 *
 * A {@link com.lami.tuomatuo.mq.redis.lettuce.protocol.ConnectionWatchdog} monitors each
 * connection and reconnectors automatically until {@link #close} is called. Channel
 * and pattern subscriptions are renewed after reconnecting
 * Created by xjk on 9/18/16.
 */
public class RedisPubSubConnection<K, V> extends RedisConnection<K, V> {

    private List<RedisPubSubListener<V>> listeners;
    private Set<String> channels;
    private Set<String> patterns;

    /**
     * Initialize a new connection.
     *
     * @param queue   Command queue.
     * @param codec   Codec used to encode/decode keys and values.
     * @param timeout Maximum time to wait for a responses.
     * @param unit    Unit of time for the timeout.
     */
    public RedisPubSubConnection(BlockingQueue<Command<?>> queue, RedisCodec<K, V> codec, int timeout, TimeUnit unit) {
        super(queue, codec, timeout, unit);
        listeners = new CopyOnWriteArrayList<RedisPubSubListener<V>>();
        channels = new HashSet<String>();
        patterns = new HashSet<String>();
    }

    /**
     * Add a new listener
     * @param listener
     */
    public void addListeners(RedisPubSubListener<V> listener){
        listeners.add(listener);
    }

    public void removeListener(RedisPubSubListener<V> listener){
        listeners.remove(listener);
    }

    public void psubscribe(String...patterns){
        dispatch(CommandType.PSUBSCRIBE, new PubSubOutput<Object>(codec), args(patterns));
    }

    public void punsubscribe(String...patterns){
        dispatch(CommandType.PUNSUBSCRIBE, new PubSubOutput<Object>(codec), args(patterns));
    }

    public void subscribe(String...patterns){
        dispatch(CommandType.SUBSCRIBE, new PubSubOutput<Object>(codec), args(patterns));
    }

    public void unsubscribe(String...patterns){
        dispatch(CommandType.UNSUBSCRIBE, new PubSubOutput<Object>(codec), args(patterns));
    }

    @Override
    public synchronized void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);

        if(channels.size() > 0){
            String[] channelArray = new String[channels.size()];
            subscribe(channels.toArray(channelArray));
            channels.clear();
        }

        if(patterns.size() > 0){
            String[] channelArray = new String[patterns.size()];
            subscribe(patterns.toArray(channelArray));
            patterns.clear();
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        PubSubOutput<V> output = (PubSubOutput<V>)e.getMessage();
        for(RedisPubSubListener<V> listener : listeners){
            switch(output.type()){
                case message:
                    listener.message(output.channel(), output.get());
                    break;
                case pmessage:
                    listener.message(output.pattern(), output.channel(), output.get());
                    break;
                case psubscribe:
                    patterns.add(output.pattern());
                    listener.psubscribed(output.pattern(), output.count());
                    break;
                case punsubscribe:
                    patterns.remove(output.pattern());
                    listener.punsubscribed(output.pattern(), output.count());
                    break;
                case subscribe:
                    channels.add(output.channel());
                    listener.subscribed(output.channel(), output.count());
                    break;
                case unsubscribe:
                    channels.remove(output.channel());
                    listener.unsubscribed(output.channel(), output.count());
                    break;
            }
        }
    }

    private CommandArgs<K, V> args(String... strings){
        CommandArgs<K, V> args = new CommandArgs<K, V>(codec);
        for(String c : strings){
            args.add(c);
        }
        return args;
    }
}
