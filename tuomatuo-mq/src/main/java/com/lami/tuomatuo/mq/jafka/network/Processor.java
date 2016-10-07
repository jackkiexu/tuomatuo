package com.lami.tuomatuo.mq.jafka.network;

import com.lami.tuomatuo.mq.jafka.api.RequestKeys;
import com.lami.tuomatuo.mq.jafka.mx.SocketServerStats;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by xjk on 10/1/16.
 */
public class Processor extends AbstractServerThread {

    ConcurrentLinkedQueue<SocketChannel> newConnections = new ConcurrentLinkedQueue<SocketChannel>();

    Logger requestLogger = Logger.getLogger("jafka.request.logger");

    HandlerMappingFactory handlerMappingFactory;

    SocketServerStats stats;

    int maxRequestSize;

    public Processor(HandlerMappingFactory handlerMappingFactory, SocketServerStats stats, int maxRequestSize) {
        this.handlerMappingFactory = handlerMappingFactory;
        this.stats = stats;
        this.maxRequestSize = maxRequestSize;
    }

    public void run() {

    }

    private SocketChannel channelFor(SelectionKey key){
        return (SocketChannel)key.channel();
    }

    private void write(SelectionKey key) throws IOException{
        Send response = (Send)key.attachment();
        SocketChannel socketChannel = channelFor(key);
        int written = response.writeTo(socketChannel);
        stats.recordBytesWritten(written);
        if(response.complete()){
            key.attach(null);
            key.interestOps(SelectionKey.OP_READ);
        }else{
            key.interestOps(SelectionKey.OP_WRITE);
            getSelector().wakeup();
        }
    }

    private void read(SelectionKey key) throws IOException{
        SocketChannel socketChannel = channelFor(key);
        Receive request = null;
        if(key.attachment() == null){
            request = new BoundedByteBufferReceive(maxRequestSize);
            key.attach(request);
        }else{
            request = (Receive) key.attachment();
        }

        int read = request.readFrom(socketChannel);
        stats.recordBytesRead(read);

        if(read < 0){

        }
    }


    private void close(SelectionKey key){
        SocketChannel channel = (SocketChannel)key.channel();
        logger.info("Closing connection from " + channel.socket().getRemoteSocketAddress());
        try {
            channel.socket().close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        key.attach(null);
        key.cancel();
    }

    /**
     * Handle a completed request producing an optional response
     * @param key
     * @param request
     * @return
     */
    private Send handle(SelectionKey key, Receive request){
        short requestTypeId = request.buffer().getShort();
        RequestKeys requestType = RequestKeys.valueOf(requestTypeId);
        if(requestType == null){
            throw new InvalidRequestException("No mapping found for handler id : " + requestTypeId);
        }
        logger.info("Handling " + requestType + " request from " + channelFor(key).socket().getRemoteSocketAddress());

        HandlerMapping handlerMapping = handlerMappingFactory.mapping(requestType, request);
        if(handlerMapping == null){
            throw new InvalidRequestException("No handler found for request");
        }
        long start = System.nanoTime();
        Send maybeSend = handlerMapping.handler(requestType, request);
        stats.recordRequest(requestType, System.nanoTime() - start);
        return maybeSend;
    }

    private void configureNewConnections() throws ClosedChannelException{
        while(newConnections.size() > 0){
            SocketChannel channel = newConnections.poll();
            logger.info("Listening to new connection from " + channel.socket().getRemoteSocketAddress());
            channel.register(getSelector(), SelectionKey.OP_READ);
        }
    }

    public void accept(SocketChannel socketChannel){
        newConnections.add(socketChannel);
        getSelector().wakeup();
    }
}
