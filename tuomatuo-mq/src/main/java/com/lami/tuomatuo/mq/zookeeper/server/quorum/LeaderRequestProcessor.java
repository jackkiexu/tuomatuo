package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;

/**
 * Responsible for performing local session upgrade. Only request submitted
 * directly to the leader should go through this processor
 * Created by xujiankang on 2017/3/19.
 */
public class LeaderRequestProcessor implements RequestProcessor {
}
