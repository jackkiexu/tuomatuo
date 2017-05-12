package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import org.apache.zookeeper.proto.ReplyHeader;

/**
 * Manages the unknown request (i.e unknown OpCode), by:
 * - sending back the KeeperException.UnimplementedException() error code to the client
 * - closing the connection
 *
 * Created by xjk on 3/18/17.
 */
public class UnimplementedRequestProcessor implements RequestProcessor {

    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        KeeperException ke = new KeeperException.UnimplementedException();

        request.setException(ke);
        ReplyHeader rh = new ReplyHeader(request.cxid, request.zxid, ke.code().intValue());
        try{
            request.cnxn.sendResponse(rh, null, "response");
        }catch (Exception e){
            throw new RequestProcessorException("Can't send the response", e);
        }

        request.cnxn.sendCloseSession();
    }

    @Override
    public void shutdown() {

    }
}
