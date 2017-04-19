package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServer;
import com.lami.tuomatuo.mq.zookeeper.server.ZooKeeperServerBean;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class LeaderBean extends ZooKeeperServerBean implements LeaderMXBean{

    private final Leader leader;

    public LeaderBean(Leader leader, ZooKeeperServer zks) {
        super(zks);
        this.leader = leader;
    }

    public String getName(){
        return "Leader";
    }

    public String getCurrentZxid(){
        return "0x" + Long.toHexString(zks.getZxid());
    }

    public String followerInfo(){
        StringBuilder sb = new StringBuilder();
        for(LearnerHandler handler : leader.getLearners()){
            sb.append(handler.toString()).append("\n");
        }
        return sb.toString();
    }
}
