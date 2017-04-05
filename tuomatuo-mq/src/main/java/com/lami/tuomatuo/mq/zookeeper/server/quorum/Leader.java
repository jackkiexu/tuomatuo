package com.lami.tuomatuo.mq.zookeeper.server.quorum;


import com.lami.tuomatuo.mq.zookeeper.server.Request;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

/**
 * This class has the control logic for the leader
 * Created by xujiankang on 2017/3/19.
 */
public class Leader {

    private static final Logger LOG = LoggerFactory.getLogger(Leader.class);

    static final private boolean nodelay = System.getProperty("leader.nodelay", "true").equals("true");

    static {
        LOG.info("TCP NoDelay set to : " + nodelay);
    }

    static public class Proposal{
        public QuorumPacket packet;

        public HashSet<Long> ackSet = new HashSet<>();

        public Request request;

        @Override
        public String toString() {
            return "Proposal{" +
                    "packet=" + packet +
                    ", ackSet=" + ackSet +
                    ", request=" + request +
                    '}';
        }
    }

    final LeaderZooKeeperServer zk;

    final QuorumPeer self;

    private boolean quorumFormed = false;

    public LearnerCnxnAcceptor cnxnAcceptor;


    class LearnerCnxnAcceptor extends Thread{

        private volatile boolean stop = false;

        @Override
        public void run() {
            try{
                Socket s = ss.accept();
                // start with the initLimit, once the ack is processed
                // in LearnerHandler switch to the syncLimit
                s.setSoTimeout(self.tickTime * self.initLimit);
                s.setTcpNoDelay(nodelay);
                LearnerHandler fh = new LearnerHandler(s, Leader.this);
                fh.start();
            }catch (SocketException e){
                if(stop){
                    LOG.info("exception while shutting down acceptor:" + e);
                    // When Leader.shutdown() calls ss.close()
                    // the call to accept throwns an exception
                    // we catch and set stop to true
                    stop = true;
                }
            }
        }

        public void halt(){
            stop = true;
        }
    }

}
