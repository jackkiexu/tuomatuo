package com.lami.tuomatuo.mq.jafka.log;

import com.lami.tuomatuo.mq.jafka.api.PartitionChooser;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * Created by xjk on 2016/10/9.
 */
public class LogManager implements PartitionChooser, Closeable {

    private Map<String, Integer> topicPartitionsMap;

    public Map<String, Integer> getTopicPartitionsMap(){
        return topicPartitionsMap;
    }

    public void close() throws IOException {

    }

    public int choosePartition(String topic) {
        return 0;
    }
}
