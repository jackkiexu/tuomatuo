package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;

/**
 * A remote peer been only provides limited information about the remote peer,
 * and the peer cannot be managed remotely
 * Created by xujiankang on 2017/4/13.
 */
public class RemotePeerBean implements RemotePeerMXBean, ZKMBeanInfo {

    private QuorumPeer.QuorumServer peer;

    public RemotePeerBean(QuorumPeer.QuorumServer peer) {
        this.peer = peer;
    }

    public String getName() { return "replica." + peer.id; }

    public boolean isHidden() {
        return false;
    }

    public String getQuorumAddress(){
        return peer.addr.getHostName() + " : " + peer.addr.getPort();
    }
}
