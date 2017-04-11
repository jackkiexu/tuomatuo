package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ByteBufferInputStream;
import com.lami.tuomatuo.mq.zookeeper.server.ByteBufferOutputStream;
import com.lami.tuomatuo.mq.zookeeper.server.ServerCnxn;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class is the superclass of two of the three main actors in a ZK
 * ensemble: Followers and Observers. Both Followers and Observers sahre
 * a good deal od code which is moved into Peer to avoid duplication
 *
 * Created by xujiankang on 2017/3/19.
 */
public class Learner {

    private static final Logger LOG= LoggerFactory.getLogger(Learner.class);

    static {
        LOG.info("TCP NoDelay set to :" + nodelay);
    }

    static class PacketInFlight{
        TxnHeader hdr;
        Record rec;
    }

    public QuorumPeer self;
    public LearnerZooKeeperServer zk;

    public BufferedOutputStream bufferedOutput;
    public Socket sock;

    public Socket getSocket(){
        return sock;
    }

    public InputArchive leaderIs;
    public OutputArchive leaderOs;

    // the protocol version of the leader
    public final ConcurrentHashMap<Long, ServerCnxn> pendingRevalidations = new ConcurrentHashMap<>();

    public int getPendingRevalidationsCount(){
        return pendingRevalidations.size();
    }

    /**
     * validate a session for a client
     */
    public void validateSession(ServerCnxn cnxn, long clientId, int timeout)throws IOException{
        LOG.info("Revalidating client : 0x" + Long.toHexString(clientId));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
    }

}
