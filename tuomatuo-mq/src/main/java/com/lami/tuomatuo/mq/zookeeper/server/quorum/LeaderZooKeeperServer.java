package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * Just like the standard ZooKeeperServer. We just replace the request
 * processors: PrepRqequestProcessor -> ProposalRequestProcessor ->
 * CommmitProcessor -> leader. ToBeAppliedrequestProcessor
 * FinalRequestProcessor
 *
 * Created by xujiankang on 2017/3/19.
 */
public class LeaderZooKeeperServer extends QuorumZooKeeperServer {
}
