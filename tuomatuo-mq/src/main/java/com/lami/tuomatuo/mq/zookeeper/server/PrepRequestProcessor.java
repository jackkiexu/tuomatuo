package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.*;
import com.lami.tuomatuo.mq.zookeeper.common.PathUtils;
import com.lami.tuomatuo.mq.zookeeper.server.auth.AuthenticationProvider;
import com.lami.tuomatuo.mq.zookeeper.server.auth.ProviderRegistry;
import org.apache.jute.Record;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.StatPersisted;
import org.apache.zookeeper.proto.CheckVersionRequest;
import org.apache.zookeeper.proto.CreateRequest;
import org.apache.zookeeper.proto.DeleteRequest;
import org.apache.zookeeper.txn.CreateTxn;
import org.apache.zookeeper.txn.ErrorTxn;
import org.apache.zookeeper.txn.TxnHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This request processor is genrally at the start of a RequestProcessor
 * change. It sets up any transaction associated with requests that change the
 * state of the system. It counts on ZooKeeperServer to update
 * outstandingRequests, so that it can take into account transactions that are
 * in the queue to be applied when generating a transaction
 *
 * Created by xujiankang on 2017/3/19.
 */
public class PrepRequestProcessor extends Thread implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PrepRequestProcessor.class);

    static boolean skipACL;
    static {
        skipACL = System.getProperty("zookeeper.skipACL", "no").equals("yes");
        LOG.info("zookeeper.skipACL : " + skipACL);
    }

    private static boolean failCreate = false;

    public LinkedBlockingQueue<Request> submittedRequests = new LinkedBlockingQueue<>();

    public RequestProcessor nextProcessor;

    public ZooKeeperServer zks;

    public PrepRequestProcessor(ZooKeeperServer zks, RequestProcessor nextProcessor) {
        super("ProcessThread(sid:" + zks.getServerId()
        + " cport "+ zks.getClientPort() + ")");
        this.nextProcessor = nextProcessor;
        this.zks = zks;
    }


    public static void setFailCreate(boolean b){
        failCreate = b;
    }

    @Override
    public void run() {
        try{
            while(true){
                Request request = submittedRequests.take();
                long traceMask = ZooTrace.CLIENT_PING_TRACE_MASK;
                if(request.type == ZooDefs.OpCode.ping){
                    traceMask = ZooTrace.CLIENT_REQUEST_TRACE_MASK;
                }
                ZooTrace.logRequest(LOG, traceMask, 'P', request, "");
                if(Request.requestOfDeath == request){
                    break;
                }
                pRequest(request);
            }
        }catch (Exception e){
            LOG.info("Unexpected exception ", e);
        }
        LOG.info("PrepRequestProcessor exited loop!");
    }


    ZooKeeperServer.ChangeRecord getRecordForPath(String path) throws Exception{
        ZooKeeperServer.ChangeRecord lastChange = null;
        synchronized (zks.outstandingChanges){
            lastChange = zks.outstandingChangesForPath.get(path);

            if(lastChange == null){
                DataNode n = zks.getZkDatabase().getNode(path);
                if(n != null){
                    Long acl;
                    Set<String> children;
                    synchronized (n){
                        acl = n.acl;
                        children = n.getChildren();
                    }
                    lastChange = new ZooKeeperServer.ChangeRecord(-1, path, n.stat,
                            children != null ? children.size() : 0,
                            zks.getZKDatabase().convertLong(acl));
                }
            }
        }

        if(lastChange == null || lastChange.stat == null){
            throw new KeeperException.NoNodeException();
        }

        return lastChange;
    }



    void addChangeRecord(ZooKeeperServer.ChangeRecord c){
        synchronized (zks.outstandingChanges){
            zks.outstandingChanges.add(c);
            zks.outstandingChangesForPath.put(c.path, c);
        }
    }


    /**
     * Grab current pending change records for each op in a multi-op
     *
     * This is used inside MultiOp error path to rollback in the event
     * of a failed multi-op
     *
     * @param multiRequest
     * @return
     */
    public HashMap<String, ZooKeeperServer.ChangeRecord> getPendingChanges(MultiTransactionRecord multiRequest){
        HashMap<String, ZooKeeperServer.ChangeRecord> pendingChangeRecords = new HashMap<>();

        for(Op op : multiRequest){
            String path = op.getPath();

            try{
                ZooKeeperServer.ChangeRecord cr = getRecordForPath(path);
                if(cr != null){
                    pendingChangeRecords.put(path, cr);
                }

                int lastSlash = path.lastIndexOf('/');
                if(lastSlash == -1 || path.indexOf('\0') != -1){
                    continue;
                }

                String parentPath = path.substring(0, lastSlash);
                ZooKeeperServer.ChangeRecord parentCr = getRecordForPath(parentPath);
                if(parentCr != null){
                    pendingChangeRecords.put(parentPath, parentCr);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return pendingChangeRecords;
    }

    /**
     * Rollback pending changes record from a failed multi-op
     *
     * if a multi-op fails, we can't leave any invalid change records we created
     * around. We also need to restore their prior value (if any) if their prior
     * value is still valid
     */
    void rollbackPendingChanges(long zxid, HashMap<String, ZooKeeperServer.ChangeRecord> pendingChangeReccords){

        synchronized (zks.outstandingChanges){
            // grab a list iterator starting at the END of the list so we can iterate in reverse
            ListIterator<ZooKeeperServer.ChangeRecord> iter = zks.outstandingChanges.listIterator(zks.outstandingChanges.size());
            while(iter.hasPrevious()){
                ZooKeeperServer.ChangeRecord c = iter.previous();
                if(c.zxid == zxid){
                    iter.remove();
                    zks.outstandingChangesForPath.remove(c.path);
                }else{
                    break;
                }
            }

            boolean empty = zks.outstandingChanges.isEmpty();
            long firstZxid = 0;
            if(!empty){
                firstZxid = zks.outstandingChanges.get(0).zxid;
            }

            Iterator<ZooKeeperServer.ChangeRecord> priorIter = pendingChangeReccords.values().iterator();
            while(priorIter.hasNext()){
                ZooKeeperServer.ChangeRecord c = priorIter.next();

                // Don't apply any prior change records less than firstZxid
                if(!empty && (c.zxid < firstZxid)){
                    continue;
                }

                zks.outstandingChangesForPath.put(c.path, c);
            }
        }

    }

    static void checkACL(ZooKeeperServer zks, List<ACL> acl, int perm,
                         List<Id> ids) throws Exception{
        if(skipACL){
            return;
        }

        if(acl == null || acl.size() == 0){
            return;
        }

        for(Id authId : ids){
            if(authId.getScheme().equals("super")){
                return;
            }
        }

        for(ACL a : acl){
            Id id = a.getId();
            if((a.getPerms() & perm) != 0){
                if(id.getScheme().equals("world")
                        && id.getId().equals("anyone")){
                    return;
                }
                AuthenticationProvider ap = ProviderRegistry.getProvider(id.getScheme());

                if(ap != null){
                    for(Id authId : ids){
                        if(authId.getScheme().equals(id.getScheme())
                                && ap.matches(authId.getId(), id.getId())){
                            return;
                        }
                    }
                }
            }
        }
        throw new KeeperException.NoAuthException();
    }

    /**
     * This method will be called inside the ProcessRequestThread, which is a
     * singleton, so there will be a single thread calling this code
     */
    protected void pRequest2Txn(int type, long zxid, Request request, Record record, boolean deserialize)throws Exception{

        request.hdr = new TxnHeader(request.sessionId, request.cxid, zxid, zks.getTime(), type);

        switch (type){
            case ZooDefs.OpCode.create:{
                zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                CreateRequest createRequest = (CreateRequest)record;
                if(deserialize){
                    ByteBufferInputStream.byteBuffer2Record(request.request, createRequest);
                }

                String path = createRequest.getPath();
                int lastSlash = path.lastIndexOf('/');
                if(lastSlash == -1 || path.indexOf('\0') != -1 || failCreate){
                    LOG.info("Invalid path " + path + " with session 0x" +
                            Long.toHexString(request.sessionId));
                    throw new KeeperException.BadArgumentsException(path);
                }

                List<ACL> listACL = removeDuplicates(createRequest.getAcl());
                if(!fixupACL(request.authInfo, listACL)){
                    throw new KeeperException.InvalidACLException(path);
                }

                String parentPath = path.substring(0, lastSlash);
                ZooKeeperServer.ChangeRecord parentRecord = getRecordForPath(parentPath);

                checkACL(zks, parentRecord.acl, ZooDefs.Perms.CREATE,
                        request.authInfo);

                int parentCVersion = parentRecord.stat.getCversion();
                CreateMode createMode = CreateMode.fromFlag(createRequest.getFlags());

                if(createMode.isSequential()){
                    path = path + String.format(Locale.ENGLISH, "%010d", parentCVersion);
                }

                try{
                    PathUtils.validatePath(path);
                }catch (Exception e){
                    throw new KeeperException.BadArgumentsException(path);
                }

                try{
                    if(getRecordForPath(path) != null){
                        throw new KeeperException.NodeExistsException(path);
                    }
                }catch (KeeperException.NoNodeException e){

                }


                boolean ephemeralParent = parentRecord.stat.getEphemeralOwner() != 0;
                if(ephemeralParent){
                    throw new KeeperException.NoChildrenForEphemeralsException(path);
                }

                int newCversion = parentRecord.stat.getCversion() + 1;
                request.txn = new CreateTxn(path, createRequest.getData(),
                        listACL,
                        createMode.isEphemral(), newCversion);

                StatPersisted s = new StatPersisted();
                if(createMode.isEphemral()){
                    s.setEphemeralOwner(request.sessionId);
                }

                parentRecord = parentRecord.duplicate(request.hdr.getZxid());
                parentRecord.childCount++;
                parentRecord.stat.setCversion(newCversion);
                addChangeRecord(parentRecord);
                addChangeRecord(new ZooKeeperServer.ChangeRecord(request.hdr.getZxid(), path, s,
                        0, listACL));
                break;
            }
            case ZooDefs.OpCode.delete:{
                zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                DeleteRequest deleteRequest = (DeleteRequest)record;
                if(deserialize){
                    ByteBufferInputStream.byteBuffer2Record(request.request, deleteRequest);
                }

                String path = deleteRequest.getPath();
                String lastSlash = path.lastIndexOf('/');
            }
            case ZooDefs.OpCode.setData:{

            }
            case ZooDefs.OpCode.setACL:{

            }
            case ZooDefs.OpCode.createSession:{

            }
            case ZooDefs.OpCode.closeSession:{

            }
            case ZooDefs.OpCode.check:{
                zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                CheckVersionRequest checkVersionRequest = (CheckVersionRequest)record;
                if(deserialize){
                    ByteBufferInputStream.byteBuffer2Record(request.request, checkVersionRequest);;
                }

                path = checkVersionRequest.getPath();

            }

        }
    }


    /**
     * This method will be called inside the ProcessRequestThread, which is a
     * singleton, so there will be a single thread calling this code
     *
     * @param request
     * @throws RequestProcessorException
     */
    protected void pRequest(Request request) throws RequestProcessorException{
       request.hdr = null;
        request.txn = null;

        try{

            switch (request.type){

            }

        }catch (KeeperException e){
            if(request.hdr != null){
                request.hdr.setType(ZooDefs.OpCode.error);
                request.txn = new ErrorTxn(e.code().intValue());
            }
        }catch (Exception e){
            LOG.error("Failed to prcess " + request, e);
            StringBuilder sb = new StringBuilder();
            ByteBuffer bb = request.request;
            if(bb != null){
                bb.rewind();
                while(bb.hasRemaining()){
                    sb.append(Integer.toHexString(bb.get() & 0xff));
                }
            }else{
                sb.append("request buffer is null");
            }

            LOG.info("Dumping request buffer: 0x" + sb.toString());
            if(request.hdr != null){
                request.hdr.setType(ZooDefs.OpCode.error);
                request.txn = new ErrorTxn(KeeperException.Code.MARSHALLINGERROR.intValue());
            }
        }

        request.zxid = zks.getZxid();
        nextProcessor.processRequest(request);
    }


    private List<ACL> removeDuplicates(List<ACL> acl){
        ArrayList<ACL> retval = new ArrayList<ACL>();
        Iterator<ACL> it = acl.iterator();
        while(it.hasNext()){
            ACL a = it.next();
            if(retval.contains(a) == false){
                retval.add(a);
            }
        }
        return retval;
    }


    public PrepRequestProcessor(String name, ZooKeeperServerListener listener) {
        super(name, listener);
    }


    /**
     * This method checks out the acl making sure it isn't null or empty
     * it has valid schemes and ids, and expending any relative ids that
     * depend on the requestor's authentication information
     *
     * @param authInfo list of ACL IDs associated with the client connection
     * @param acl list of ACLs being assigned to the node (create or setACL operation)
     * @return
     */
    private boolean fixupACL(List<Id> authInfo, List<ACL> acl){
        if(skipACL){
            return true;
        }

        if(acl == null || acl.size() == 0){
            return false;
        }

        Iterator<ACL> it = acl.iterator();
        LinkedList<ACL> toAdd = null;
        while(it.hasNext()){
            ACL a = it.next();
            Id id = a.getId();
            if(id.getScheme().equals("world") && id.getId().equals("anyone")){
                // wide open
            }else if(id.getScheme().equals("auth")){
                // This is the "auth" id, so we have to expand it to the
                // authenticated ids of the requestor
                it.remove();
                if(toAdd == null){
                    toAdd = new LinkedList<>();
                }
                boolean authIdValid = false;

                for(Id cid : authInfo){
                    AuthenticationProvider ap = ProviderRegistry.getProvider(cid.getScheme());
                    if(ap == null){
                        LOG.info("Missing AuthenticaticationProvider for " + cid.getScheme());
                    }
                    else if(ap.isAuthenticated()){
                        authIdValid = true;
                        toAdd.add(new ACL(a.getPerms(), cid));
                    }
                }

                if(!authIdValid){
                    return false;
                }
            }else{
                AuthenticationProvider ap = ProviderRegistry.getProvider(id.getScheme());
                if(ap == null){
                    return false;
                }
                if(!ap.isValid(id.getId())){
                    return false;
                }
            }
        }

        if(toAdd != null){
            for(ACL a : toAdd){
                acl.add(a);
            }
        }
        return acl.size() > 0;
    }

    public void processRequest(Request request){
        //
        submittedRequests.add(request);
    }

    @Override
    public void shutdown() {
        LOG.info("Shutting down");
        submittedRequests.clear();
        submittedRequests.add(Request.requestOfDeath);
        nextProcessor.shutdown();
    }
}
