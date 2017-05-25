package com.lami.tuomatuo.mq.zookeeper.server;

import java.util.Date;

/**
 * Statistics on the ServerCnxn
 *
 * Created by xujiankang on 2017/3/16.
 */
public interface Stats {
    /**
     * Date time the connection was established
     * @return
     */
    Date getEstablished();

    /**
     * The number of requests that have been submitted but not yet
     * responsed to;
     */
    long getOutstandingRequests();

    /** Number of packets received */
    long getPacketsReceived();
    /** Number of packets sent (incl notifications) */
    long getPacketsSent();
    /** Min latency in ms */
    long getMinLatency();
    /** Average letency in ms */
    long getAvagLatency();
    /** Max latency in ms */
    long getMaxLatency();

    /** Last operation performed by this connection */
    String getLastOperation();

    /** Last cxid of this connection */
    long getLastCxid();

    /** Last zxid of this connection */
    long getLastZxid();

    /** Last time server sent a response to client on this connection */
    long getLastResponseTime();
    /** Latency of last response to licent on this connection in ms */
    long getLastLatency();

    /** Reset counters */
    void resetStats();
}
