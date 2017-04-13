package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
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
        
        this.self = self;
    }
}
