package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.server.ByteBufferInputStream;
import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperThread;
import com.lami.tuomatuo.mq.zookeeper.server.ZooTrace;
import com.lami.tuomatuo.mq.zookeeper.server.util.ZxidUtils;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.zookeeper.server.quorum.LearnerInfo;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * There will be an interface of this class created by the Leader for each
 * leaner, All communication with a learner is handled by this
 * class
 *
 * Created by xujiankang on 2017/3/19.
 */
public class LearnerHandler extends Thread {

    public static final Logger LOG = LoggerFactory.getLogger(LearnerHandler.class);

    public Socket sock;

    public Socket getSock() {
        return sock;
    }

    public Leader leader;

    /**
     * Deadline for receiving the next ack. If we are bootstrapping then
     * it's based on the initLimit, if we are done bootstrapping it's based
     * on the syncLimit. Once the deadLine is past this learner should
     * be considered no longer 'sync'd with the leader
     */
    public volatile long tickOfNextAckDeadline;

    // ZooKeeper server identifier of this learner
    public long sid = 0;

    public long getSid() {
        return sid;
    }

    public int version = 0x1;

    public int getVersion() {
        return version;
    }

    public LinkedBlockingQueue<QuorumPacket> queuedPackets = new LinkedBlockingQueue<>();

    public LearnerHandler(String name) {
        super(name);
    }

    /**
     * This class controls the time that the Leader has been
     * waiting for acknowledgement of a proposal from this Learner
     * If the time is above syncLimit, the connection will be closed
     * It keeps track of only one proposal at a time, when the ACK for
     * that proposal arrives, it switches to the last proposal received
     * or clear the value if there is no pending proposal
     */
    public class SyncLimitCheck {
        public boolean started = false;
        public long currentZxid = 0;
        public long currentTime = 0;
        public long nextZxid = 0;
        public long nextTime = 0;

        public synchronized void start() {
            started = true;
        }

        public synchronized void  updateProposal(long zxid, long time){
            if(!started){
                return;
            }

            if(currentTime == 0){
                currentTime = time;
                currentZxid = zxid;
            }else{
                nextTime = time;
                nextZxid = zxid;
            }
        }

        public synchronized void updateAck(long zxid){
            if(currentZxid == zxid){
                currentTime = nextTime;
                currentZxid = nextZxid;
                nextTime = 0;
                nextZxid = 0;
            }
            else if(nextZxid == zxid){
                LOG.info("ACK for " + zxid + " received before ACK for " + currentZxid + " !!!");
                nextTime = 0;
                nextZxid = 0;
            }
        }


        public synchronized boolean check(long time){
            if(currentTime == 0){
                return true;
            }
            else{
                long msDelay = (time -currentTime) / 1000000;
                return (msDelay < (leader.self.tickTime * leader.self.syncLimit));
            }
        }
    }

    public SyncLimitCheck syncLimitCheck = new SyncLimitCheck();

    public BinaryInputArchive ia;

    public BinaryOutputArchive oa;

    public BufferedOutputStream bufferedOutput;

    public LearnerHandler(Socket sock, Leader leader) throws Exception{
        super("LearnerHandler--" + sock.getRemoteSocketAddress());
        this.sock = sock;
        this.leader = leader;
        leader.addLearnerHandler(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LearnerHandler ").append(sock);
        sb.append(" tickOfNextAckDeadline:").append(tickOfNextAckDeadline());
        sb.append(" synced?:").append(synced());
        sb.append(" queuedPacketLength:").append(queuedPackets.size());
        return sb.toString();
    }


    // If this packet is queued, the sender thread will exit
    public QuorumPacket proposalDeath = new QuorumPacket();

    public QuorumPeer.LearnerType learnerType = QuorumPeer.LearnerType.PARRTICIPANT;
    public QuorumPeer.LearnerType getLearnerType() {
        return learnerType;
    }

    /**
     * This method will use the thread to send packets added to the queuePackets list
     */
    public void sendPackets() throws Exception{
        long traceMask = ZooTrace.SERVER_PACKET_TRACE_MASK;
        while(true){
            try{
                QuorumPacket p;
                p = queuedPackets.poll();
                if(p == null){
                    bufferedOutput.flush();
                    p = queuedPackets.take();
                }

                if(p == proposalDeath){
                    // Packet of death
                    break;
                }
                if(p.getType() == Leader.PING){
                    traceMask = ZooTrace.SERVER_PING_TRACE_MASK;
                }
                if(p.getType() == Leader.PROPOSAL){
                    ZooTrace.logQuorumPacket(LOG, traceMask, 'o', p);
                }
                oa.writeRecord(p, "packet");
            }catch (Exception e){
                if(!sock.isClosed()){
                    LOG.info("Unexpected exception at " + this, e);
                    try{
                        /**
                         * this will cause everything to shutdown on
                         * this learner handler and will help notify
                         * the learner/observer instaneously
                         */
                        sock.close();
                    }catch (Exception ei){
                        LOG.info("Error closing socket for handler " + this, ei);
                    }
                }
                break;
            }
        }
    }

    /**
     * This thread will receive packets from the peer and process them and
     * also listen to new connections from new peers
     */
    @Override
    public void run() {
        try{
            tickOfNextAckDeadline = leader.self.tick
                    + leader.self.initLimit + leader.self.syncLimit;

            ia = BinaryInputArchive.getArchive(new BufferedInputStream(sock.getInputStream()));
            bufferedOutput = new BufferedOutputStream(sock.getOutputStream());
            oa = BinaryOutputArchive.getArchive(bufferedOutput);

            QuorumPacket qp = new QuorumPacket();
            ia.readRecord(qp, "packet");
            if(qp.getType() != Leader.FOLLOWERINFO && qp.getType() != Leader.OBSERVERINFO){
                LOG.info("First packet " + qp.toString()
                    + " is not FOLLOWERINFO or OBSERVERINFO");
                return;
            }
            byte learnerInfoData[] = qp.getData();
            if(learnerInfoData != null){
                if(learnerInfoData.length == 8){
                    ByteBuffer bbsid = ByteBuffer.wrap(learnerInfoData);
                    this.sid = bbsid.getLong();
                }
                else{
                    LearnerInfo li = new LearnerInfo();
                    ByteBufferInputStream.byteBuffer2Record(ByteBuffer.wrap(learnerInfoData), li);
                    this.sid = li.getServerid();
                    this.version = li.getProtocolVersion();
                }
            }
            else{
                this.sid = leader.followerCounter.getAndDecrement();
            }

            if(qp.getType() == Leader.OBSERVERINFO){
                learnerType = QuorumPeer.LearnerType.OBSERVER;
            }

            long lastAcceptedEpoch = ZxidUtils.getEpochFromZxid(qp.getZxid());

            long peerLastZxid;
            StateSummary ss = null;
            long zxid = qp.getZxid();
            long newEpoch = leader.getEpochToPropose(this.getSid(), lastAcceptedEpoch);

            if(this.getVersion() < 0x10000){
                // we are going to have to extrapolate the epoch information
                long epoch = ZxidUtils.getEpochFromZxid(zxid);
                ss = new StateSummary(epoch, zxid);
                // fake the message
                leader.waitForEpochAck(this.getSid(), ss);
            }
            else{
                byte ver[] = new byte[4];
                ByteBuffer.wrap(ver).putInt(0x10000);
                QuorumPacket newEpochPacket = new QuorumPacket(Leader.LEADERINFO, ZxidUtils.makeZxid(newEpoch, 0), ver, null );
                oa.writeRecord(newEpochPacket, "packet");
                bufferedOutput.flush();
                QuorumPacket ackEpochPacket = new QuorumPacket();
                ia.readRecord(ackEpochPacket, "packet");
                if(ackEpochPacket.getType() != Leader.ACKEPOCH){
                    LOG.info(ackEpochPacket.toString()
                            + " is not ACKEPOCH");
                    return;
                }

                ByteBuffer bbepoch = ByteBuffer.wrap(ackEpochPacket.getData());
                ss = new StateSummary(bbepoch.getInt(), ackEpochPacket.getZxid());
                leader.waitForEpochAck(this.getSid(), ss);
            }

            peerLastZxid = ss.getLastZxid();

            // the default to send the follower
            int packetToSend = Leader.SNAP;
            long zxidToSend = 0;
            long leaderLastZxid = 0;
            // the packets that the follower needs to get updates from
            long updates = peerLastZxid;

            // we are sending the diff check if we have proposals in me
            ReentrantReadWriteLock lock = leader.zk.getZKDatabase().getLogLock();
            ReentrantReadWriteLock.ReadLock rl = lock.readLock();
            try{

            }finally {
                rl.unlock();
            }

            // Start sending packets
            new Thread(){
                @Override
                public void run() {
                    Thread.currentThread().setName("Sender-" + sock.getRemoteSocketAddress());
                    try{
                        sendPackets();
                    }catch (Exception e){
                        LOG.info("UNexpected " + e);
                    }
                }
            }.start();

            /**
             * Have to wait for the first ACK, wait until
             * the leader is ready, and only then we can
             * start processing messages
             */
            qp = new QuorumPacket();
            ia.readRecord(qp, "packet");
            if(qp.getType() != Leader.ACK){
                LOG.info("Next packet was supposed to be an ACK");
                return;
            }
            LOG.info("Received NEWLEADER-ACK message from " + getSid());
            leader.waitForNewLeaderAck(getSid(), qp.getZxid(), getLearnerType());

            syncLimitCheck.start();

            // now that the ack has been peocessed expect the syncLimit
            sock.setSoTimeout(leader.self.tickTime * leader.self.syncLimit);

            // Wait until leader starts up
            synchronized (leader.zk){
                while(!leader.zk.isRunning() && !this.isInterrupted()){
                    leader.zk.wait(20);
                }
            }

            /**
             * Mutation packets will be queued during the serialize
             * so we need to mark when the peer can actually start
             * using the data
             */

            queuedPackets.add(new QuorumPacket(Leader.UPTODATE, -1, null, null));

            while (true){
                qp = new QuorumPacket();
                ia.readRecord(qp, "packet");

                long traceMask = ZooTrace.SERVER_PACKET_TRACE_MASK;
                if(qp.getType() == Leader.PING){
                    traceMask = ZooTrace.SERVER_PING_TRACE_MASK;
                }

                ZooTrace.logQuorumPacket(LOG, traceMask, 'i', qp);
                tickOfNextAckDeadline = leader.self.tick + leader.self.syncLimit;

                ByteBuffer bb;
                long sessionId;
                int cxid;
                int type;

                switch (qp.getType()){
                    case Leader.ACK:{
                        if(this.learnerType == QuorumPeer.LearnerType.OBSERVER){
                            LOG.info("Received ACK from Observer " + this.sid);
                        }

                        syncLimitCheck.updateAck(qp.getZxid());
                        leader.processAck(this.sid, qp.getZxid(), sock.getLocalSocketAddress());
                        break;
                    }
                    case Leader.PING:{
                        // Process the touches
                        ByteArrayInputStream bis = new ByteArrayInputStream(qp.getData());
                        DataInputStream dis = new DataInputStream(bis);
                        while(dis.available() > 0){
                            long sess = dis.readLong();
                            int to = dis.readInt();
                            leader.zk.touch(sess, to);
                        }
                        break;
                    }
                    case Leader.REVALIDATE:{
                        ByteArrayInputStream bis = new ByteArrayInputStream(qp.getData());
                        DataInputStream dis = new DataInputStream(bis);
                        long id = dis.readLong();
                        int to = dis.readInt();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        DataOutputStream dos = new DataOutputStream(bos);
                        dos.writeLong(id);
                        boolean valid = leader.zk.touch(id, to);
                        if(valid){
                            try{
                                /**
                                 * set the session owner
                                 * as the follower that
                                 * owns the session
                                 */
                                leader.zk.setOwner(id, this);
                            }catch (Exception e){
                                LOG.info("Somehow session " + Long.toHexString(id) + " expired right after being renewed");
                            }
                        }
                        ZooTrace.logTraceMessage(LOG,
                                ZooTrace.SESSION_TRACE_MASK,
                                "Session 0x" + Long.toHexString(id)
                                        + " is valid: " + valid);
                        dos.writeBoolean(valid);
                        qp.setData(bos.toByteArray());
                        queuedPackets.add(qp);
                        break;
                    }
                    case Leader.REQUEST:{
                        bb = ByteBuffer.wrap(qp.getData());
                        sessionId = bb.getLong();
                        cxid = bb.getInt();
                        type = bb.getInt();
                        bb = bb.slice();
                        Request si;
                        if(type == ZooDefs.OpCode.sync){
                            si = new LearnerSyncRequest(this, sessionId, cxid, type, bb, qp.getAuthinfo());

                        }else{
                            si = new Request(null, sessionId, cxid, type, bb, qp.getAuthinfo());
                        }
                        si.setOwner(this);
                        leader.zk.submitRequest(si);
                        break;
                    }
                    default:{

                    }

                }
            }

        }catch (Exception e){
            LOG.info("Unexpected expection causing shutdown ", e);
            try{
                sock.close();
            }catch (Exception e2){}
        }finally {
            LOG.info("***** GOODBYE "
            + (sock != null ? sock.getRemoteSocketAddress(): "<null>")
            + " ******* ");
            shutdown();
        }
    }

    public void shutdown(){
        // Send the packet of death
        try{

        }catch (Exception e){
            LOG.info("Ignoring unexpected exception", e);
        }

        try{
            if(sock != null && !sock.isClosed()){
                sock.close();
            }
        }catch (IOException e){
            LOG.info("Ignoring unexpected exception during socket close", e);
        }
        this.interrupt();
        leader.removeLearnerHandler(this);
    }

    public long tickOfNextAckDeadline(){
        return tickOfNextAckDeadline;
    }

    // pings calls from the leader to the peers
    public void ping(){
        long id;
        if(syncLimitCheck.check(System.nanoTime())){
            synchronized (leader){
                id = leader.lastProposed;
            }
            QuorumPacket ping = new QuorumPacket(Leader.PING, id, null, null);
            queuePacket(ping);
        }else{
            LOG.info("Closing connection to peer due to transaction timeout");
            shutdown();
        }
    }

    void queuePacket(QuorumPacket p){
        queuedPackets.add(p);
    }

    public boolean synced(){
        return isAlive()
                && leader.self.tick <= tickOfNextAckDeadline;
    }
}
