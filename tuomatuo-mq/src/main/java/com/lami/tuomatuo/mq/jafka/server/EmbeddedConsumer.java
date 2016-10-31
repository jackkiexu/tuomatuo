package com.lami.tuomatuo.mq.jafka.server;

import com.lami.tuomatuo.mq.jafka.consumer.ConsumerConfig;
import com.lami.tuomatuo.mq.jafka.consumer.ConsumerConnector;
import com.lami.tuomatuo.mq.jafka.consumer.TopicEventHandler;
import com.lami.tuomatuo.mq.jafka.consumer.ZookeeperTopicEventWatcher;
import com.lami.tuomatuo.mq.jafka.message.Message;
import com.lami.tuomatuo.mq.jafka.producer.Producer;
import com.lami.tuomatuo.mq.jafka.producer.ProducerConfig;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xjk on 2016/10/31.
 */
public class EmbeddedConsumer implements TopicEventHandler<String>{

    private static final Logger logger = Logger.getLogger(EmbeddedConsumer.class);

    private ConsumerConfig consumerConfig;

    private ServerStartable serverStartable;

    private List<String> whiteListTopics;

    private List<String> blackListTopics;

    private Producer<Void, Message> producer;

    private ZookeeperTopicEventWatcher topicEventWatcher;

    private ConsumerConnector consumerConnector;

    private List<MirroringThread> threadList = new ArrayList<MirroringThread>();

    private List<String> mirrorTopics = new ArrayList<String>();


    public EmbeddedConsumer(ConsumerConfig consumerConfig,ProducerConfig producerConfig, ServerStartable serverStartable) {
        this.consumerConfig = consumerConfig;
        this.serverStartable = serverStartable;

        this.whiteListTopics = Arrays.asList(consumerConfig.getMirrorTopicsWhiteList().split(","));
        this.blackListTopics = Arrays.asList(consumerConfig.getMirrorTopicsBlackList().split(","));
        this.producer = new Producer<Void, Message>(producerConfig);
    }

    public void startup(){
        logger.info("starting up embedded consumer");
        topicEventWatcher = new ZookeeperTopicEventWatcher(consumerConfig, this, serverStartable);
    }

    public void handleTopicEvent(List<String> allTopics) {

    }
}
