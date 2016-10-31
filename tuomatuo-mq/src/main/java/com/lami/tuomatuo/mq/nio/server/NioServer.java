package com.lami.tuomatuo.mq.nio.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xjk on 10/24/16.
 */
public class NioServer  implements Runnable{

    private static final Logger logger = Logger.getLogger(NioServer.class);

    // The host:port combination to listen on
    private InetAddress hostAddress;
    private int port;

    // The channel on which we'll accept connections
    private ServerSocketChannel serverChannel;

    // The selector we'll be monitoring
    private Selector selector;

    // The buffer into which we'll read data when it's available
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    private EchoWorker worker;

    // A list of Change Request instances;
    private BlockingQueue<ChangeRequest> changeRequests = new LinkedBlockingQueue<ChangeRequest>();

    // Maps a SocketChannel to a list of ByteBuffer instance
    private Map pendingData = new ConcurrentHashMap();


    public NioServer(InetAddress hostAddress, int port, EchoWorker worker) throws IOException{
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();
        this.worker = worker;
    }

    private Selector initSelector() throws IOException{
        // Create a new selector
        Selector socketSelector = SelectorProvider.provider().openSelector();

        // Create a new non-blocking server socket channel
        this.serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // Bind the server socket to the specified address and port
        InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
        serverChannel.socket().bind(isa);

        //Register the server socket channel, indicating an interest in
        // accepting new connection
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    private void accept(SelectionKey key) throws IOException{
        logger.info("NioServer accept : " + key);
        // For an accept to be pending the channel miust be a server socket channel
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
        logger.info("NioServer accept serverSocketChannel " + serverSocketChannel);

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
        Socket socket = socketChannel.socket();
        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)key.channel();

        //Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (Exception e) {
            e.printStackTrace();

            // The remote forcibly closed the connection, cancel
            // The server key and close the channel
            key.cancel();
            socketChannel.close();
            return;
        }

        if(numRead == -1){
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel
            key.channel().close();
            key.cancel();
            return;
        }

        // Hand the data off to our worker thread
        this.worker.processData(this, socketChannel, this.readBuffer.array(), numRead);
    }

    private void write(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel)key.channel();

        synchronized (this.pendingData){
            BlockingQueue queue = (BlockingQueue)this.pendingData.get(socketChannel);
            while(!queue.isEmpty()){
                ByteBuffer buf = (ByteBuffer)queue.poll();
                socketChannel.write(buf);
                if(buf.remaining() > 0){

                    // .. or the socket's buffer fills up
                    break;
                }
            }

            if(queue.isEmpty()){
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for data
                key.interestOps(SelectionKey.OP_READ);
            }

        }
    }

    public void send(SocketChannel socketChannel, byte[] data){
        // Indicate we want the interest ops set changed
        this.changeRequests.add(new ChangeRequest(socketChannel, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

        // And queue the data we want written
        BlockingQueue queue = (BlockingQueue)this.pendingData.get(socketChannel);
        if(queue == null){
            queue = new LinkedBlockingQueue();
            this.pendingData.put(socketChannel, queue);
        }
        queue.add(ByteBuffer.wrap(data));

        // Finally wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void run() {
        while (true){
            try {
                while(!this.changeRequests.isEmpty()){
                    ChangeRequest change = (ChangeRequest)changeRequests.poll();
                    if(change == null) break;
                    switch (change.type){
                        case ChangeRequest.CHANGEOPS:
                            // SocketChannel
                            // https://docs.oracle.com/javase/7/docs/api/java/nio/channels/spi/AbstractSelectableChannel.html#keyFor(java.nio.channels.Selector)
                            // Retrieves the key representing the channel's registration with given selector
                            SelectionKey key = change.socketChannel.keyFor(this.selector);
                            key.interestOps(change.ops);
                    }
                }
                changeRequests.clear();

                // Wait for an event one of the registered channels
                this.selector.select(500);

                // Iterate over the set of the keys for which events are available
                java.util.Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while(selectedKeys.hasNext()){
                    SelectionKey key = (SelectionKey)selectedKeys.next();
                    selectedKeys.remove();

                    if(!key.isValid()) continue;

                    // Check what event is available and deal with it
                    if(key.isAcceptable()){
                        this.accept(key);
                    }else if(key.isReadable()){
                        this.read(key);
                    }else if(key.isWritable()){
                        this.write(key);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }



    public static void main(String[] args) {
        try {
            EchoWorker worker = new EchoWorker();
            new Thread(worker).start();
            new Thread(new NioServer(null, 9090, worker)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
