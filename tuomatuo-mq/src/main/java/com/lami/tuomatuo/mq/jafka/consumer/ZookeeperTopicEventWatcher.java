package com.lami.tuomatuo.mq.jafka.consumer;


import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkStateListener;
import com.github.zkclient.ZkClient;
import com.lami.tuomatuo.mq.jafka.common.ConsumerRebalanceFailedException;
import com.lami.tuomatuo.mq.jafka.server.ServerStartable;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkStringSerializer;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.Watcher;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Created by xjk on 2016/10/31.
 */
public class ZookeeperTopicEventWatcher implements Closeable {

    private TopicEventHandler<String> eventHandler;

    private ServerStartable serverStartable;

    private final Object lock = new Object();

    private ZkClient zkClient;

    private static final Logger logger = Logger.getLogger(ZookeeperTopicEventWatcher.class);


    public ZookeeperTopicEventWatcher(ConsumerConfig consumerConfig, TopicEventHandler<String> eventHandler, ServerStartable serverStartable) {
        super();
        this.eventHandler = eventHandler;
        this.serverStartable = serverStartable;

        this.zkClient = new ZkClient(consumerConfig.getZkConnect(), consumerConfig.getZkSessionTimeoutMs(), consumerConfig.getZkConnectionTimeoutMs(), ZkStringSerializer.getInstance());
        startWatchingTopicEvents();
    }

    private void startWatchingTopicEvents(){
        ZkTopicEventListener topicEventListener = new ZkTopicEventListener();
        ZkUtils.makeSurePersistentPathExists(zkClient, ZkUtils.BrokerTopicsPath);
        List<String> topics = zkClient.subscribeChildChanges(ZkUtils.BrokerTopicsPath, topicEventListener);

        // cal to bootstrap topic list
        try {
            topicEventListener.handleChildChange(ZkUtils.BrokerTopicsPath, topics);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopWatchingTopicEvents(){
        this.zkClient.unsubscribeAll();
    }

    public void close() throws IOException {
        synchronized (lock){
            if(zkClient == null){
                logger.info("cannot shutdown already shutdown topic event watcher ");
                return;
            }
            stopWatchingTopicEvents();
            zkClient.close();
            zkClient = null;
        }
    }


    class ZkTopicEventListener implements IZkChildListener{

        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            synchronized (lock){
                if(zkClient == null) return;
                try {
                    List<String> lastestTopics = zkClient.getChildren(ZkUtils.BrokerTopicsPath);
                    logger.info("all Topic : " + lastestTopics);
                    eventHandler.handleTopicEvent(lastestTopics);
                } catch (ConsumerRebalanceFailedException e) {
                    e.printStackTrace();
                    logger.info("can't rebalance in embedded consumer); proceed to shutdown", e);
                    serverStartable.shutdown();
                } catch (Exception e){
                    logger.info("error in handling child changes in embedded consumer", e);
                }
            }
        }
    }


    class ZkSessionExpireListener implements IZkStateListener{

        private ZkTopicEventListener zkTopicEventListener;

        public ZkSessionExpireListener(ZkTopicEventListener zkTopicEventListener) {
            this.zkTopicEventListener = zkTopicEventListener;
        }

        public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
            synchronized (lock){
                if(zkClient != null){
                    zkClient.subscribeChildChanges(ZkUtils.BrokerTopicsPath, zkTopicEventListener);
                }
            }
        }

        public void handleNewSession() throws Exception {

        }
    }
}
