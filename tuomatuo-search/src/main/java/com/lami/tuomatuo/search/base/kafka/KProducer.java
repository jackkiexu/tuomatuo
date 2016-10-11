package com.lami.tuomatuo.search.base.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * Created by xjk on 2016/4/19.
 */
public class KProducer {

    public static void main(String[] args) {
        String topic= "test3";
        long events = 3000;
        Random rand = new Random();

        Properties props = new Properties();
        props.put("metadata.broker.list", "192.168.1.28:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");

        ProducerConfig config = new ProducerConfig(props);

        Producer<String, String> producer = new Producer<String, String>(config);

        for (long nEvents = 0; nEvents < events; nEvents++) {
            String msg = "NativeMessage-" + rand.nextInt() ;
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, nEvents + "", msg);
            producer.send(data);
        }
        producer.close();
    }
}
