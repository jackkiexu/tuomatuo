package com.lami.tuomatuo.mq.zookeeper.client;

import com.lami.tuomatuo.mq.zookeeper.ZooKeeper;
import com.lami.tuomatuo.mq.zookeeper.common.ZKConfig;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.File;

/**
 * Handless client specific propose
 * Created by xujiankang on 2017/3/29.
 */
public class ZKClientConfig extends ZKConfig {

    public static final String ZK_SASL_CLIENT_USERNAME = "zookeeper.sasl.client.username";
    public static final String ZK_SASL_CLIENT_USERNAME_DeFAULT = "zookeeper";


    public static final String LOGIN_CONTEXT_NAME_KEY = ZooKeeperSaslClient.LOGIN_CONTEXT_NAME_KEY;
    public static final String LOGIN_CONTEXT_NAME_KEY_DEFAULT = "Client";

    public static final String ENABLE_CLIENT_SASL_KEY = ZooKeeperSaslClient.ENABLE_CLIENT_SASL_KEY;

    public static final String ENABLE_CLIENT_SASL_DEFAULT = ZooKeeperSaslClient.ENABLE_CLIENT_SASL_DEFAULT;
    public static final String ZOOKEEPER_SERVER_REALM = "zookeeper.server.realm";


    /**
     * This control whether automatic watch reseting is enabled. Clients
     * automatically reset watches during reconnect, this option allows
     * the client to turn off this behavior by setting the property
     * "zookeeper.disableAutoWatchReset" to "true"
     */
    public static final String DISABLE_AUTO_WATCH_RESET = "zookeeper.disableAutoWatchReset";

    public static final String ZOOKEEPER_CLIENT_CNXN_SOCKET = ZooKeeper.ZOOKEEPER_CLIENT_CNXN_SOCKET;

    public static final String SECURE_CLIENT = ZooKeeper.SECURE_CLIENT;
    public static final int CLIENT_MAX_PACKET_LENGTH_DEFAULT = 4096 * 1024; /* 4MB */


    public ZKClientConfig() {
        super();
    }

    public ZKClientConfig(File configFile)throws QuorumPeerConfig.ConfigException{
        super(configFile);
    }


    public ZKClientConfig(String configPath) throws QuorumPeerConfig.ConfigException{
        super(configPath);
    }

    @Override
    protected void handleBackwardCompatibility() {
        /**
         * backward compatibility for properties which are common to both client
         * and server
         */
        super.handleBackwardCompatibility();

        /**
         * backward compatibility for client specific properties
         */
        setProperty(ZK_SASL_CLIENT_USERNAME, System.getProperty(ZK_SASL_CLIENT_USERNAME));
        setProperty(LOGIN_CONTEXT_NAME_KEY, System.getProperty(LOGIN_CONTEXT_NAME_KEY));
        setProperty(ENABLE_CLIENT_SASL_KEY, System.getProperty(ENABLE_CLIENT_SASL_KEY));
        setProperty(ZOOKEEPER_SERVER_REALM, System.getProperty(ZOOKEEPER_SERVER_REALM));
        setProperty(DISABLE_AUTO_WATCH_RESET, System.getProperty(DISABLE_AUTO_WATCH_RESET));
        setProperty(ZOOKEEPER_CLIENT_CNXN_SOCKET, System.getProperty(ZOOKEEPER_CLIENT_CNXN_SOCKET));
        setProperty(SECURE_CLIENT, System.getProperty(SECURE_CLIENT));
    }

    /**
     * Return true if the SASL client is enabled. By default. the client is
     * enabled but can be disabled by setting the system property
     * zookeeper.sasl.client to false
     * @return
     */
    public boolean isSaslClientEnabled(){
        return Boolean.valueOf(getProperty(ENABLE_CLIENT_SASL_KEY, ENABLE_CLIENT_SASL_DEFAULT));
    }
}
