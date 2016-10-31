package com.lami.tuomatuo.mq.jafka.utils;

import java.util.Properties;

/**
 * Created by xjk on 2016/10/20.
 */
public class ZkConfig {

    protected Properties props;

    public ZkConfig(Properties props) {
        this.props = props;
    }

    /** ZK host string */
    public String getZkConnect(){
        return Utils.getString(props, "zk, connect", null);
    }

    /** zookeeper session timeout */
    public int getZkSessionTimeoutMs(){
        return Utils.getInt(props, "zk.sessiontime.ms", 6000);
    }

    /** the max time that the client waits to establish a connection to zookeeper */
    public int getZkConnectionTimeoutMs(){
        return Utils.getInt(props, "zk.connectiontimeout.ms", 6000);
    }

    /** how far a zk follower can be behind a ZK leader */
    public int getZkSyncTimeMs(){
        return Utils.getInt(props, "zk.synctime.ms", 2000);
    }

    protected int get(String name, int defaultValue){
        return Utils.getInt(props, name, defaultValue);
    }

    protected String get(String name, String defaultValue){
        return Utils.getString(props, name, defaultValue);
    }



}
