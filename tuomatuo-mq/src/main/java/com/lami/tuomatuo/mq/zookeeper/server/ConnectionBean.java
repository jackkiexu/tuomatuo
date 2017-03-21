package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ObjectName;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Implementation of connection MBean interface
 *
 * Created by xujiankang on 2017/3/19.
 */
public class ConnectionBean implements ConnectionMXBean, ZKMBeanInfo {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionBean.class);

    private final ServerCnxn connection;
    private final Stats stats;

    private final ZooKeeperServer zk;

    private final String remoteIP;
    private final long sessionId;

    public ConnectionBean(ServerCnxn connection, ZooKeeperServer zk) {
        this.connection = connection;
        this.stats = connection;
        this.zk = zk;

        InetSocketAddress sockAddr = connection.getRemoteSocketAddress();
        if(sockAddr == null){
            remoteIP = "Unknown";
        }else{
            InetAddress addr = sockAddr.getAddress();
            if(addr instanceof Inet6Address){
                remoteIP = ObjectName.quote(addr.getHostAddress());
            }else{
                remoteIP = addr.getHostAddress();
            }

        }

        sessionId = connection.getSessionId();
    }


    public String getSessionId(){
        return "0x" + Long.toHexString(sessionId);
    }

    public String getSourceIP(){
        InetSocketAddress sockAddr = connection.getRemoteSocketAddress();
        if(sockAddr == null) return null;
        return sockAddr.getAddress().getHostAddress() + " : " + sockAddr.getPort();
    }

    @Override
    public String getName() {
        return MBeanRegistry.getInstance().makeFullPath("Connections", remoteIP, getSessionId());
    }

    @Override
    public boolean isHidden() {
        return false;
    }


    @Override
    public String[] getEphemeralNodes() {
        return new String[0];
    }

    @Override
    public String getStartedTime() {
        return null;
    }


    @Override
    public long getPacketsReceived() {
        return 0;
    }

    @Override
    public long getPacketsSent() {
        return 0;
    }

    @Override
    public long getOutstandingRequests() {
        return 0;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void terminateSession() {

    }

    @Override
    public void terminateConnection() {

    }

    @Override
    public long getMinLatency() {
        return 0;
    }

    @Override
    public long getAvgLatency() {
        return 0;
    }

    @Override
    public long getMaxLatency() {
        return 0;
    }

    @Override
    public String getLastOperation() {
        return null;
    }

    @Override
    public String getLastCxid() {
        return null;
    }

    @Override
    public String getLastZxid() {
        return null;
    }

    @Override
    public String getLastResponseTime() {
        return null;
    }

    @Override
    public long getLastLatency() {
        return 0;
    }

    @Override
    public void resetCounters() {

    }


}
