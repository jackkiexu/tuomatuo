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
    /**
     * @param cnxn
     * @param sessionId
     * @param xid
     * @param type
     * @param bb
     * @param authInfo
     */
    public LearnerSyncRequest(ServerCnxn cnxn, long sessionId, int xid, int type, ByteBuffer bb, List<Id> authInfo) {
        super(cnxn, sessionId, xid, type, bb, authInfo);
    }
}
