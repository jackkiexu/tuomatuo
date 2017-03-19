package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperThread;

/**
 * The class manages the quorum protocol. There are three states this server
 * can be in
 * Leader election - each server will elect a leader (proposing itself as a
 * leader initially)
 * Follower - the server will synchronize with the leader and replicate any
 * transaction
 * Leader - the server will process requests and forward them to followers
 * A majority of follower must log the request before it can be accepted
 *
 * This class will setup a datagram socket that will always respond with its
 * view of the current leader. The response will take the form of
 *
 * int xid;
 * long myid;
 * long leader_id
 * long leader_zxid
 *
 *
 * The request for the current leader will consist solely of an xid; int xid
 *
 * Created by xujiankang on 2017/3/19.
 */
public class QuorumPeer extends ZooKeeperThread implements QuorumStats.Provider{
    public QuorumPeer(String name) {
        super(name);
    }

    public int getQuorumSize(){
        return 0;
    }

    @Override
    public String[] getQuorumPeers() {
        return new String[0];
    }

    @Override
    public String getServerState() {
        return null;
    }
}
