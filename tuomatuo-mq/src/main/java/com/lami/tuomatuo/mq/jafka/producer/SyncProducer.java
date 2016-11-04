package com.lami.tuomatuo.mq.jafka.producer;

import com.lami.tuomatuo.mq.jafka.api.MultiProducerRequest;
import com.lami.tuomatuo.mq.jafka.api.ProducerRequest;
import com.lami.tuomatuo.mq.jafka.api.RequestKeys;
import com.lami.tuomatuo.mq.jafka.common.annotations.ThreadSafe;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;
import com.lami.tuomatuo.mq.jafka.mx.SyncProducerStats;
import com.lami.tuomatuo.mq.jafka.network.BoundedByteBufferSend;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import com.lami.tuomatuo.mq.jafka.utils.Time;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Random;

/**
 * Created by xjk on 2016/10/19.
 */
@ThreadSafe
public class SyncProducer implements Closeable {

    private static final Logger logger = Logger.getLogger(SyncProducer.class);

    private static final RequestKeys RequestKey = RequestKeys.Produce;// 0

    private static final Random randomGenerator = new Random();

    private SyncProducerConfig config;

    private int MaxConnectBackoffMs = 60000;

    private SocketChannel channel = null;

    private int sentOnConnection = 0;

    private long lastConnectionTime;

    private final Object lock = new Object();

    private volatile boolean shutdown = false;

    private String host;

    private int port;

    public SyncProducer(SyncProducerConfig config) {
        super();
        this.config = config;
        this.host = config.getHost();
        this.port = config.getPort();

        lastConnectionTime = System.currentTimeMillis() - (long)(randomGenerator.nextDouble() * config.reconnectInterval);
    }

    private void verifySendBuffer(ByteBuffer slice){
        // TODO check the source
    }

    public void multiSend(List<ProducerRequest> producers){
        for(ProducerRequest request : producers){
            request.getMessages().verifyMessageSize(config.maxMessageSize);
        }
        send(new BoundedByteBufferSend(new MultiProducerRequest(producers)));
    }

    public void send(String topic, ByteBufferMessageSet messages){
        send(topic, ProducerRequest.RandomPartition, messages);
    }

    public void send(String topic, int partition, ByteBufferMessageSet messages){
        messages.verifyMessageSize(config.maxMessageSize);
        send(new BoundedByteBufferSend(new ProducerRequest(topic, partition, messages)));
    }

    private void send(BoundedByteBufferSend send){
        synchronized (lock){
            verifySendBuffer(send.getBuffer().slice());
            long startTime = Time.SystemTime.nanoseconds();
            getOrMakeConnection();
            logger.info("write data to " + host + ":" + port);
            try {
                send.writeTo(channel);
            } catch (IOException e) {
                e.printStackTrace();
                // no way to tell if write successed. Disconnect and re-throw exception to let client handle retry
                disconnect();
                throw new RuntimeException(e);
            }
            sentOnConnection++;
            if(sentOnConnection >= config.reconnectInterval
                    || (config.reconnectTimeInterval >= 0 && System.currentTimeMillis() - lastConnectionTime >= config.reconnectTimeInterval)){
                disconnect();
                channel = connect();
                sentOnConnection = 0;
                lastConnectionTime = System.currentTimeMillis();
            }

            long endTime = Time.SystemTime.nanoseconds();
            SyncProducerStats.recordProducerRequest(endTime - startTime);
        }
    }

    private void getOrMakeConnection(){
        if(channel == null){
            channel = connect();
        }
    }

    private SocketChannel connect(){
        long connectBackoffMs = 1;
        long beginTimeMs = Time.SystemTime.milliseconds();
        while(channel == null && !shutdown){
            try {
                channel = SocketChannel.open();
                channel.socket().setSendBufferSize(config.bufferSize);
                channel.configureBlocking(false);
                channel.socket().setSoTimeout(config.getSocketTimeoutMs());
                channel.socket().setKeepAlive(true);
                channel.connect(new InetSocketAddress(config.getHost(), config.getPort()));
                logger.info("Connected to " + config.getHost() + " : " + config.getPort() + " for producing");
            } catch (IOException e) {
                e.printStackTrace();
                disconnect();
                long endTimeMs = Time.SystemTime.milliseconds();
                if((endTimeMs - beginTimeMs + connectBackoffMs) > config.connectTimeoutMs){
                    logger.info("Producer connection to " + config.getHost() + " : " + config.getHost() + " timing out after " + config.connectTimeoutMs + " ms", e);
                    throw new RuntimeException(e.getMessage(), e);
                }
                logger.info("Producer connection to " + config.getHost() + " : " + config.getHost() + " failed, next attempt in " + config.connectTimeoutMs + " ms", e);
                try {
                    Time.SystemTime.sleep(connectBackoffMs);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                connectBackoffMs = Math.min(10 * connectBackoffMs, MaxConnectBackoffMs);
            }
        }
        return channel;
    }

    private void disconnect(){
        if(channel != null){
            logger.info("Disconnecting from " + config.getHost() + " : " + config.getHost());
            Closer.closeQuietly(channel);
            Closer.closeQuietly(channel.socket());
            channel = null;
        }
    }

    public void close() throws IOException {
        synchronized (lock){
            try{
                disconnect();
            }finally {
                shutdown = true;
            }
        }
    }
}
