package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a very simple RequestProcessor that simply forwards a request from a
 * previous stage to the leader as an ACK
 *
 * Created by xujiankang on 2017/3/19.
 */
public class AckRequestProcessor implements RequestProcessor{

    private static final Logger LOG = LoggerFactory.getLogger(AckRequestProcessor.class);

    public Leader leader;

    public AckRequestProcessor(Leader leader) {
        this.leader = leader;
    }

    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        QuorumPeer self = leader.self;
        if(self != null){
            leader.processAck(self.getId(), request.zxid, null);
        }
        else{
            LOG.error("Null QuorumPeer");
        }
    }

    @Override
    public void shutdown() {

    }
}
