package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.common.Time;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.flexible.QuorumVerifier;
import lombok.Data;
import org.apache.jute.Record;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.txn.TxnHeader;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * This is the structure that represents a request moving through a chain of
 * RequestProcessors. There are various pieces of information that is tacked
 * onto the request as it is processed
 * Created by xjk on 3/20/17.
 */
@Data
public class Request {

    public final static Request requestOfDeath = new Request(null, 0, 0, 0, null, null);


    public Request(ServerCnxn cnxn, long sessionId, int xid, int type, ByteBuffer bb,  List<Id> authInfo) {
        this.cnxn = cnxn;
        this.sessionId = sessionId;
        this.cxid = xid;
        this.type = type;
        this.request = bb;
        this.authInfo = authInfo;
    }

    public Request(long sessionId, int xid, int type, TxnHeader hdr, Record txn, long zxid) {
        this.sessionId = sessionId;
        this.cxid = xid;
        this.type = type;
        this.hdr = hdr;
        this.txn = txn;
        this.zxid = zxid;
        this.request = null;
        this.cnxn = null;
        this.authInfo = null;
    }

    public final long sessionId;

    public final int cxid;

    public final int type;

    public final ByteBuffer request;

    public final ServerCnxn cnxn;

    private TxnHeader hdr;

    private Record txn;

    public long zxid = -1;

    public final List<Id> authInfo;

    public final long createTime = Time.currentElapsedTime();

    private Object owner;

    private KeeperException e;

    public QuorumVerifier qv = null;

    /** If this is a create or close request for a local-only session */
    private boolean isLocalSession = false;


    /**
     * is the packet type a valid packet in zookeeper
     *
     * @param type
     *                the type of the packet
     * @return true if a valid packet, false if not
     */
    static boolean isValid(int type) {
        // make sure this is always synchronized with Zoodefs!!
        switch (type) {
            case ZooDefs.OpCode.notification:
                return false;
            case ZooDefs.OpCode.check:
            case ZooDefs.OpCode.closeSession:
            case ZooDefs.OpCode.create:
            case ZooDefs.OpCode.create2:
            case ZooDefs.OpCode.createTTL:
            case ZooDefs.OpCode.createContainer:
            case ZooDefs.OpCode.createSession:
            case ZooDefs.OpCode.delete:
            case ZooDefs.OpCode.deleteContainer:
            case ZooDefs.OpCode.exists:
            case ZooDefs.OpCode.getACL:
            case ZooDefs.OpCode.getChildren:
            case ZooDefs.OpCode.getChildren2:
            case ZooDefs.OpCode.getData:
            case ZooDefs.OpCode.multi:
            case ZooDefs.OpCode.ping:
            case ZooDefs.OpCode.reconfig:
            case ZooDefs.OpCode.setACL:
            case ZooDefs.OpCode.setData:
            case ZooDefs.OpCode.setWatches:
            case ZooDefs.OpCode.sync:
            case ZooDefs.OpCode.checkWatches:
            case ZooDefs.OpCode.removeWatches:
                return true;
            default:
                return false;
        }
    }

    public boolean isQuorum() {
        switch (this.type) {
            case ZooDefs.OpCode.exists:
            case ZooDefs.OpCode.getACL:
            case ZooDefs.OpCode.getChildren:
            case ZooDefs.OpCode.getChildren2:
            case ZooDefs.OpCode.getData:
                return false;
            case ZooDefs.OpCode.create:
            case ZooDefs.OpCode.create2:
            case ZooDefs.OpCode.createTTL:
            case ZooDefs.OpCode.createContainer:
            case ZooDefs.OpCode.error:
            case ZooDefs.OpCode.delete:
            case ZooDefs.OpCode.deleteContainer:
            case ZooDefs.OpCode.setACL:
            case ZooDefs.OpCode.setData:
            case ZooDefs.OpCode.check:
            case ZooDefs.OpCode.multi:
            case ZooDefs.OpCode.reconfig:
                return true;
            case ZooDefs.OpCode.closeSession:
            case ZooDefs.OpCode.createSession:
                return !this.isLocalSession;
            default:
                return false;
        }
    }

    static String op2String(int op) {
        switch (op) {
            case ZooDefs.OpCode.notification:
                return "notification";
            case ZooDefs.OpCode.create:
                return "create";
            case ZooDefs.OpCode.create2:
                return "create2";
            case ZooDefs.OpCode.createTTL:
                return "createTtl";
            case ZooDefs.OpCode.createContainer:
                return "createContainer";
            case ZooDefs.OpCode.setWatches:
                return "setWatches";
            case ZooDefs.OpCode.delete:
                return "delete";
            case ZooDefs.OpCode.deleteContainer:
                return "deleteContainer";
            case ZooDefs.OpCode.exists:
                return "exists";
            case ZooDefs.OpCode.getData:
                return "getData";
            case ZooDefs.OpCode.check:
                return "check";
            case ZooDefs.OpCode.multi:
                return "multi";
            case ZooDefs.OpCode.setData:
                return "setData";
            case ZooDefs.OpCode.sync:
                return "sync:";
            case ZooDefs.OpCode.getACL:
                return "getACL";
            case ZooDefs.OpCode.setACL:
                return "setACL";
            case ZooDefs.OpCode.getChildren:
                return "getChildren";
            case ZooDefs.OpCode.getChildren2:
                return "getChildren2";
            case ZooDefs.OpCode.ping:
                return "ping";
            case ZooDefs.OpCode.createSession:
                return "createSession";
            case ZooDefs.OpCode.closeSession:
                return "closeSession";
            case ZooDefs.OpCode.error:
                return "error";
            case ZooDefs.OpCode.reconfig:
                return "reconfig";
            case ZooDefs.OpCode.checkWatches:
                return "checkWatches";
            case ZooDefs.OpCode.removeWatches:
                return "removeWatches";
            default:
                return "unknown " + op;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sessionid:0x").append(Long.toHexString(sessionId))
                .append(" type:").append(op2String(type))
                .append(" cxid:0x").append(Long.toHexString(cxid))
                .append(" zxid:0x").append(Long.toHexString(hdr == null ?
                -2 : hdr.getZxid()))
                .append(" txntype:").append(hdr == null ?
                "unknown" : "" + hdr.getType());

        // best effort to print the path assoc with this request
        String path = "n/a";
        if (type != ZooDefs.OpCode.createSession
                && type != ZooDefs.OpCode.setWatches
                && type != ZooDefs.OpCode.closeSession
                && request != null
                && request.remaining() >= 4)
        {
            try {
                // make sure we don't mess with request itself
                ByteBuffer rbuf = request.asReadOnlyBuffer();
                rbuf.clear();
                int pathLen = rbuf.getInt();
                // sanity check
                if (pathLen >= 0
                        && pathLen < 4096
                        && rbuf.remaining() >= pathLen)
                {
                    byte b[] = new byte[pathLen];
                    rbuf.get(b);
                    path = new String(b);
                }
            } catch (Exception e) {
                // ignore - can't find the path, will output "n/a" instead
            }
        }
        sb.append(" reqpath:").append(path);

        return sb.toString();
    }

    public void setException(KeeperException e) {
        this.e = e;
    }

    public KeeperException getException() {
        return e;
    }

}
