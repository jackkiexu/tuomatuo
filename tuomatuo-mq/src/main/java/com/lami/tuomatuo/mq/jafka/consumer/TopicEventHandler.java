package com.lami.tuomatuo.mq.jafka.consumer;

import java.util.List;

/**
 * Created by xjk on 2016/10/31.
 */
public interface TopicEventHandler<T> {

    void handleTopicEvent(List<T> allTopics);

}
