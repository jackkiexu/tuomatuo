package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * this class implements a connection manager for leader election using TCP. It
 * maintains one connection for every pair of servers. The tricky part is to
 * guarantee that there is exactly one connection for every pair servers that
 * are operating correctly and that communicate over the network
 *
 * If two servers try to start a connection concurrently, then the connection
 * manager uses a very simple tie-breaking mechanism to decide which connection
 * to drop based on the IP addressed of two parties
 *
 * For every peer, the manager maintains a queue of messages to send. If the
 * connection to any particular peer drops, then the sender thread puts the
 * message back on the list. As this implementation currently uses a queue
 * implementation to maintian messages to send to another peer, we add the
 * message to the tail of the queue, thus changing the order of messages.
 * Although this is not a problem for the leader election, it could be a problem
 * when consolidating peer communication. This is to be varified, though
 *
 *
 * Created by xujiankang on 2017/3/19.
 */
public class QuorumCnxManager {

    private static final Logger LOG = LoggerFactory.getLogger(QuorumCnxManager.class);

    // maximum capacity of thread queues
    private static final int RECV_CAPACITY = 100;

    // initialized to 1 to prevent sending
    // state notification to peers
    public static final int SEND_CAPACITY = 1;

    public static final int PACKETMAXSIZE = 1024 * 512;

    // Maximum number of attempts to connect to a peer
    public static final int MAX_CONNECTION_ATTEMPTS = 2;

    // Negative counter for oberver server ids
    public long observerCounter = -1;

    // Connection timeout value in milliseconds
    private int cnxT0 = 5000;

    public QuorumPeer self;

    // Mapping from Peer to Thread number
    public ConcurrentHashMap<Long, SendWorker> senderWorkerMap;
    public ConcurrentHashMap<Long, ArrayBlockingQueue<ByteBuffer>> queueSendMap;
    public ConcurrentHashMap<Long, ByteBuffer> lastMessageSent;

    // Reception queue
    public ArrayBlockingQueue<Message> recvQueue;

    // Object to synchronize access to recvQueue
    private Object recvQLock = new Object();

    // shutdown flag
    volatile boolean shutdown = false;

    public Listener listener;

    private AtomicInteger threadCnt = new AtomicInteger(0);

    static public class Message {
        public ByteBuffer buffer;
        public long sid;

        public Message(ByteBuffer buffer, long sid) {
            this.buffer = buffer;
            this.sid = sid;
        }
    }

    public QuorumCnxManager(QuorumPeer self) {
        this.recvQueue = new ArrayBlockingQueue<Message>(RECV_CAPACITY);
        this.queueSendMap = new ConcurrentHashMap<Long, ArrayBlockingQueue<ByteBuffer>>();
        this.senderWorkerMap = new ConcurrentHashMap<Long, SendWorker>();
        this.lastMessageSent = new ConcurrentHashMap<Long, ByteBuffer>();

        String cnxToValue = System.getProperty("zookeeper.cnxTimeout");
        if(cnxToValue != null){
            this.cnxT0 = new Integer(cnxToValue);
        }

        this.self = self;

        // Starts listener thread that waits for connection requests
        listener = new Listener();
    }

    // Invokes initiateConnection for testing purpose
    public void testInitiateConnection(long sid) throws Exception{
        LOG.info("Opening channel to server " + sid);

        Socket sock = new Socket();
        setSocketOpts(sock);
        sock.connect(self.getVotingView().get(sid).electionAddr, cnxT0);
        initiateConnection(sock, sid);
    }

    public boolean initiateConnection(Socket sock, Long sid){
        DataOutputStream dout = null;
        try{
            // Sending id and challenge
            dout = new DataOutputStream(sock.getOutputStream());
            dout.writeLong(self.getId());
            dout.flush();
        }catch (IOException e){
            LOG.info("Ignoring  exception reading or writing challenge : ", e);
            closeSocket(sock);
            return false;
        }

        // If lost the challenge, when drop the new connection
        if(sid > self.getId()){
            LOG.info("Have smaller server identifier, so dropping the " +
                " connection : ( " + sid + " , " + self.getId() + ")");
            closeSocket(sock);
            // Otherwise proceed with the connectin
        }
        else{
            SendWorker sw = new SendWorker(sock, sid);
            RecvWorker rw = new RecvWorker(sock, sid, sw);
            sw.setRecv(rw);

            SendWorker vsw = senderWorkerMap.get(sid);

            if(vsw != null){
                vsw.finish();
            }
            senderWorkerMap.put(sid, sw);
            if(!queueSendMap.containsKey(sid)){
                queueSendMap.put(sid, new ArrayBlockingQueue<ByteBuffer>(SEND_CAPACITY));
            }

            sw.start();
            rw.start();

            return true;
        }

        return false;
    }

    /**
     * If this server receives a connection request, then it gives up on the new
     * connection if it wins. Notice that it checks whether it has a connection
     * to this server already or not. If it does, then it sends the smallest
     * possible long value to lose the challenge
     */
    public boolean receiveConnection(Socket sock){
        Long sid = null;

        try{
            // Read server id
            DataInputStream din = new DataInputStream(sock.getInputStream());
            sid = din.readLong();
            // this is not a server id but a protocol version (see ZOOKEEPER-1633)
            if(sid < 0){
                sid = din.readLong();
                // next comes the #bytes in the remainder of the message
                int num_remaining_bytes = din.readInt();
                byte[] b = new byte[num_remaining_bytes];
                // remove the remainder of the message from din
                int num_read = din.read(b);
                if(num_read != num_remaining_bytes){
                    LOG.info("Read only " + num_read + " bytes out of " +
                    num_remaining_bytes + " sent by server " + sid);
                }
            }

            if(sid == QuorumPeer.OBSERVER_ID){
                // Chose identifier at random. We need a value to identify
                // the connection
                sid = observerCounter--;
                LOG.info("Setting arbitray identifier to observer: " + sid);
            }
        }catch (IOException e){
            closeSocket(sock);
            LOG.info("Exception reading or writing challenge : " + e.toString());
            return false;
        }

        // if wins the challenge, then close the new connection
        if(sid < self.getId()){
            /**
             * This replica might still believe that the connection to sid is
             * up, so we have to shut down workers before trying to open a
             * new connection
             */
            SendWorker sw = senderWorkerMap.get(sid);
            if(sw != null){
                sw.finish();
            }
            // Now we start a new connection
            LOG.info("Create new connection to server : " + sid);
            closeSocket(sock);
            connecOne(sid);
            // Otherwise start worker threads to receive data
        }
        else{
            SendWorker sw = new SendWorker(sock, sid);
            RecvWorker rw = new RecvWorker(sock, sid, sw);

            sw.setRecv(rw);

            SendWorker vsw = senderWorkerMap.get(sid);

            if(vsw != null){
                vsw.finish();
            }

            senderWorkerMap.put(sid, sw);
            if(!queueSendMap.containsKey(sid)){
                queueSendMap.put(sid, new ArrayBlockingQueue<ByteBuffer>(SEND_CAPACITY));
            }

            sw.start();
            rw.start();
            return true;
        }
        return false;
    }

    /**
     * Processes invoke this message to queue a message to send, Currently
     * only leader election uses it
     */
    public void toSend(Long sid, ByteBuffer b){
        // if sending message to myself, then simply enqueue it (loopback)
        if(self.getId() == sid){
            b.position(0);
            addToRecvQueue(new Message(b.duplicate(), sid));
            // Otherwise send to the corresponding thread to send
        }
        else{
            // start a new connection if doesn't have one already
            if(!queueSendMap.containsKey(sid)){
                ArrayBlockingQueue<ByteBuffer> bq = new ArrayBlockingQueue<ByteBuffer>(SEND_CAPACITY);
                queueSendMap.put(sid, bq);
                addToSendQueue(bq, b);
            }
            else{
                ArrayBlockingQueue<ByteBuffer> bq = queueSendMap.get(sid);
                if(bq != null){
                    addToSendQueue(bq, b);
                }
                else{
                    LOG.info("No queue for server " + sid);
                }
            }
            connectOne(sid);
        }
    }

    // Try to establish a connection to server with id sid
    public synchronized void connectOne(long sid){
        if(senderWorkerMap.get(sid) == null){
            InetSocketAddress electionAddr;
            if(self.quorumPeers.containsKey(sid)){
                electionAddr = self.quorumPeers.get(sid).electionAddr;
            }else{
                LOG.info("Invalid server id :" + sid);
                return;
            }

            try{
                LOG.info("Opening channel to server " + sid);
                Socket sock = new Socket();
                setSockOpts(sock);
                sock.connect(self.getView().get(sid).electionAddr, cnxT0);
                LOG.info("Connected to server " + sid);
                initiateConnection(sock, sid);
            }catch (Exception e){
                LOG.info("Cannot open channel to :" + sid +
                        " at election address " + electionAddr, e);
                e.printStackTrace();
            }
        }
        else{
            LOG.info("There is a connection already for server " + sid);
        }
    }

    /**
     * Try to establish a connection with each server if one
     * doesn't exist
     */
    public void connectAll(){
        long sid;
        for(Enumeration<Long> en = queueSendMap.keys(); en.hasMoreElements();){
            sid = en.nextElement();
            connectOne(sid);
        }
    }

    // Check if all queues are empty, indicating that all messages have been delivered
    public boolean haveDelivered(){
        for(ArrayBlockingQueue<ByteBuffer> queue : queueSendMap.values()){
            LOG.info("Queue size : " + queue.size());
            if(queue.size() == 0){
                return true;
            }
        }

        return false;
    }

    // Flag that it is time to wrap up all activities and interrupt the listener
    public void halt(){
        shutdown = true;
        LOG.info("Halting listener");
        listener.halt();

        softHalt();
    }

    // A soft halt simply finishes workers
    public void softHalt(){
        for(SendWorker sw : senderWorkerMap.values()){
            LOG.info("Halting sender : " + sw);
            sw.finish();
        }
    }

    // Helper method to set socket options
    public void setSockOpts(Socket sock) throws Exception{
        sock.setTcpNoDelay(true);
        sock.setSoTimeout(self.tickTime * self.syncLimit);
    }

    // Helper method to close a socket
    public void closeSocket(Socket sock){
        try{
            sock.close();
        }catch (Exception e){
            LOG.info("Exception while closing", e);
        }
    }

    // Return number of worker threads
    public long getThreadCount(){
        return threadCnt.get();
    }

    // Return reference to QuorumPeer
    public QuorumPeer getQuorumPeer(){
        return self;
    }

    // Thread to listen on some port
    public class Listener extends Thread{

        public volatile ServerSocket ss = null;

        // Sleep on accept()
        @Override
        public void run() {

            int numRetries = 0;
            InetSocketAddress addr;
            while((!shutdown) && (numRetries < 3)){
                try{

                    ss = new ServerSocket();
                    ss.setReuseAddress(true);
                    if(self.getQuorumListenOnAllIPs()){
                        int port = self.quorumPeers.get(self.getId()).electionAddr.getPort();
                        addr = new InetSocketAddress(port);
                    }else{
                        addr = self.quorumPeers.get(self.getId()).electionAddr;
                    }
                    LOG.info("My election bind port : " + addr.getPort());
                    setName(self.quorumPeers.get(self.getId()).electionAddr.toString());
                    ss.bind(addr);
                    while(!shutdown){
                        Socket client = ss.accept();
                        setSockOpts(client);
                        LOG.info("Received connection request "
                            + client.getRemoteSocketAddress());
                        receiveConnection(client);
                        numRetries = 0;
                    }

                }catch (Exception e){
                    LOG.info("Exception while listening", e);
                    numRetries++;
                    try{
                        ss.close();
                        Thread.sleep(1000);
                    }catch (Exception e1){
                        LOG.info("Error closing server socket", e1);
                    }
                }
            }

            LOG.info("Leaving listener");
            if(!shutdown){
                LOG.info("As I'm leaving the listener thread, "
                 + " I won't be able to participate in leader "
                + " election any longer :"
                + self.quorumPeers.get(self.getId()).electionAddr);
            }
        }

        // Halts this listener thread
        public void halt(){
            try{
                LOG.info("Trying to close listener: " + ss);
                if(ss != null){
                    LOG.info("Closing listener : " + self.getId());
                    ss.close();
                }
            }catch (Exception e){
                LOG.info("Exception when shutting down listener :" , e);
            }
        }
    }

    /**
     * Thread to send messages. Instance waits on a queue, and send a message as
     * soon as there is one available. If connection breaks, then opens a new
     * one
     */
    class SendWorker extends Thread{
        public Long sid;
        public Socket sock;
        public RecvWorker recvWorker;
        public volatile boolean running = true;
        public DataOutputStream dout;

        /**
         * An instance of this thread receives message to send
         * through a queue and sends them to the server sid
         */
        public SendWorker(Socket sock, Long sid) {
            super("SendWorker " + sid);
            this.sid = sid;
            this.sock = sock;
            recvWorker = null;
            try{
                dout = new DataOutputStream(sock.getOutputStream());
            }catch (Exception e){
                LOG.info("Unable to access socket output stream");
                closeSocket(sock);
                running = false;
            }
            LOG.info("Address of remote peer :" + this.sid);
        }

        public synchronized void setRecv(RecvWorker recvWorker){
            this.recvWorker = recvWorker;
        }

        public synchronized RecvWorker getRecvWorker(){
            return recvWorker;
        }

        public synchronized boolean finish(){
            LOG.info("Calling finish for :" + sid);
            if(!running){
                // Avoids running finish() twice
                return running;
            }
            running = false;
            closeSocket(sock);

            this.interrupt();
            if(recvWorker != null) recvWorker.finish();
            LOG.info("Removing entry from senderWorkerMap sid=" + sid);
            senderWorkerMap.remove(sid, this);
            threadCnt.decrementAndGet();
            return running;
        }

        public synchronized void send(ByteBuffer b) throws Exception{
            byte[] msgBytes = new byte[b.capacity()];
            try{
                b.position(0);
                b.get(msgBytes);
            }catch (Exception e){
                LOG.info("BufferUnderFlowException " , e);
                return;
            }

            dout.writeInt(b.capacity());
            dout.write(b.array());
            dout.flush();
        }

        @Override
        public void run() {
            threadCnt.incrementAndGet();
            try{
                /**
                 * If there is nothing in the queue to send, then we
                 * send the lastMessage to ensure that the last message
                 * was received by the peer. The message could be dropped
                 * in case self or the peer shutdown their connection
                 * prior to reading/processing
                 * the last message. Duplicate messages are handled correctly
                 * by the peer
                 *
                 * If the send queue is non-empty, then we have a recent
                 * message than that stored in lastMessage. To avoid sending
                 * stale message, we should send the message in the send queue
                 *
                 */
                ArrayBlockingQueue<ByteBuffer> bq = queueSendMap.get(sid);
                if(bq == null || isSendQueueEmpty(bq)){
                    ByteBuffer b = lastMessageSent.get(sid);
                    if(b != null){
                        LOG.info("Attempting to send lastMessage to sid = " + sid);
                        send(b);
                    }
                }
            }catch (Exception e){
                LOG.info("Failed to send last message Shutting down thread", e);
                this.finish();
            }

            try{
                while(running && !shutdown && sock != null){
                    ByteBuffer b = null;
                    try{
                        ArrayBlockingQueue<ByteBuffer> bq = queueSendMap.get(sid);
                        if(bq != null){
                            b = pollSendQueue(bq, 1000, TimeUnit.MICROSECONDS);
                        }else{
                            LOG.info("No queue of incoming message for " + " server " + sid);
                            break;
                        }

                        if(b != null){
                            lastMessageSent.put(sid, b);
                            send(b);
                        }
                    }catch (Exception e){
                        LOG.info("Interrupted while waiting for message on queue", e);
                    }
                }
            }catch (Exception e){
                LOG.info("Exception when using channel: for id " + sid +
                    " my id = " + self.getId() + " error = " + e);
            }
            this.finish();
            LOG.info("Send worker leaving thread");
        }
    }

    /**
     * Thread to receive messages. Instance waits on a socket read. If the
     * channel breaks, then removes itself from the pool of receivers
     */
    class RecvWorker extends Thread{
        Long sid;
        Socket sock;
        volatile  boolean running = true;
        DataInputStream din;
        public SendWorker sw;

        public RecvWorker(Socket sock, Long sid,  SendWorker sw) {
            super("RecvWorker:" + sid);
            this.sid = sid;
            this.sock = sock;
            this.sw = sw;
            try{
                din = new DataInputStream(sock.getInputStream());
                // Ok to wait Until socket disconnects while reading
                sock.setSoTimeout(0);
            }catch (Exception e){
                LOG.info("Error while accessing socket for " + sid, e);
                closeSocket(sock);
                running = false;
            }
        }


        public synchronized boolean finish(){
            if(!running){
                // Avoids running finish() twice
                return running;
            }
            running = false;
            this.interrupt();
            threadCnt.decrementAndGet();
            return running;
        }

        @Override
        public void run() {
            threadCnt.incrementAndGet();
            try{
                while(running && !shutdown && sock != null){
                    // Reads the first int to determine the length of the
                    // message
                    int length = din.readInt();
                    if(length <= 0 || length > PACKETMAXSIZE){
                        throw new Exception("Received packet with invalid packet: "
                        + length);
                    }

                    // Allocates a new ByteBuffer to receive the message
                    byte[] msgArray = new byte[length];
                    din.readFully(msgArray, 0, length);
                    ByteBuffer message = ByteBuffer.wrap(msgArray);
                    addToRecvQueue(new Message(message.duplicate(), sid));
                }
            }catch (Exception e){
                LOG.info("Connection broken for id : " + sid + ", myid = "
                + self.getId() + ", error = ", e);
            }finally {
                LOG.info("Interrupting SendWorker");
                sw.finish();
                if(sock != null) closeSocket(sock);
            }
        }
    }


    /**
     * Inserts an element in the specified queue, if the queue is full, this
     * method removes an element from the head of the Queue and then inserts
     * the element at the tail, It can happen that an element is removed
     * by another thread in SendWorker#processMessage() processMessage
     * method before this method attempts to remove an element from the queue
     * This will cause ArrayBlockingQueue#remove() to throw an
     * exception, which is safe to ignore
     *
     * Unlike addToRecvQueue this method does
     * not need to be synchronized since there in only thread that inserts
     * an element in the qeueue and another thread reads from the queue
     */
    public void addToSendQueue(ArrayBlockingQueue<ByteBuffer> queue, ByteBuffer buffer){
        if(queue.remainingCapacity() == 0){
            try{
                queue.remove();
            }catch (Exception e){
                LOG.info("Trying to remove from an empty " +
                "Queue Ignoring exception");
            }
        }

        try{
            queue.add(buffer);
        }catch (Exception e){
            LOG.info("Unable to insert an element in the queue");
        }
    }

    // Returns true if queue is empty
    public boolean isSendQueueEmpty(ArrayBlockingQueue<ByteBuffer> queue){
        return queue.isEmpty();
    }

    /**
     * Retrieves and removes buffer at the head of this queue
     * waiting up the specified wait time if necessary for an element to
     * become available
     */
    public ByteBuffer pollSendQueue(ArrayBlockingQueue<ByteBuffer> queue, long timeout,
                                    TimeUnit unit) throws Exception{
        return queue.poll(timeout, unit);
    }


    public void addToRecvQueue(Message msg){
        synchronized (recvQLock){
            if(recvQueue.remainingCapacity() == 0){
                try{
                    recvQueue.remove();
                }catch (Exception e){
                    LOG.info("Tring to remove from an empty"
                    + "recvQueue Ignoring exception" + e);
                }
            }

            try{
                recvQueue.add(msg);
            }catch (Exception e){
                LOG.info("Unable to insert element in the recvQueue" + e);
            }
        }
    }

    /**
     * Retrieves and removes a message at the head of this queue
     * waiting up to the specified wait time if necessary for an element to
     * become available
     */
    public Message pollRecvQueue(long timeout, TimeUnit unit)throws Exception{
        return recvQueue.poll(timeout, unit);
    }
}
