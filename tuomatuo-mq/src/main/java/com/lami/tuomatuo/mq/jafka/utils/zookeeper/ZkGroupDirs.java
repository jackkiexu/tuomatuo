package com.lami.tuomatuo.mq.jafka.utils.zookeeper;

/**
 * Created by xjk on 2016/10/19.
 */
public class ZkGroupDirs {

    public String group;

    public String consumerDir;

    public String consumeGroupDir;

    public String consumerRegistryDir;

    public ZkGroupDirs(String group) {
        super();
        this.group = group;
        this.consumerDir = ZkUtils.ConsumersPath;
        this.consumeGroupDir = this.consumerDir + "/" + group;
        this.consumerRegistryDir = this.consumeGroupDir + "/ids";
    }
}
