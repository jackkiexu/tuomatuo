package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * This MBean represents a client connection
 * Created by xjk on 3/18/17.
 */
public interface ConnectionMXBean {

    /**
     * source (client) IP Address
     * @return
     */
    public String getSourceIP();

    /**
     * client's session Id
     * @return
     */
    public String getSessionId();

    /**
     * time the connection was started
     * @return
     */
    public String getStartedTime();

    /**
     * number of ephemeral nodes owned by this connection
     * @return
     */
    public String[] getEphemeralNodes();

    /**
     * number of packets received to this client
     * @return
     */
    public long getPacketsReceived();

    /**
     * number of packets sent to this client
     * @return
     */
    public long getPacketsSent();

    /**
     * number of requests being processed
     * @return
     */
    public long getOutstandingRequests();

    /**
     * session timeout in ms
     * @return
     */
    public int getSessionTimeout();

    /**
     * Terminate this client session. The client will reconnect with a different
     * session id
     */
    public void terminateSession();

    /**
     * Terminate the client connection. The client will immediately attempt to
     * reconnect with the same session id
     */
    public void terminateConnection();

    /** Min latency in ms */
    long getMinLatency();

    /** Average latency in ms */
    long getAvgLatency();

    /** Max latency in ms */
    long getMaxLatency();

    /** Last operation performed by this connection */
    String getLastOperation();

    /** Last cxid of this connection */
    String getLastCxid();

    /** Last zxid of this connection */
    String getLastZxid();

    /** Last time server sent a response to client on this connection */
    String getLastResponseTime();

    /** Latency of last response of client on this connection in ms */
    long getLastLatency();

    /** Reset counters */
    void resetCounters();
}
