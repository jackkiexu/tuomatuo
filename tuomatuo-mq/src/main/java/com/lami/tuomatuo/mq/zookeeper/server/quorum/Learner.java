package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.*;
import com.lami.tuomatuo.mq.zookeeper.server.util.ZxidUtils;
import org.apache.jute.*;
import org.apache.zookeeper.server.quorum.LearnerInfo;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.apache.zookeeper.txn.TxnHeader;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class is the superclass of two of the three main actors in a ZK
 * ensemble: Followers and Observers. Both Followers and Observers sahre
 * a good deal od code which is moved into Peer to avoid duplication
 *
 * Created by xujiankang on 2017/3/19.
 */
public class Learner {

    public static final Logger LOG= LoggerFactory.getLogger(Learner.class);

    static final public boolean nodelay = System.getProperty("follower.nodelay", "true").equals("true");

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

    /** thr protocol version of the leader */
    protected int leaderProtocolVersion = 0x01;


    // the protocol version of the leader
    public final ConcurrentHashMap<Long, ServerCnxn> pendingRevalidations = new ConcurrentHashMap<>();

    public int getPendingRevalidationsCount(){
        return pendingRevalidations.size();
    }

    /**
     * validate a session for a client
     */
    public void validateSession(ServerCnxn cnxn, long clientId, int timeout)throws Exception{
        LOG.info("Revalidating client : 0x" + Long.toHexString(clientId));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeLong(clientId);
        dos.writeInt(timeout);
        dos.close();
        QuorumPacket qp = new QuorumPacket(Leader.REVALIDATE, -1, baos.toByteArray(), null);
        pendingRevalidations.put(clientId, cnxn);
        writePacket(qp, true);
    }

    // write a packet to the leader
    public void writePacket(QuorumPacket pp, boolean flush) throws Exception{
        synchronized (leaderOs){
            if(pp != null){
                leaderOs.writeRecord(pp, "packet");
            }
            if(flush){
                bufferedOutput.flush();
            }
        }
    }

    // read a packet from the leader
    public void readPacket(QuorumPacket pp) throws Exception{
        synchronized (leaderIs){
            leaderIs.readRecord(pp, "packet");
        }

        long traceMask = ZooTrace.SERVER_PACKET_TRACE_MASK;
        if(pp.getType() == Leader.PING){
            traceMask = ZooTrace.SERVER_PING_TRACE_MASK;
        }
    }

    // send a request packet to the leader
    public void request(Request request) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oa = new DataOutputStream(baos);
        oa.writeLong(request.sessionId);
        oa.writeInt(request.cxid);
        oa.writeInt(request.type);
        if(request.request != null){
            request.request.rewind();
            int len = request.request.remaining();
            byte b[] = new byte[len];
            request.request.get(b);
            request.request.rewind();
            oa.write(b);
        }
        oa.close();
        QuorumPacket qp = new QuorumPacket(Leader.REQUEST, -1, baos.toByteArray(), request.authInfo);
        writePacket(qp, true);
    }

    // Returns the address of the node we think is the leader
    public InetSocketAddress findLeader(){
        InetSocketAddress addr = null;
        // Find the leader by id
        Vote current = self.getCurrentVote();
        for(QuorumPeer.QuorumServer s : self.getView().values()){
            if(s.id == current.getId()){
                addr = s.addr;
                break;
            }
        }

        return addr;
    }

    // Establish a connection with the leader found by findleader. Retries
    // 5 time before giving up
    public void connectToLeader(InetSocketAddress addr) throws Exception{
        sock = new Socket();
        sock.setSoTimeout(self.tickTime * self.initLimit);
        for(int tries = 0; tries < 5; tries++){
            try{
                sock.connect(addr, self.tickTime * self.syncLimit);
                sock.setTcpNoDelay(nodelay);
                break;
            }catch (Exception e){
                if(tries == 4){
                    LOG.info("Unexpected exception", e);
                    throw e;
                }else{
                    LOG.info("Unexpected exception, tries=" + tries
                    + ", connecting to" + addr, e);
                    sock = new Socket();
                    sock.setSoTimeout(self.tickTime * self.initLimit);
                }
            }
        }

        leaderIs = BinaryInputArchive.getArchive(new BufferedInputStream(sock.getInputStream()));
        bufferedOutput = new BufferedOutputStream(sock.getOutputStream());
        leaderOs = BinaryOutputArchive.getArchive(bufferedOutput);
    }

    /**
     * Once connected to the leader, perform the handshake protocol to
     * establish a following / observing connection
     * @return the zxid the leader sends for synchronization purposes
     */
    public long registerWithLeader(int pktType) throws Exception{
        // Send follower info, including last zxid and sid
        long lastLoggedZxid = self.getLastLoggedZxid();
        QuorumPacket qp = new QuorumPacket();
        qp.setType(pktType);
        qp.setZxid(ZxidUtils.makeZxid(self.getAcceptedEpoch(), 0));

        // add sid to payload
        LearnerInfo li = new LearnerInfo(self.getId(), 0x10000);
        ByteArrayOutputStream bsid = new ByteArrayOutputStream();
        BinaryOutputArchive boa = BinaryOutputArchive.getArchive(bsid);

        boa.writeRecord(li, "LearnerInfo");
        qp.setData(bsid.toByteArray());

        writePacket(qp, true);
        readPacket(qp);
        final  long newEpoch = ZxidUtils.getEpochFromZxid(qp.getZxid());
        if(qp.getType() == Leader.LEADERINFO){
            // we are connected to a 1.0 server so accept the new epoch and read the next packet
            leaderProtocolVersion = ByteBuffer.wrap(qp.getData()).getInt();
            byte epochBytes[] = new byte[4];
            final ByteBuffer wrappedEpochBytes = ByteBuffer.wrap(epochBytes);
            if(newEpoch > self.getAcceptedEpoch()){
                wrappedEpochBytes.putInt((int)self.getCurrentEpoch());
                self.setAcceptedEpoch(newEpoch);
            }else if(newEpoch == self.getAcceptedEpoch()){
                /**
                 * Since we have already acked an epoch equal to the leaders, we cannot ack
                 * again, but we still need to send our lastZxid to the leader so that we can
                 * sync with it if it does assume leadership of the epoch
                 * the -1 indicates that this reply should not count as an ack for the new epoch
                 */
                wrappedEpochBytes.putInt(-1);
            }else{
                throw new Exception("Leaders epoch, " + newEpoch + " is less than accepted epoch " + self.getAcceptedEpoch());
            }

            QuorumPacket acknewEpoch = new QuorumPacket(Leader.ACKEPOCH, lastLoggedZxid, epochBytes, null);
            writePacket(acknewEpoch, true);
            return ZxidUtils.makeZxid(newEpoch, 0);
        }
        else{
            if(newEpoch > self.getAcceptedEpoch()){
                self.setAcceptedEpoch(newEpoch);
            }
            if(qp.getType() != Leader.NEWLEADER){
                LOG.info("First packet should have been NEWLEADER");
                throw new IOException("First packet should have been NEWLEADER");
            }
            return qp.getZxid();
        }

    }

    // Finally, synchronize our history with the Leader
    public void syncWithLeader(long newLeaderZxid) throws Exception{
        QuorumPacket ack = new QuorumPacket(Leader.ACK, 0, null, null);
        QuorumPacket qp = new QuorumPacket();
        long newEpoch = ZxidUtils.getEpochFromZxid(newLeaderZxid);

        readPacket(qp);
        LinkedList<Long> packetsCommitted = new LinkedList<>();
        LinkedList<PacketInFlight> packetsNotCommitted = new LinkedList<>();
        synchronized (zk){
            if(qp.getType() == Leader.DIFF){
                LOG.info("Getting a diff from the leader 0x" + Long.toHexString(qp.getZxid()));
            }
            else if(qp.getType() == Leader.SNAP){
                LOG.info("Getting a snapshot from leader");
                // The leader is going to dump the database
                // clear our own database and read
                zk.getZKDatabase().clear();
                zk.getZKDatabase().deserializeSnapshot(leaderIs);                       // 从数据流中解析出数据
                String signature = leaderIs.readString("signature");
                if(!signature.equals("BeanWasHere")){
                    LOG.info("Missing signature. Got " + signature);
                    throw new IOException("Missing signature");
                }
            }
            else if(qp.getType() == Leader.TRUNC){
                // we need to truncate the log to the lastzxid of the leader
                LOG.info("Truncating log to get in sync with the leader 0x " + Long.toHexString(qp.getZxid()));
                boolean truncated = zk.getZKDatabase().truncateLog(qp.getZxid());
                if(!truncated){
                    // not able to truncate the log
                    LOG.info("Not able to truncate the log" + Long.toHexString(qp.getZxid()));
                    System.exit(13);
                }
            }
            else {
                LOG.info("Got unexpected packet from leader " + qp.getType() + " exiting ...");
                System.exit(13);
            }

            zk.getZKDatabase().setlastProcessedZxid(qp.getZxid());
            zk.createSessionTracker();

            long lastQueued = 0;

            /**
             * in V1.0 we take a snapshot when we get the NEWLEADer message, but in pre V1.0
             * we take snapshot at the UPDATE, since V1.0 also gets the UPDATE (after the NEWLEADER)
             * we need to make sure that we don't take snapshot twice
             */
            boolean snapshotTaken = false;
            // we are now going to start getting transactions to apply followed by an UPTODATE
            outerLoop:
            while(self.isRunning()){

            }

            ack.setZxid(ZxidUtils.makeZxid(newEpoch, 0));
            writePacket(ack, true);
            sock.setSoTimeout(self.tickTime * self.syncLimit);
            zk.startup();
            /**
             * Update the election vote here to ensure that all members of the
             * ensemble report the same vote to new servers that start up and
             * send leader election notification to the ensemble
             */
            self.updateElectionVote(newEpoch);

            // we need to log the stuff that came in between the snapshot and the uptodate
            if(zk instanceof  FollowerZooKeeperServer){
                FollowerZooKeeperServer fzk = (FollowerZooKeeperServer)zk;
                for(PacketInFlight p : packetsNotCommitted){
                    fzk.logRequest(p.hdr, p.rec);
                }
                for(Long zxid : packetsCommitted){
                    fzk.commit(zxid);
                }
            }
            else if(zk instanceof ObserverZooKeeperServer){
                // Similar to follower, we need to log requests between the snapshot
                // and UPTODATE
                ObserverZooKeeperServer ozk = (ObserverZooKeeperServer)zk;
                for(PacketInFlight p : packetsNotCommitted){
                    Long zxid = packetsCommitted.peekFirst();
                    if(p.hdr.getZxid() != zxid){
                        /**
                         * log warning message if there is no matching commit
                         * old leader send oytstanding proposal to observer
                         */
                        LOG.info("Committing " + Long.toHexString(zxid)
                                + ", but next proposal is"
                                + Long.toHexString(p.hdr.getZxid()));
                        continue;
                    }
                    packetsCommitted.remove();
                    Request request = new Request(null, p.hdr.getClientId(),
                            p.hdr.getCxid(), p.hdr.getType(), null, null);
                    request.txn = p.rec;
                    request.hdr = p.hdr;
                    ozk.commitRequest(request);
                }
            }
            else{
                // NEW server type need to handle in-flight packets
                throw new UnsupportedOperationException("Unknown server type");
            }
        }
    }


    public void revalidate(QuorumPacket qp) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(qp.getData());
        DataInputStream dis = new DataInputStream(bis);
        long sessionId = dis.readLong();
        boolean valid = dis.readBoolean();
        ServerCnxn cnxn = pendingRevalidations.remove(sessionId);
        if(cnxn != null){
            zk.finishSessionInit(cnxn, valid);
        }
        else{
            zk.finishSessionInit(cnxn, valid);
        }

    }


    protected void ping(QuorumPacket qp) throws Exception{
        // Send back the ping with our session data
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        HashMap<Long, Integer> touchTable = zk.getTouchSnapshot();
        for(Map.Entry<Long, Integer> entry : touchTable.entrySet()){
            dos.writeLong(entry.getKey());
            dos.writeInt(entry.getValue());
        }
        qp.setData(bos.toByteArray());
        writePacket(qp, true);
    }

    // shutdown the Peer
    public void shutdown(){
        // set the zookeeper server to null
        self.cnxnFactory.setZooKeeperServer(null);
        // clear all the connections
        self.cnxnFactory.closeAll();
        // shutdown previous zookeeper
        if(zk != null){
            zk.shutdown();
        }
    }
}
