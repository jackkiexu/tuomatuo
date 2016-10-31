package com.lami.tuomatuo.mq.jafka.server;

import com.github.zkclient.IZkStateListener;
import com.github.zkclient.ZkClient;
import com.lami.tuomatuo.mq.jafka.cluster.Broker;
import com.lami.tuomatuo.mq.jafka.log.LogManager;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkStringSerializer;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 10/31/16.
 */
public class Zookeeper implements IZkStateListener, Closeable {

    private static final Logger logger = Logger.getLogger(Zookeeper.class);

    private Config config;

    private LogManager logManager;

    private String brokerIdPath;

    private ZkClient zkClient;

    private List<String> topics = new ArrayList<String>();

    private final Object lock = new Object();


    public Zookeeper(Config config, LogManager logManager) {
        this.config = config;
        this.logManager = logManager;

        this.brokerIdPath = ZkUtils.BrokerIdsPath + "/" + config.getBrokerId();
    }

    public void startup(){
        logger.info("connecting to zookeeper : " + config.getZkConnect());
        zkClient = new ZkClient(config.getZkConnect(), config.getZkSessionTimeoutMs(), config.getZkConnectionTimeoutMs(), ZkStringSerializer.getInstance());
        zkClient.subscribeStateChanges(this);
    }

    public void registerTopicInZk(String topic){
        registerTopicInZk(topic);
        synchronized (lock){
            topics.add(topic);
        }
    }

    private void registerTopicInZkInternal(String topic){
        String brokerTopicPath = ZkUtils.BrokerTopicsPath + "/" + topic + "/" + config.getBrokerId();
        Integer numParts = logManager.getTopicPartitionsMap().get(topic);
        if(numParts == null){
            numParts = Integer.valueOf(config.getNumPartitions());
        }
        logger.info("Begin registering broker topic " + brokerTopicPath + " with " + numParts + " partitions");
        ZkUtils.createEphemeralPathExpectConflict(zkClient, brokerTopicPath, "" + numParts);
        logger.info("End registering broker topic " + brokerTopicPath);
    }

    public void registerBrokerInZk(){
        logger.info("Registering broker " + brokerIdPath);
        String hostname = config.getHostName();
        if(hostname == null){
            try {
                hostname = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new RuntimeException("can not get local host, setting 'hostname' in configuration");
            }
        }

        String creatorId = hostname + "-" + System.currentTimeMillis();
        Broker broker = new Broker(config.getBrokerId(), creatorId, hostname, config.getPort());
        try {
            ZkUtils.createEphemeralPathExpectConflict(zkClient, brokerIdPath, broker.getZKString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("A broker is already registered on the path " + brokerIdPath + ". This probably"
                    + "indicates that you either have configured a brokerid that is already in use, or"
                    + "else you have shutdown this broker and resterted it faster than the zookeeper " + " timeout so it appears to be re-registering"
            );
        }
        logger.info("Registering broker " + brokerIdPath + " successed with " + broker);
    }

    public void close() throws IOException {
        if(zkClient != null){
            logger.info("closing zookeeper client ..");
            zkClient.close();
        }
    }

    public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
        // do nothing, since zkClient will do reconnect for us
    }

    public void handleNewSession() throws Exception {
        logger.info("re-registering broker info in zookeeper for broker " + config.getBrokerId());
        registerBrokerInZk();
        synchronized (lock){
            logger.info("re-gistering broker topics in zookeeper for broker " + config.getBrokerId());
            for(String topic : topics){
                registerTopicInZkInternal(topic);
            }
        }
    }
}
