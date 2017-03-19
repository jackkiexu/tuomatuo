package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * just like the standard ZooKeeperServer. We just replace the request
 * processors: FollowerRequestProcessor -> CommitProcessor ->
 * FinalRequestProcessor
 *
 * Created by xujiankang on 2017/3/19.
 */
public class FollowerZooKeeperServer extends LearnerZooKeeperServer {
}
