package com.lami.tuomatuo.mq.zookeeper.server;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * Server configuration storage
 *
 * We use this instead of Properties as it's typed
 *
 *
 * Created by xujiankang on 2017/3/19.
 */
public class ServerConfig {

    /**
     * if you update the configuration parameters be sure
     * to update the "conf" 4 letter word
     */
    protected InetSocketAddress clientPortAddress;
    protected InetSocketAddress secureClientPortAddress;
    protected File dataDir;
    protected int tickTime = ZooKeeperServer.DEFAULT_TICK_TIME;
    protected int maxClientCnxns;
    /** default to -1 if not set explicitly */
    protected int minSessionTimeout = -1;
    /** default to -1 if not set explicitly */
    protected int maxSessionTimeout = -1;




    public void readFrom(){

    }

    public InetSocketAddress getClientPortAddress() {
        return clientPortAddress;
    }

    public InetSocketAddress getSecureClientPortAddress() {
        return secureClientPortAddress;
    }

    public File getDataDir() {
        return dataDir;
    }

    public int getTickTime() {
        return tickTime;
    }

    public int getMaxClientCnxns() {
        return maxClientCnxns;
    }
    /** minimum session timeout in milliseconds -1 if unset */
    public int getMinSessionTimeout() {
        return minSessionTimeout;
    }
    /** maximum session timeout in milliseconds -1 if unset */
    public int getMaxSessionTimeout() {
        return maxSessionTimeout;
    }
}
