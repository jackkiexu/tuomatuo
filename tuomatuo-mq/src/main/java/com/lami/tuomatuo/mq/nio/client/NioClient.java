package com.lami.tuomatuo.mq.nio.client;

import com.lami.tuomatuo.mq.nio.server.ChangeRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xjk on 10/29/16.
 */
public class NioClient implements Runnable {

    private static Logger logger = Logger.getLogger(NioClient.class);

    // The host:port combination to connect to
    private InetAddress hostAddress;
    private int port;

    // the selector we'll be monitoring
    private Selector selector;

    // The buffer into which we'll read data when it's available
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    // A list of PendingChange instance
    private BlockingQueue pendingChanges = new LinkedBlockingQueue();

    // Maps a SocketChannel to a list of ByteBuffer instances
    private Map pendingData = new ConcurrentHashMap();

    // Map a SocketChannel to a RspHandler
    private Map rspHandlerMaps = new ConcurrentHashMap();

    public NioClient(InetAddress hostAddress, int port) throws IOException{
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();
    }

    private Selector initSelector() throws IOException{
        // Crreate a new selector
        return SelectorProvider.provider().openSelector();
    }

    private SocketChannel inittiateConnection() throws IOException{
        // Create a non-blocking socket channel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // Kick off connection establishment
        socketChannel.connect(new InetSocketAddress(this.hostAddress, this.port));

        // Queue a channel registeration since the caller is not the
        // selecting thread. As part of the registration we'll register
        // an interest in connection events. These are raised when a channel
        // is ready to complete connection establishment
        this.pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
        return socketChannel;
    }

    private void finishConnection(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)key.channel();

        // Finish the connection. If the connection operation failed
        // this will raise an IOException

        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            e.printStackTrace();
            key.cancel();
            return;
        }

        // Register an interest in writing on this channel
        key.interestOps(SelectionKey.OP_WRITE);
    }

    public void send(byte[] data, RspHandler handler) throws IOException{
        // Start a new connection
        SocketChannel socketChannel = this.inittiateConnection();

        // Register the response handler
        this.rspHandlerMaps.put(socketChannel, handler);

        // And queue the data we want written
        BlockingQueue queue = (BlockingQueue)this.pendingData.get(socketChannel);
        if(queue == null){
            queue = new LinkedBlockingQueue();
            this.pendingData.put(socketChannel, queue);
        }
        queue.add(ByteBuffer.wrap(data));

        // Finally wake up our seleting thread so it can make the required changes
        this.selector.wakeup();
    }

    private void handleResponse(SocketChannel socketChannel, byte[] data, int numRead) throws IOException{
        // Make a correctly sized copy of the data before handing it
        // to the client
        byte[] rspData = new byte[numRead];
        System.arraycopy(data, 0, rspData, 0 , numRead);

        // Look up the handler for this channel
        RspHandler handler = (RspHandler)this.rspHandlerMaps.get(socketChannel);

        // And pass the response to it
        if(handler.handleResponse(rspData)){
            // The handler has seen enough, close the connection
            socketChannel.close();
            socketChannel.keyFor(this.selector).cancel();
        }
    }

    public void run() {

    }



}
