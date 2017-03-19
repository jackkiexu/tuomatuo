package com.lami.tuomatuo.mq.zookeeper.server;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration data for a {@link ZooKeeperServer}. This class is immutable
 *
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperServerConf {

    /**
     * The key in the map returned by toMap for the client port
     */
    public static final String KEY_CLIENT_PORT = "client_port";

    /**
     * The key in the map returned by toMap for the data directory
     */
    public static final String KEY_DATA_DIR = "data_dir";

    /**
     * The key in the map returned by toMap for the data log
     * directory
     */
    public static final String KEY_DATA_LOG_DIR = "data_log_dir";

    /**
     * The key in the map returned by toMap for the tick time
     */
    public static final String KEY_TICK_TIME = "tick_time";

    /**
     * The key in the map returned by toMap() for the maximum
     * client connections per host
     */
    public static final String KEY_MAX_CLIENT_CNXNS = "max_client_cnxns";

    /**
     * The key in the map returned by toMap for the minimum session
     * timeout
     */
    public static final String KEY_MIN_SESSION_TIMEOUT = "min_session_timeout";

    /**
     * The key in the map returned by toMap for the maximum session
     * timeout
     */
    public static final String KEY_MAX_SESSION_TIMEOUT = "max_session_timeout";

    /**
     * The key in the map returned by {@link #toMap()} for the server ID
     */
    public static final String KEY_SERVER_ID = "server_id";

    private final int clientPort;
    private final String dataDir;
    private final String dataLogDir;
    private final int tickTime;
    private final int maxClientCnxnsPerHost;
    private final int minSessionTimeout;
    private final int maxSessionTimeout;
    private final long serverId;

    /**
     * Creates a new configuration
     *
     * @param clientPort client port
     * @param dataDir absolute path to data directory
     * @param dataLogDir absolute path to data log directory
     * @param tickTime tick time
     * @param maxClientCnxnsPerHost maximum number of client connections
     * @param minSessionTimeout minimum session timemout
     * @param maxSessionTimeout maximum session timeout
     * @param serverId server Id
     */
    public ZooKeeperServerConf(int clientPort, String dataDir, String dataLogDir,
                               int tickTime, int maxClientCnxnsPerHost,
                               int minSessionTimeout, int maxSessionTimeout,
                               long serverId) {
        this.clientPort = clientPort;
        this.dataDir = dataDir;
        this.dataLogDir = dataLogDir;
        this.tickTime = tickTime;
        this.maxClientCnxnsPerHost = maxClientCnxnsPerHost;
        this.minSessionTimeout = minSessionTimeout;
        this.maxSessionTimeout = maxSessionTimeout;
        this.serverId = serverId;
    }


    /**
     * Gets the client port
     * @return
     */
    public int getClientPort() {
        return clientPort;
    }

    /**
     * Gets the data directory
     * @return
     */
    public String getDataDir() {
        return dataDir;
    }

    /**
     * Gets the data log directory
     * @return
     */
    public String getDataLogDir() {
        return dataLogDir;
    }

    /**
     * Gets the tick time
     * @return
     */
    public int getTickTime() {
        return tickTime;
    }

    /**
     * Get the maximum client connections per host
     * @return
     */
    public int getMaxClientCnxnsPerHost() {
        return maxClientCnxnsPerHost;
    }

    /**
     * Gets the minimum session timeout
     * @return
     */
    public int getMinSessionTimeout() {
        return minSessionTimeout;
    }

    /**
     * Gets the maximum session timeout
     * @return
     */
    public int getMaxSessionTimeout() {
        return maxSessionTimeout;
    }

    /**
     * Gets the server ID
     * @return
     */
    public long getServerId() {
        return serverId;
    }

    /**
     * Converts this configuration to a map. The returned map is mutable, and
     * changes to it do not reflect back into this configuration
     * @return
     */
    public Map<String, Object> toMap(){
        Map<String, Object> conf = new LinkedHashMap<>();
        conf.put(KEY_CLIENT_PORT, clientPort);
        conf.put(KEY_DATA_DIR, dataDir);
        conf.put(KEY_DATA_LOG_DIR, dataLogDir);
        conf.put(KEY_TICK_TIME, tickTime);
        conf.put(KEY_MAX_CLIENT_CNXNS, maxClientCnxnsPerHost);
        conf.put(KEY_MIN_SESSION_TIMEOUT, minSessionTimeout);
        conf.put(KEY_MAX_SESSION_TIMEOUT, maxSessionTimeout);
        conf.put(KEY_SERVER_ID, serverId);
        return conf;
    }
}