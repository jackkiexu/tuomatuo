package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * Implementation of leader election using TCP. It uses an object of the class
 * QuorumCnxmanager to manage connections. Otherwise, the algorithm is push-based
 * as with the other UDP implementations
 *
 * There are a few parameters that can be tuned to change its behavior. First
 * finalizeWait determines the amount of time to wait until deciding upon a leader
 * This is part of the leader election algorithm
 *
 * Created by xujiankang on 2017/3/19.
 */
public class FastleaderElection implements Election {
    @Override
    public Vote lookForleader() throws InterruptedException {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
