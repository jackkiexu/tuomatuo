package com.lami.tuomatuo.mq.nio.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by xjk on 10/24/16.
 */
public class EchoWorker implements Runnable {

    private BlockingQueue<ServerDataEvent> blockingQueue = new LinkedBlockingQueue<ServerDataEvent>();

    public void processData(NioServer server, SocketChannel socketChannel, byte[] data, int count){
        byte[] dataCopy = new byte[count];
        System.arraycopy(data, 0, dataCopy, 0, count);
        blockingQueue.add(new ServerDataEvent(server, socketChannel, dataCopy));
    }

    public void run() {
        ServerDataEvent dataEvent = null;
        while(true){
            // Wait for data to become available
            try {
                dataEvent = blockingQueue.take();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Return to sender
            dataEvent.server.send(dataEvent.socket, dataEvent.data);
        }
    }
}
