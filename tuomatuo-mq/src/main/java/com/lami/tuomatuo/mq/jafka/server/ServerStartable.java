package com.lami.tuomatuo.mq.jafka.server;

import com.lami.tuomatuo.mq.jafka.consumer.ConsumerConfig;
import com.lami.tuomatuo.mq.jafka.producer.ProducerConfig;
import org.apache.log4j.Logger;

/**
 * Created by xjk on 2016/10/31.
 */
public class ServerStartable {

    private final Logger logger = Logger.getLogger(ServerStartable.class);

    public Config config;

    public ConsumerConfig consumerConfig;

    public ProducerConfig producerConfig;

    private Server server;

    private EmbeddedConsumer embeddedConsumer;

    public ServerStartable(Config config, ConsumerConfig consumerConfig, ProducerConfig producerConfig) {
        this.config = config;
        this.consumerConfig = consumerConfig;
        this.producerConfig = producerConfig;
    }

    private void init(){
        server = new Server(config);
        if(consumerConfig != null){
            embeddedConsumer = new EmbeddedConsumer(consumerConfig, producerConfig, this);
        }
    }

    public void startup(){
    }

    public void shutdown(){

    }
}
