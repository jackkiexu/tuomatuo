package com.lami.tuomatuo.mq.jafka.consumer;


import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by xjk on 2016/10/19.
 */
public class TopicCount {

    private String consumerIdString;

    private Map<String, Integer> topicCountMap;

    private static final ObjectMapper mapper = new ObjectMapper();

    public TopicCount(String consumerIdString, Map<String, Integer> topicCountMap) {
        this.consumerIdString = consumerIdString;
        this.topicCountMap = topicCountMap;
    }

    public Map<String, Set<String>> getConsumerThreadIdsPerTopic(){
        Map<String, Set<String>> consumerThreadIdsPerTopicMap = new HashMap<String, Set<String>>();
        for(Map.Entry<String, Integer> e : topicCountMap.entrySet()){
            Set<String> consumerSet = new HashSet<String>();
            final int nConsumers = e.getValue().intValue();
            for(int i = 0; i < nConsumers; i++){
                consumerSet.add(consumerIdString + "-" + i);
            }
            consumerThreadIdsPerTopicMap.put(e.getKey(), consumerSet);
        }
        return consumerThreadIdsPerTopicMap;
    }

    public static TopicCount parse(String consumerIdString, String jsonString){
        try {
            Map<String, Integer> topicCountMap = mapper.readValue(jsonString, new TypeReference<Map<String, Integer>>() {});
            return new TopicCount(consumerIdString, topicCountMap);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("error parse consumer json string " + jsonString);
        }
    }

    public String toJsonString(){
        try {
            return mapper.writeValueAsString(topicCountMap);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
