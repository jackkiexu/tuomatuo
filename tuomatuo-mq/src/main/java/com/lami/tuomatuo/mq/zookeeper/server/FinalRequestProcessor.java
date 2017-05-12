package com.lami.tuomatuo.mq.zookeeper.server;


import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.MultiResponse;
import com.lami.tuomatuo.mq.zookeeper.OpResult;
import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import org.apache.jute.Record;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.proto.*;
import org.apache.zookeeper.txn.ErrorTxn;
import org.apache.zookeeper.txn.TxnHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * This Request processor actually applies transaction associated with a
 * request and services any quiries It is always at the end of a
 * RequestProcessor chain (hence the name). so it does not have a nextProcessor
 * member
 *
 * This RequestProcessor counts on ZooKeeperServer to populate the
 * outstandingRequests member of ZooKeeperServer
 *
 * Created by xujiankang on 2017/3/19.
 */
public class FinalRequestProcessor implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(FinalRequestProcessor.class);

    ZooKeeperServer zks;

    public FinalRequestProcessor(ZooKeeperServer zks) {
        this.zks = zks;
    }

    @Override
    public void processRequest(Request request) throws RequestProcessorException {
        LOG.info("Processing request :" + request);
        long traceMask = ZooTrace.CLIENT_REQUEST_TRACE_MASK;
        if(request.type == ZooDefs.OpCode.ping){
            traceMask = ZooTrace.SERVER_PING_TRACE_MASK;
        }
        ZooTrace.logRequest(LOG, traceMask, 'E', request, "");
        DataTree.ProcessTxnResult rc = null;


        synchronized (zks.outstandingChanges){
            while(!zks.outstandingChanges.isEmpty()
                    && zks.outstandingChanges.get(0).zxid <= request.zxid){
                ZooKeeperServer.ChangeRecord  cr = zks.outstandingChanges.remove(0);
                if(cr.zxid < request.zxid){
                    LOG.info("Zxid outstanding" + cr.zxid
                                + " is less than current " + request.zxid);
                }
                if(zks.outstandingChangesForPath.get(cr.path) == cr){
                    zks.outstandingChangesForPath.remove(cr.path);
                }
            }

            if(request.hdr != null){
                TxnHeader hdr = request.hdr;
                Record txn = request.txn;

                rc = zks.processTxn(hdr, txn);
            }

            // do not add non quorum packets to the queue
            if(Request.isQuorum(request.type)){
                zks.getZKDatabase().addCommittedProposal(request);
            }
        }


        if (request.hdr != null && request.hdr.getType() == ZooDefs.OpCode.closeSession){
            ServerCnxnFactory scxn = zks.getServerCnxnFactory();
            // this might be possible since
            // we might just be playing diffs from the leader
            if(scxn != null && request.cnxn == null){
                /**
                 * Calling this if we have cnxn results in the client's
                 * close session response being lost - we're already closed
                 * the session/socket here before we can send the closeSession
                 * in the switch block below
                 */
                scxn.closeSession(request.sessionId);
                return;
            }
        }

        if(request.cnxn == null){
            return;
        }

        ServerCnxn cnxn = request.cnxn;

        String lastOp = "NA";
        zks.decInProcess();;
        KeeperException.Code err = KeeperException.Code.OK;
        Record rsp = null;
        boolean closeSession = false;

        try{
            if(request.hdr != null && request.hdr.getType() == ZooDefs.OpCode.error){
                throw  KeeperException.create(KeeperException.Code.get(((ErrorTxn)request.txn).getErr()));
            }

            KeeperException ke = request.getException();
            if(ke != null && request.type != ZooDefs.OpCode.multi){
                throw ke;
            }

            LOG.info("{}", request);

            switch (request.type){
                case ZooDefs.OpCode.ping:{
                    zks.serverStats().updateLatency(request.createTime);
                    lastOp = "PING";
                    cnxn.updateStatsForResponse(request.cxid, request.zxid, lastOp,
                            request.createTime, System.currentTimeMillis());
                    cnxn.sendResponse(new ReplyHeader(-2, zks.getZkDatabase().getDataTreeLastProcessedZxid(), 0), null, "response");
                    return;
                }
                case ZooDefs.OpCode.createSession:{
                    zks.serverStats().updateLatency(request.createTime);

                    lastOp = "SESS";
                    cnxn.updateStatsForResponse(request.cxid, request.zxid, lastOp,
                            request.createTime, System.currentTimeMillis());
                    zks.finishSessionInit(request.cnxn, true);
                    return;
                }
                case ZooDefs.OpCode.multi:{
                    lastOp = "MULT";
                    rsp = new MultiResponse();

                    for(DataTree.ProcessTxnResult subTxnResult : rc.multiResult){
                        OpResult subResult;
                        switch (subTxnResult.type){
                            case ZooDefs.OpCode.check:
                                subResult = new OpResult.CheckResult();
                                break;
                            case ZooDefs.OpCode.create:
                                subResult = new OpResult.CreateResult(subTxnResult.path);
                                break;
                            case ZooDefs.OpCode.delete:
                                subResult = new OpResult.DeleteResult();
                                break;
                            case ZooDefs.OpCode.setData:
                                subResult = new OpResult.SetDataResult(subTxnResult.stat);
                                break;
                            case ZooDefs.OpCode.error:
                                subResult = new OpResult.ErrorResult(subTxnResult.err);
                                break;
                            default:
                                throw new IOException("Invalid type of op");
                        }

                        ((MultiResponse)rsp).add(subResult);
                    }
                }
                case ZooDefs.OpCode.create:{
                    lastOp = "CREA";
                    rsp = new CreateResponse(rc.path);
                    err = KeeperException.Code.get(rc.err);
                    break;
                }
                case ZooDefs.OpCode.delete:{
                    lastOp = "DELE";
                    err = KeeperException.Code.get(rc.err);
                    break;
                }
                case ZooDefs.OpCode.setData:{
                    lastOp = "SETD";
                    rsp = new SetDataResponse(rc.stat);
                    err = KeeperException.Code.get(rc.err);
                    break;
                }
                case ZooDefs.OpCode.setACL:{
                    lastOp = "SETA";
                    rsp = new SetACLResponse(rc.stat);
                    err = KeeperException.Code.get(rc.err);
                    break;
                }
                case ZooDefs.OpCode.closeSession:{
                    lastOp = "CLOS";
                    closeSession = true;
                    err = KeeperException.Code.get(rc.err);
                    break;
                }
                case ZooDefs.OpCode.sync:{
                    lastOp = "SYNC";
                    SyncRequest syncRequest = new SyncRequest();
                    ByteBufferInputStream.byteBuffer2Record(request.request,
                            syncRequest);
                    rsp = new SyncResponse(syncRequest.getPath());
                    break;
                }
                case ZooDefs.OpCode.check:{
                    lastOp = "CHEC";
                    rsp = new SetDataResponse(rc.stat);
                    err = KeeperException.Code.get(rc.err);
                    break;
                }
                case ZooDefs.OpCode.exists:{
                    lastOp = "EXIS";
                    // TODO we need to figure out the security requirement for this
                    ExistsRequest existsRequest = new ExistsRequest();
                    ByteBufferInputStream.byteBuffer2Record(request.request,
                            existsRequest);
                    String path = existsRequest.getPath();
                    if(path.indexOf('\0') != -1){
                        throw new KeeperException.BadArgumentsException();
                    }
                    Stat stat = zks.getZkDatabase().statNode(path, existsRequest.getWatch()? cnxn : null);
                    rsp = new ExistsResponse(stat);
                    break;
                }
                case ZooDefs.OpCode.getData:{
                    lastOp = "GETD";
                    GetDataRequest getDataRequest = new GetDataRequest();
                    ByteBufferInputStream.byteBuffer2Record(request.request,
                            getDataRequest);
                    DataNode n = zks.getZkDatabase().getNode(getDataRequest.getPath());
                    if(n == null){
                        throw new KeeperException.NoNodeException();
                    }
                    Long aclL;
                    synchronized (n){
                        aclL = n.acl;
                    }

                    PrepRequestProcessor.checkACL(zks, zks.getZKDatabase().convertLong(aclL),
                            ZooDefs.Perms.READ,
                            request.authInfo
                            );
                    Stat stat = new Stat();
                    byte b[] = zks.getZKDatabase().getData(getDataRequest.getPath(), stat,
                            getDataRequest.getWatch() ? cnxn : null);
                    rsp = new GetDataResponse(b, stat);
                    break;
                }
                case ZooDefs.OpCode.setWatches:{
                    lastOp = "SETW";
                    SetWatches setWatches = new SetWatches();
                    request.request.rewind();
                    ByteBufferInputStream.byteBuffer2Record(request.request, setWatches);
                    long relativeZxid = setWatches.getRelativeZxid();
                    zks.getZKDatabase().setWatches(relativeZxid,
                            setWatches.getDataWatches(),
                            setWatches.getExistWatches(),
                            setWatches.getChildWatches(), cnxn);
                    break;
                }
                case ZooDefs.OpCode.getACL:{
                    lastOp = "GETA";
                    GetACLRequest getACLRequest = new GetACLRequest();
                    ByteBufferInputStream.byteBuffer2Record(request.request, getACLRequest);
                    Stat stat = new Stat();
                    List<ACL> acl = zks.getZKDatabase().getACL(getACLRequest.getPath(), stat);
                    rsp = new GetACLResponse(acl, stat);
                    break;
                }
                case ZooDefs.OpCode.getChildren:{
                    lastOp = "GETC";
                    GetChildrenRequest getChildrenRequest = new GetChildrenRequest();
                    ByteBufferInputStream.byteBuffer2Record(request.request,
                            getChildrenRequest);
                    DataNode n = zks.getZkDatabase().getNode(getChildrenRequest.getPath());
                    if(n == null){
                        throw new KeeperException.NoNodeException();
                    }
                    Long aclG;
                    synchronized (n){
                        aclG = n.acl;
                    }
                    PrepRequestProcessor.checkACL(zks, zks.getZKDatabase().convertLong(aclG),
                            ZooDefs.Perms.READ,
                            request.authInfo
                            );
                    List<String> children = zks.getZkDatabase().getChildren(
                    getChildrenRequest.getPath(), null, getChildrenRequest.getWatch()?cnxn:null
                    );
                    rsp = new GetChildrenResponse(children);
                }
                case ZooDefs.OpCode.getChildren2:{
                    lastOp = "GETC";
                    GetChildren2Request getChildren2Request = new GetChildren2Request();
                    ByteBufferInputStream.byteBuffer2Record(request.request, getChildren2Request);
                    Stat stat = new Stat();
                    DataNode n = zks.getZkDatabase().getNode(getChildren2Request.getPath());
                    if(n == null){
                        throw new KeeperException.NoNodeException();
                    }
                    Long aclG;
                    synchronized (n){
                        aclG = n.acl;
                    }
                    PrepRequestProcessor.checkACL(zks, zks.getZKDatabase().convertLong(aclG),
                            ZooDefs.Perms.READ,
                            request.authInfo
                            );
                    List<String> children = zks.getZKDatabase().getChildren(
                            getChildren2Request.getPath(), stat,
                            getChildren2Request.getWatch() ? cnxn : null
                    );
                    rsp = new GetChildren2Response(children, stat);
                    break;
                }

            }

        }catch (KeeperException.SessionMovedException e){
            /**
             * sessions moved is a connection level error, we need to tear
             * down the connection otw ZOOKEEPER-710 might happen
             * ie client on slow follower starts to renew session, fails
             * before this completes, then tries the fast follower(leader)
             * and is successful, however the initial renew is then
             * successfully fwd/processed by the leader and as a result
             * the client and leader disagree on where the client is most
             * recently attached (and therefore invalid SESSION MOVED generated)
             */
            cnxn.sendCloseSession();
            return;
        }catch (Exception e){
            // log at error level as we are returning a marshalling
            // error to the user
            LOG.error("Failed to process " + request, e);
            StringBuilder sb = new StringBuilder();
            ByteBuffer bb = request.request;
            bb.rewind();
            while(bb.hasRemaining()){
                sb.append(Integer.toHexString(bb.get() & 0xff));
            }
            LOG.error("Dumping request buffer: 0x " + sb.toString());
            err = KeeperException.Code.MARSHALLINGERROR;
        }

        long lastZxid = zks.getZkDatabase().getDataTreeLastProcessedZxid();
        ReplyHeader hdr = new ReplyHeader(request.cxid, lastZxid, err.intValue());

        zks.serverStats().updateLatency(request.createTime);
        cnxn.updateStatsForResponse(request.cxid, lastZxid, lastOp,
                request.createTime, System.currentTimeMillis());

        try{
            cnxn.sendResponse(hdr, rsp, "response");
            if(closeSession){
                cnxn.sendCloseSession();
            }
        }catch (Exception e){
            LOG.info("FIXMSG ", e);
        }
    }

    @Override
    public void shutdown() {
        LOG.info("shutdown of request processor complete");
    }
}
