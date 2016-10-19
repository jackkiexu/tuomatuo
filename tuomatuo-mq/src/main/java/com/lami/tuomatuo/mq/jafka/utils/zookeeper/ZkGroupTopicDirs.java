package com.lami.tuomatuo.mq.jafka.utils.zookeeper;

/**
 * Created by xjk on 2016/10/19.
 */
public class ZkGroupTopicDirs extends ZkGroupDirs {

    public String topic;

    public String consumerOffsetDir;

    public String consumerOwnerDir;

    public ZkGroupTopicDirs(String group, String topic) {
        super(group);
        this.topic = topic;
        this.consumerOffsetDir = consumeGroupDir + "/offsets/" + topic;
        this.consumerOwnerDir = consumeGroupDir + "/owners/" + topic;
    }
}
