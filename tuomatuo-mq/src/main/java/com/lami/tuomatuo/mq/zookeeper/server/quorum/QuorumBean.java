package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class QuorumBean implements QuorumMXBean, ZKMBeanInfo {

    private final QuorumPeer peer;
    private final String name;

    public QuorumBean(QuorumPeer peer) {
        this.peer = peer;
        name = "ReplicatedServer_id " + peer.getId();
    }

    public String getname() {
        return name;
    }

    public boolean isHidden(){
        return false;
    }

    public int getQuorumSize(){
        return peer.getQuorumSize();
    }



    @Override
    public String getName() {
        return null;
    }

}
