package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * Implementation of the local peer MBean interface
 * Created by xujiankang on 2017/3/19.
 */
public class LocalPeerBean extends ServerBean implements LocalPeerMXBean{

    private final QuorumPeer peer;

    public LocalPeerBean(QuorumPeer peer) {
        this.peer = peer;
    }

    @Override
    public String getName() {
        return "replica." + peer.getId();
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public int getTickTime() {
        return peer.getTickTime();
    }

    @Override
    public int getMaxClientCnxnPerHost() {
        return peer.getMaxClientCnxnPerHost();
    }

    @Override
    public int getMinSessionTimeout() {
        return peer.getMinSessionTimeout();
    }

    @Override
    public int getMaxSessionTimeout() {
        return peer.getMaxSessionTimeout();
    }

    @Override
    public int getInitLimit() {
        return peer.getInitLimit();
    }

    @Override
    public int getSyncLimit() {
        return peer.getSyncLimit();
    }

    public int getTick(){
        return peer.getTick();
    }

    public String getState(){
        return peer.getState().toString();
    }

    public String getQuorumAddress(){
        return peer.getQuorumAddress().toString();
    }

    public int getElectionType(){
        return peer.getElectionType();
    }

}
