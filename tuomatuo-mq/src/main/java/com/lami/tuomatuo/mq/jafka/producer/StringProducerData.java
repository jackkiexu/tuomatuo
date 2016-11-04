package com.lami.tuomatuo.mq.jafka.producer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/11/4.
 */
public class StringProducerData extends ProducerData<String, String> {


    public StringProducerData(String topic, String key, List<String> data) {
        super(topic, key, data);
    }

    public StringProducerData(String topic, List<String> data) {
        super(topic, data);
    }

    public StringProducerData(String topic, String data) {
        super(topic, data);
    }

    public StringProducerData(String topic) {
        super(topic, new ArrayList<String>());
    }

    public StringProducerData add(String message){
        getData().add(message);
        return this;
    }

}
