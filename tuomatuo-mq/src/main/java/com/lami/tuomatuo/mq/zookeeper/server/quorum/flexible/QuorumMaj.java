package com.lami.tuomatuo.mq.zookeeper.server.quorum.flexible;

import org.apache.zookeeper.server.quorum.flexible.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class QuorumMaj implements QuorumVerifier {
    private static final Logger LOG = LoggerFactory.getLogger(QuorumMaj.class);

    int half;

    /**
     * Defines a majority to avoid computing it every time.
     *
     * @param n number of servers
     */
    public QuorumMaj(int n){
        this.half = n/2;
    }

    /**
     * Returns weight of 1 by default.
     *
     * @param id
     */
    public long getWeight(long id){
        return (long) 1;
    }

    /**
     * Verifies if a set is a majority.
     */
    // 验证 集群中是否过半的的集合在 set 里面
    public boolean containsQuorum(HashSet<Long> set){
        return (set.size() > half);
    }
}
