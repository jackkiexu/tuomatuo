package com.lami.tuomatuo.mq.redis.lettuce;


import com.lami.tuomatuo.mq.redis.lettuce.codec.RedisCodec;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.Command;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.CommandHandler;
import com.lami.tuomatuo.mq.redis.lettuce.protocol.ConnectionWatchdog;
import com.lami.tuomatuo.mq.redis.lettuce.pubsub.PubSubCommandHandler;
import com.lami.tuomatuo.mq.redis.lettuce.pubsub.RedisPubSubConnection;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * A scalable thread-safe <a href="http://redis.io/">Redis</a> Client... Mutiple threads
 * may share one {@link RedisConnection} provided they avoid blocking and tansactional
 * operations such as BLPOP and MULTI/EXEC
 * Created by xjk on 9/18/16.
 */
public class RedisClient {

    private ClientBootstrap bootstrap;
    private Timer timer;
    private ChannelGroup channels;
    private int timeout;
    private TimeUnit unit;

    public RedisClient(String host, int port) {
        ExecutorService connectors = Executors.newFixedThreadPool(1);
        ExecutorService workers = Executors.newFixedThreadPool(1);
        ClientSocketChannelFactory factory = new NioClientSocketChannelFactory(connectors, workers);

        InetSocketAddress addr = new InetSocketAddress(host, port);
        bootstrap = new ClientBootstrap(factory);
        bootstrap.setOption("remoteAddress", addr);
        setDefaultTimeout(60, TimeUnit.SECONDS);

        channels = new DefaultChannelGroup();
        timer = new HashedWheelTimer();
    }

    /**
     * Set the default timeout for connections created by this client
     * @param timeout
     * @param unit
     */
    public void setDefaultTimeout(int timeout, TimeUnit unit){
        this.timeout = timeout;
        this.unit = unit;
        bootstrap.setOption("connectTimeoutMillis", unit.toMillis(timeout));
    }

    public <K, V> RedisConnection<K, V> connect(RedisCodec<K, V> codec){
        try {
            BlockingQueue<Command<?>> queue = new LinkedBlockingQueue<Command<?>>();

            ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, channels, timer);
            CommandHandler handler = new CommandHandler(queue);
            RedisConnection<K, V> connection = new RedisConnection<K, V>(queue, codec, timeout, unit);

            ChannelPipeline pipeline = Channels.pipeline(watchdog, handler, connection);
            Channel channel = bootstrap.getFactory().newChannel(pipeline);

            ChannelFuture future = channel.connect((SocketAddress) bootstrap.getOption("remoteAddress"));
            future.await();

            if (!future.isSuccess()) {
                throw future.getCause();
            }

            watchdog.setReconnect(true);

            return connection;
        } catch (Throwable e) {
            throw new RedisException("Unable to connect", e);
        }
    }

    public <K, V> RedisPubSubConnection<K, V> connectPubSub(RedisCodec<K, V> codec){
        try {
            BlockingQueue<Command<?>> queue = new LinkedBlockingDeque<Command<?>>();

            ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, channels, timer);
            PubSubCommandHandler<K, V> handler = new PubSubCommandHandler<K, V>(queue, codec);
            RedisPubSubConnection<K, V> connection = new RedisPubSubConnection<K, V>(queue, codec, timeout, unit);

            ChannelPipeline pipeline = Channels.pipeline(watchdog, handler, connection);
            Channel channel = bootstrap.getFactory().newChannel(pipeline);

            ChannelFuture future = channel.connect((SocketAddress)bootstrap.getOption("remoteAddress"));
            future.await();

            if(!future.isSuccess()){
                throw future.getCause();
            }
            watchdog.setReconnect(true);

            return connection;
        } catch (Throwable e) {
            throw new RedisException("Unable to connect", e);
        }
    }

    /**
     * Shutdown this client and close all open connections. The client should be
     * discarded after shutdown
     */
    public void shutdown(){
        for(Channel c : channels){
            ChannelPipeline pipline = c.getPipeline();
            RedisConnection connection = pipline.get(RedisConnection.class);
            connection.close();
        }
        ChannelGroupFuture future = channels.close();
        future.awaitUninterruptibly();
        bootstrap.releaseExternalResources();
    }
}
