package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;

/**
 * This is a very simple RequestProcessor that simply forwards a request from a
 * previous stage to the leader as an ACK
 *
 * Created by xujiankang on 2017/3/19.
 */
public class AckRequestProcessor implements RequestProcessor{
}
