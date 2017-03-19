package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * Observers are peers that do not take part in the atomic broadcast protocal
 * Instead, they are informed of successful proposals by the Leader. Observers
 * therefore naturally act as a rely point for publishing the proposal stream
 * and can relieve Followers of some of the connection load. Observers may
 * submit proposals, but do not vote their acceptance
 *
 * Created by xujiankang on 2017/3/19.
 */
public class Observer extends Learner {
}
