package com.lami.tuomatuo.mq.jafka.consumer;

import com.lami.tuomatuo.mq.jafka.api.FetchRequest;
import com.lami.tuomatuo.mq.jafka.api.MultiFetchResponse;
import com.lami.tuomatuo.mq.jafka.api.MultiFetchRequest;
import com.lami.tuomatuo.mq.jafka.api.OffsetRequest;
import com.lami.tuomatuo.mq.jafka.common.ErrorMapping;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;
import com.lami.tuomatuo.mq.jafka.network.BoundedByteBufferReceive;
import com.lami.tuomatuo.mq.jafka.network.BoundedByteBufferSend;
import com.lami.tuomatuo.mq.jafka.network.Receive;
import com.lami.tuomatuo.mq.jafka.network.Request;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import com.lami.tuomatuo.mq.jafka.utils.KV;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/11/1.
 */
public class SimpleConsumer implements Closeable {

    private static final Logger logger = Logger.getLogger(SimpleConsumer.class);

    private String host;

    private int port;

    private int soTimeout;

    private int bufferSize;

    private SocketChannel channel = null;

    private final Object lock = new Object();

    public SimpleConsumer(String host, int port, int soTimeout, int bufferSize) {
        super();
        this.host = host;
        this.port = port;
        this.soTimeout = soTimeout;
        this.bufferSize = bufferSize;
    }


    private SocketChannel connect() throws IOException{
        close();
        InetSocketAddress address = new InetSocketAddress(host, port);
        SocketChannel ch = SocketChannel.open();
        logger.info("Connected to " + address + " for fetching");
        ch.configureBlocking(true);
        ch.socket().setReceiveBufferSize(bufferSize);
        ch.socket().setSoTimeout(soTimeout);
        ch.socket().setKeepAlive(true);
        ch.connect(address);
        return ch;
    }

    public ByteBufferMessageSet fetch(FetchRequest request) throws IOException{
        synchronized (lock){
            getOrMakeConnection();
            KV<Receive, ErrorMapping> response = null;

            try {
                sendRequest(request);
                response = getResponse();
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("Reconnect in fetch request due to socket error: ", e);
                // retry once
                try {
                    channel = connect();
                    sendRequest(request);
                    response = getResponse();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            return new ByteBufferMessageSet(response.k.buffer(), request.getOffset(), response.v);
        }
    }

    public long[] getOffsetsBefore(String topic, int partition, long time, int maxNumOffsets) throws IOException{
        synchronized (lock){
            KV<Receive, ErrorMapping> response = null;
            try {
                getOrMakeConnection();
                sendRequest(new OffsetRequest(topic, partition, time, maxNumOffsets));
                response = getResponse();
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("Reconnect int get offset request due to socket error : ", e);
                // retry once
                try {
                    channel = connect();
                    sendRequest(new OffsetRequest(topic, partition, time, maxNumOffsets));
                    response = getResponse();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    channel = null;
                    throw e1;
                }
            }

            return OffsetRequest.deserializeOffsetArray(response.k.buffer());
        }
    }

    public MultiFetchResponse multifetch(List<FetchRequest> fetches) throws IOException{
        synchronized (lock){
            getOrMakeConnection();
            KV<Receive, ErrorMapping> response = null;
            try {
                sendRequest(new MultiFetchRequest(fetches));
                response = getResponse();
            } catch (IOException e) {
                e.printStackTrace();
                logger.info("Reconnect in multifetch due to socket error : ", e);
                // retry once
                try {
                    channel = connect();
                    sendRequest(new MultiFetchRequest(fetches));
                    response = getResponse();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    channel = null;
                    throw e1;
                }
            }

            List<Long> offsets = new ArrayList<Long>();
            for(FetchRequest fetch : fetches){
                offsets.add(fetch.getOffset());
            }

            return new MultiFetchResponse(response.k.buffer(), fetches.size(), offsets);
        }
    }

    public void close(){
        synchronized (lock){
            if(channel != null){
                close(channel);
                channel = null;
            }
        }
    }

    private void close(SocketChannel socketChannel){
        logger.info("Disconnecting from " + channel.socket().getRemoteSocketAddress());
        Closer.closeQuietly(socketChannel);
        Closer.closeQuietly(socketChannel.socket());
    }

    private void getOrMakeConnection() throws IOException{
        if(channel == null){
            channel = connect();
        }
    }

    private void sendRequest(Request request) throws IOException{
        new BoundedByteBufferSend(request).writeCompletely(channel);
    }

    private KV<Receive, ErrorMapping> getResponse() throws IOException{
        BoundedByteBufferReceive response = new BoundedByteBufferReceive();
        response.readCompletely(channel);
        return new KV<Receive, ErrorMapping>(response, ErrorMapping.valueOf(response.buffer().getShort()));
    }
}
