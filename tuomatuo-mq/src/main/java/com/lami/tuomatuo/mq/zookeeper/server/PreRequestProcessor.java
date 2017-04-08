package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.server.auth.AuthenticationProvider;
import com.lami.tuomatuo.mq.zookeeper.server.auth.ProviderRegistry;
import org.apache.jute.Record;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.txn.ErrorTxn;
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
public class PreRequestProcessor extends Thread implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PreRequestProcessor.class);

    static boolean skipACL;
    static {
        skipACL = System.getProperty("zookeeper.skipACL", "no").equals("yes");
        LOG.info("zookeeper.skipACL : " + skipACL);
    }

    private static boolean failCreate = false;

    public LinkedBlockingQueue<Request> submittedRequests = new LinkedBlockingQueue<>();

    public RequestProcessor nextProcessor;

    public ZooKeeperServer zks;

    public PreRequestProcessor( ZooKeeperServer zks, RequestProcessor nextProcessor) {
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

        }catch (){

        }catch (){

        }catch (){

        }

        LOG.info("PrepRequestProcessor exited loop!");
    }


    /**
     * Rollback pending changes record from a failed multi-op
     *
     * if a multi
     */
    void rollbackPendingChanges(long zxid, HashMap<String, ZooKeeperServer.ChangeRecord> pendingChangeReccords){

    }

    static void checkACL(ZooKeeperServer zks, List<ACL> acl, int perm,
                         List<Id> ids) throws Exception{

    }

    protected void pRequest2Txn(int type, long zxid, Request request, Record record, boolean deserialize)throws Exception{

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


    public PreRequestProcessor(String name, ZooKeeperServerListener listener) {
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
