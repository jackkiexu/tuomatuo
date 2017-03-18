package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * Created by xjk on 3/17/17.
 */
public class QuorumPeerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(QuorumPeerConfig.class);
    private static final int UNSET_SERVERID = -1;

    public static final String nextDynamicConfigFileSuffix = ".dynamic.next";

    private static boolean standaloneEnabled = true;
    private static boolean reconfigEnabled = false;

    protected InetSocketAddress clientPortAddress;
    protected InetSocketAddress secureClientPortAddress;
    protected File dataDir;
    protected File dataLogDir;
    protected String dynamicConfigFileStr = null;
    protected String configFileStr = null;
    protected int tickTime = ZooKeeperServer.DEFAULT_TICK_TIME;
    protected int maxClientCnxns = 60;

    /** defaults to -1 if not set explicitly */
    protected int minSessionTimeout = -1;
    /** defaults to -1 if not set explicitly */
    protected int maxSessionTimeout = -1;
    protected boolean localSessionsEnabled = false;
    protected boolean localSessionsUpgradingEnabled = false;

    protected int initLimit;
    protected int syncLimit;
    protected int electionAlg = 3;
    protected int electionPort = 2182;
    protected boolean quorumListeneOnAllIPs = false;

    protected long serverId = UNSET_SERVERID;

    protected QuorumVerifier quorumVerifier = null, lastSeenQuorumVerifier = null;
    protected int snapRetainCount = 3;
    protected int purgeInterval = 0;
    protected boolean syncEnabled = true;

    protected QuorumPeer.LearnerType peerType = QuorumPeer.LearnerType.PARTICIPANT;

    /**
     * Minimum snapshot retain count
     * @see org.apache.zookeeper.server.PurgeTxnLog#purge(File, File, int)
     */
    private final int MIN_SNAP_RETAIN_COUNT = 3;

    public static class ConfigException extends Exception{
        public ConfigException(String message) {
            super(message);
        }

        public ConfigException(String message, Throwable cause) {
            super(message, cause);
        }
    }





}
