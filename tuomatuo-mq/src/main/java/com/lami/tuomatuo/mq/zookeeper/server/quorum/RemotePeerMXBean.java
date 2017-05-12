package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * A proxy for a remote quorum peer
 * Created by xujiankang on 2017/3/19.
 */
public interface RemotePeerMXBean {

    // name of the peer
    public String getName();

    /**
     * IP address of the quorum peer
     * @return
     */
    public String getQuorumAddress();



}
