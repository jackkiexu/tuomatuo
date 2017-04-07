package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.Watcher;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;
import com.lami.tuomatuo.mq.zookeeper.server.quorum.Leader;
import com.lami.tuomatuo.mq.zookeeper.server.util.SerializeUtils;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.apache.zookeeper.txn.TxnHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class maintains the in memory database of zookeeper
 * server states that includes the sessions, datatree and the
 * committed logs. It is booted up after reading the logs
 * and snapshots from the disk
 *
 *
 * Created by xjk on 3/18/17.
 */
public class ZKDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(ZKDatabase.class);


    /**
     * Make sure on a clear you tae care of
     * all these members
     */

    protected DataTree dataTree;
    protected ConcurrentHashMap<Long, Integer> sessionsWithTimeouts;
    protected FileTxnSnapLog snapLog;
    protected long minCommittedLog, maxCommittedLog;
    public static final int commitLogCount = 500;
    public static int commitLogBuffer = 700;

    protected LinkedList<Leader.Proposal> committedLog = new LinkedList<Leader.Proposal>();
    protected ReentrantReadWriteLock logLock = new ReentrantReadWriteLock();
    volatile private boolean initialized = false;

    public ZKDatabase(FileTxnSnapLog snapLog) {
        dataTree = new DataTree();
        sessionsWithTimeouts = new ConcurrentHashMap<>();
        this.snapLog = snapLog;
    }

    /**
     * Checks to see if the zk database has been initialized or not
     * @return true if zk database is initialized and false if not
     */
    public boolean isInitialized(){
        return initialized;
    }

    public void clear(){
        minCommittedLog = 0;
        maxCommittedLog = 0;
        /**
         * to be safe we just create a new
         * datatree
         */
        dataTree = new DataTree();
        sessionsWithTimeouts.clear();;
        ReentrantReadWriteLock.WriteLock lock = logLock.writeLock();
        try{
            lock.lock();
            committedLog.clear();
        }finally {
            lock.unlock();
        }
        initialized = false;
    }


    /**
     * the datatree for this zkDatabase
     * @return the datatree for this kDatabase
     */
    public DataTree getDataTree(){
        return this.dataTree;
    }

    /**
     * the committed log for this zk database
     * @return the committed log for this zkdatabse
     */
    public long getmaxCommittedLog(){
        return maxCommittedLog;
    }

    /**
     * the minimum committed transaction log
     * available in memory
     * @return the minimum committed transaction log available in memory
     */
    public long getminCommittedLog(){
        return minCommittedLog;
    }

    /**
     * Get te lock that controls the committedLog. If you want to get the pointer to
     * the committedLog, you need to use this lock to acquire a read lock before calling getCommittedLog()
     * @return the lock that controls the committed log
     */
    public ReentrantReadWriteLock getLogLock(){
        return logLock;
    }


    public synchronized LinkedList<Leader.Proposal> getCommittedLog(){
        ReentrantReadWriteLock.ReadLock rl = logLock.readLock();
        // only make a copy if this thread isn't already holding a lock
        if(logLock.getReadHoldCount() <= 0){
            try{
                rl.lock();
                return new LinkedList<Leader.Proposal>(this.committedLog);
            }finally {
                rl.unlock();
            }
        }
        return this.committedLog;
    }

    /**
     * get the last processed zxid from a datatree
     * @return the last processed zxid of a datatree
     */
    public long getDataTreeLastProcessedZxid(){
        return dataTree.lastProcessedZxid;
    }

    /**
     * set the datatree initialized or not
     * @param b set the datatree initialized to b
     */
    public void setDataTreeInit(boolean b){
        dataTree.initialized = b;
    }

    /**
     * return the sessions in the datatree
     * @return the data tree sessions
     */
    public Collection<Long> getSessions() {
        return dataTree.getSessions();
    }

    /**
     * get sessions with timeouts
     * @return the hashmap of sessions with timeouts
     */
    public ConcurrentHashMap<Long, Integer> getSessionsWithTimeouts(){
        return sessionsWithTimeouts;
    }

    /**
     * load the database from the disk onto memory and also add
     * the transactions to the committedlog in memory
     * @return the last valid zxid on disk
     * @throws IOException
     */
    public long loadDataBase() throws IOException{
        FileTxnSnapLog.PlayBackListener listener = new FileTxnSnapLog.PlayBackListener() {
            @Override
            public void onTxnLoaded(TxnHeader hdr, Record rec) {
                Request r = new Request(null, 0, hdr.getCxid(), hdr.getType(), null, null);
                r.txn = rec;
                r.hdr = hdr;
                r.zxid = hdr.getZxid();
                addCommittedProposal(r);
            }
        };

        long zxid = snapLog.restore(dataTree, sessionsWithTimeouts, listener);
        initialized = true;
        return zxid;
    }

    /**
     * maintains a list of last committedlog
     * or so committed request This is used for
     * fast follower synchronization
     * @param request committed request
     */
    public void addCommittedProposal(Request request){
        ReentrantReadWriteLock.WriteLock wl = logLock.writeLock();
        try{
            wl.lock();
            if(committedLog.size() > commitLogCount){
                committedLog.removeFirst();
                minCommittedLog = committedLog.getFirst().packet.getZxid();
            }
            if(committedLog.size() == 0){
                minCommittedLog = request.zxid;
                maxCommittedLog = request.zxid;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
            try{
                request.hdr.serialize(boa, "hdr");
                if(request.txn != null){
                    request.txn.serialize(boa, "txn");
                }
                baos.close();
            }catch (Exception e){
                LOG.error("This really should be impossible ", e);
            }

            QuorumPacket pp = new QuorumPacket(Leader.PROPOSAL, request.zxid,
                    baos.toByteArray(), null);
            Leader.Proposal p = new Leader.Proposal();
            p.packet = pp;
            p.request = request;
            committedLog.add(p);
            maxCommittedLog = p.packet.getZxid();

        }finally {
            wl.unlock();
        }
    }

    /**
     * remove a cnxn from the database
     * @param cnxn the cnxn to remove from the datatree
     */
    public void removeCnxn(ServerCnxn cnxn) {
        dataTree.removeCnxn(cnxn);
    }

    /**
     * Kill a given session in the datatree
     * @param sessionId the session id to be killed
     * @param zxid the zxid of kill session transaction
     */
    public void killSession(long sessionId, long zxid){
        dataTree.killSession(sessionId, zxid);
    }

    /**
     * write a text dump of all the ephemerals in the datatree
     * @param pwriter
     */
    public void dumpEphemerals(PrintWriter pwriter){
        dataTree.dumpEphemerals(pwriter);
    }

    /**
     * the node count of the datatree
     * @return
     */
    public int getNodeCount(){
        return dataTree.getNodeCount();
    }

    /**
     * the paths for ephemeral session id
     * @param sessionId the session id for which paths match to
     * @return the paths for a session id
     */
    public HashSet<String> getEphemerals(long sessionId){
        return dataTree.getEphemerals(sessionId);
    }

    /**
     * the last processed zxid in the datatree
     * @param zxid
     */
    public void setlastProcessedZxid(long zxid){
        dataTree.lastProcessedZxid = zxid;
    }

    /**
     * the process txn on the data
     */
    public DataTree.ProcessTxnResult processTxn(TxnHeader hdr, Record txn){
        return dataTree.processTxn(hdr, txn);
    }

    /**
     * stat the path
     */
    public Stat statNode(String path, ServerCnxn serverCnxn) throws Exception{
        return dataTree.statNode(path, serverCnxn);
    }

    /**
     * get the datanode for this path
     * @param path
     * @return
     */
    public DataNode getNode(String path){
        return dataTree.getNode(path);
    }

    /**
     * convert from long to acl entry
     * @param aclL
     * @return
     */
    public List<ACL> convertLong(Long aclL){
        return dataTree.convertLong(aclL);
    }

    /**
     * get data and stat for a path
     */
    public byte[] getData(String path, Stat stat, Watcher watcher) throws Exception{
        return dataTree.getData(path, stat, watcher);
    }

    /**
     * check if the path is special or not
     * @param path
     * @return
     */
    public boolean isSpecialPath(String path) {
        return dataTree.isSpecialPath(path);
    }

    /**
     * get the acl size of the datatree
     * @return
     */
    public int getAclSize(){
        return dataTree.longKeyMap.size();
    }

    /**
     * Truncate the ZKDatabase to the specified zxid
     * @param zxid the zxid to truncate zk database to
     * @return true if thr truncate is successful and false if not
     * @throws Exception
     */
    public boolean truncateLog(long zxid) throws Exception{
        clear();

        // truncate the log
        boolean truncated = snapLog.truncateLog(zxid);
        if(!truncated){
            return false;
        }
        loadDataBase();
        return true;
    }

    public void deserializeSnapshot(InputArchive ia) throws Exception{
        clear();
        SerializeUtils.deserializeSnapshot(getDataTree(), ia, getSessionsWithTimeouts());;
        initialized = true;
    }

    public void serializeSnapshot(OutputArchive oa) throws Exception{
        SerializeUtils.serializeSnapshot(getDataTree(), oa, getSessionsWithTimeouts());
    }

    /**
     * append to the underlying transaction log
     * @param si
     * @return
     * @throws Exception
     */
    public boolean append(Request si) throws Exception{
        return this.snapLog.append(si);
    }

    /**
     * roll to the underlying  log
     * @throws Exception
     */
    public void rollLog() throws Exception{
        this.snapLog.rollLog();
    }

    /**
     * commit to the underlying transaction log
     * @throws Exception
     */
    public void commit() throws Exception{
        this.snapLog.commit();
    }

    /**
     * close this database, free the resources
     * @throws Exception
     */
    public void close() throws Exception{
        this.snapLog.close();
    }
}
