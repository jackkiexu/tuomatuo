package com.lami.tuomatuo.mq.jafka.log;

import java.util.Map;

/**
 * Created by xjk on 2016/10/9.
 */
public class LogManager {

    private Map<String, Integer> topicPartitionsMap;

    public Map<String, Integer> getTopicPartitionsMap(){
        return topicPartitionsMap;
    }
}
