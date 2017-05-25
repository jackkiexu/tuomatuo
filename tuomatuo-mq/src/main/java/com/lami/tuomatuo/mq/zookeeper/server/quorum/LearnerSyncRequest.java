package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.Request;
import com.lami.tuomatuo.mq.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.data.Id;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class LearnerSyncRequest extends Request {

    public LearnerHandler fh;

    public LearnerSyncRequest(LearnerHandler fh, long sessionId, int xid, int type, ByteBuffer bb, List<Id> authInfo) {
        super(null, sessionId, xid, type, bb, authInfo);
        this.fh = fh;
    }
}
