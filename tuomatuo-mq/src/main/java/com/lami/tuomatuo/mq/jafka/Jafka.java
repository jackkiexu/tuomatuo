package com.lami.tuomatuo.mq.jafka;

import com.lami.tuomatuo.mq.jafka.consumer.ConsumerConfig;
import com.lami.tuomatuo.mq.jafka.producer.ProducerConfig;
import com.lami.tuomatuo.mq.jafka.server.Config;
import com.lami.tuomatuo.mq.jafka.server.ServerStartable;
import com.lami.tuomatuo.mq.jafka.utils.Utils;

import java.util.Properties;

/**
 * Created by xjk on 11/6/16.
 */
public class Jafka {

    public void start(String mainFile, String consumerFile, String producerFile){
        start(Utils.loadProps(mainFile),
                consumerFile == null ? null : Utils.loadProps(consumerFile),
                producerFile == null ? null : Utils.loadProps(producerFile));
    }

    public void start(Properties mainProperties, Properties consumerProperties, Properties producerProperties){
        Config config = new Config(mainProperties);
        ConsumerConfig consumerConfig = consumerProperties == null ? null : new ConsumerConfig(consumerProperties);
        ProducerConfig producerConfig = consumerConfig == null ? null : new ProducerConfig(producerProperties);
        start(config, consumerConfig, producerConfig);
    }

    public void start(Config config, ConsumerConfig consumerConfig, ProducerConfig producerConfig){
        final ServerStartable serverStartable;
        if(consumerConfig == null){
            serverStartable = new ServerStartable(config);
        }else{
            serverStartable = new ServerStartable(config, consumerConfig, producerConfig);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(){

            public void run() {
                serverStartable.shutdown();
                serverStartable.awaitShutdown();
            }
        });

        serverStartable.startup();
        serverStartable.awaitShutdown();
    }

    public static void main(String[] args) {
        int argsSize = args.length;
        if(argsSize != 1 && argsSize != 3){
            System.out.println("USAGE: java [options] Jafka server.properties [consumer.properties producer.properties]");
            System.exit(1);
        }

        Jafka jafka = new Jafka();
        jafka.start(args[0], argsSize > 1 ? args[1] : null, argsSize > 1 ? args[2] : null);
    }

}
