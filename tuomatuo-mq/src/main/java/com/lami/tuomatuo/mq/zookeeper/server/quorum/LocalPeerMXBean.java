package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * A local zookeeper server MBean interface. Unlike the remote peer, the local
 * peer provides complete state/statistics at runtime and can be managed (just
 * like a standalone zookeeper server)
 * Created by xujiankang on 2017/3/19.
 */
public interface LocalPeerMXBean extends ServerMXBean{

    /**
     * the number of milliseconds of each tick
     * @return
     */
    public int getTickTime();

    /**
     * Current maxClientCnxn allowed from a particular host
     * @return
     */
    public int getMaxClientCnxnPerHost();

    /**
     * the minumum number of milliseconds allowed for a second timeout
     * @return
     */
    public int getMinSessionTimeout();


    /**
     * the maxiumum number of milliseconds allowed for a second timeout
     * @return
     */
    public int getMaxSessionTimeout();

    /**
     * the number of ticks that the initial sync phase can take
     * @return
     */
    public int getInitLimit();


    /**
     * the number of ticks that can pass between sending a request
     * and getting a acknowledgment
     * @return
     */
    public int getSyncLimit();

    /**
     * the current tick
     * @return
     */
    public int getTick();

    /**
     * the current server state
     * @return
     */
    public String getState();

    /**
     * the quorum address
     * @return
     */
    public String getQuorumAddress();

    /**
     * the election type
     * @return
     */
    public int getElectionType();




}
