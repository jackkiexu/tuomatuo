package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperThread;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

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
        
    }

}
