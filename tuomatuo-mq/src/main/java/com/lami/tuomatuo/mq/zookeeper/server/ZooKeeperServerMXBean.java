package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * ZooKeeper serverMBean
 * Created by xujiankang on 2017/3/16.
 */
public interface ZooKeeperServerMXBean {

    /** The server socket port number */
    public String getClientPort();

    /**
     * the zookeeper server version
     * @return
     */
    public String getVersion();

    /**
     * the time the server was started
     * @return
     */
    public String getStartTime();

    /**
     * the minrequest latency in ms
     * @return
     */
    public long getMinRequestLatency();

    /**
     * The averge request latency in ms
     * @return
     */
    public long getAvgRequestLatency();

    /**
     * max request latency in ms
     * @return
     */
    public long getMaxRequestLatency();

    /**
     * number of packets received so far
     * @return
     */
    public long getPacketsReceived();

    /**
     * number of packets sent so far
     * @return
     */
    public long getPacketsSent();

    /**
     * number of outstanding request
     * @return
     */
    public long getOutstandingRequests();

    /**
     * Current TickTime of server in milliseconds
     * @return
     */
    public int getTickTime();

    /**
     * Set the TickTime of server in milliseconds
     * @param tickTime
     */
    public void setTickTime(int tickTime);

    /**
     * Current maxClientCnxns allowed from a particular host
     * @return
     */
    public int getMaxClientCnxnsPerHost();

    /**
     * Set maxClientCnxs allowed from a particular host
     * @param max
     */
    public void setMaxClientCnxnsPerHost(int max);

    /**
     * Current minSessionTimeout of server in milliseconds
     * @return
     */
    public int getMinSessionTimeout();

    /**
     * Set minSessionTimeout of server in milliseconds
     * @param min
     */
    public void setMinSessionTimeout(int min);

    /**
     * Current maxSessionTimeout of server in milliseconds
     * @return
     */
    public int getMaxSessionTimeout();

    /**
     * Set maxSessionTimeout of server in milliseconds
     * @param max
     */
    public void setMaxSessionTimeout(int max);

    /**
     * Reset packet and latency statistics
     */
    public void  resetStatistics();

    /**
     * Reset min/max latency statistics
     */
    public void resetLatency();

    /**
     * Reset max latency statistics only
     */
    public void resetMaxLatency();

    /**
     * number of alive client connections
     * @return
     */
    public long getNumAliveConnections();

    /**
     * estimated size of data directory in bytes
     * @return
     */
    public long getDataDirSize();

    /**
     * estimated size of log directory in bytes
     * @return
     */
    public long getLongDirSize();

    /**
     * secure client port
     * @return
     */
    public String getSecureClientPort();

    /**
     * secure client address
     * @return
     */
    public String getSecureClientAddress();


}
