package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Flushable;
import java.io.IOException;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class SendAckRequestProcessor implements RequestProcessor, Flushable {

    private static final Logger LOG = LoggerFactory.getLogger(SendAckRequestProcessor.class);

    public Learner learner;



    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        if(request.type != ZooDefs.OpCode.sync){
            QuorumPacket qp = new QuorumPacket(Leader.ACK, request.hdr.getZxid(), null, null);

            try{
                learner.writePacket(qp, false);
            }catch (Exception e){
                LOG.info("Closing connection to leader, exception during packet send", e);
                try{
                    if(!learner.sock.isClosed()){
                        learner.sock.close();
                    }
                }catch (Exception e1){
                    LOG.debug("Ignoring error closing the connection", e1);
                }
            }

        }
    }

    @Override
    public void flush() throws IOException {
        try{
            learner.writePacket(null, true);
        }catch (Exception e){
            try{
                if(!learner.sock.isClosed()){
                    learner.sock.close();
                }
            }catch (Exception e1){
                LOG.debug("Ignoring error closing the connection", e1);
            }
        }
    }

    @Override
    public void shutdown() {

    }
}
