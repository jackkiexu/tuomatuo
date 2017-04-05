package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.Environment;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.StatPersisted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a simple standalone ZooKeeperServer. It sets up the
 * following chain of RequestProcessors to process request:
 * PrepRequestProcessor -> SyncRequestProcessor -> FinalRequestProcessor
 *
 * Created by xjk on 3/18/17.
 */
public class ZooKeeperServer implements SessionTracker.SessionExpirer, ServerStats.Provider {

    protected static final Logger LOG;

    static {
        LOG = LoggerFactory.getLogger(ZooKeeperServer.class);
        Environment.logEnv("Server environment:" , LOG);
    }

    protected ZooKeeperServerBean jmxServerBean;
    protected DataTreeBean jmxDataTreeBean;


    /**
     * The server delegates loading of the tree to an instance of the interface
     */
    public interface DataTreeBuilder{
        public DataTree build();
    }

    static public class BasicDataTreeBuilder implements DataTreeBuilder{
        public DataTree build(){
            return new DataTree();
        }
    }

    public static final int DEFAULT_TICK_TIME = 3000;
    protected int tickTime = DEFAULT_TICK_TIME;
    /** value of -1 indicates unset, use default */
    protected int minSessionTimeout = -1;
    /** value of -1 indicates unset, use default */
    protected int maxSessionTimeout = -1;

    protected SessionTracker sessionTracker;
    private FileTxnSnapLog txnLogFactory = null;
    private ZKDatabase zkDb;
    protected long hzxid = 0;
    public final static Exception ok = new Exception("No prob");
    protected RequestProcessor firstProcessor;
    protected volatile boolean running;

    /**
     * This is the secret that we use to to generate passwords, for the moment it
     * is more of a sanity check
     */
    static final private long superSecret = 0XB3415C00L;
    public int requestsInProcess;
    public final List<ChangeRecord> outstandingChanges = new ArrayList<ChangeRecord>();
    public final HashMap<String, ChangeRecord> outstandingChangesForPath =
            new HashMap<String, ChangeRecord>();

    private ServerCnxnFactory serverCnxnFactory;

    private final ServerStats serverStats;

    void removeCnxn(ServerCnxn cnxn){
        zkDb.removeCnxn(cnxn);
    }


    public int getClientPort(){
        return 0;
    }


    @Override
    public long getOutstandingRequests() {
        return 0;
    }

    @Override
    public long getLastProcessedZxid() {
        return 0;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public int getNumAliveConnections() {
        return 0;
    }

    @Override
    public long getDataDirSize() {
        return 0;
    }

    @Override
    public long getLogDirSize() {
        return 0;
    }

    @Override
    public void expire(SessionTracker.Session session) {

    }

    @Override
    public long getServerId() {
        return 0;
    }

    public enum State {
        INITIAL, RUNNING, SHUTDOWN, ERROR;
    }



    static class ChangeRecord{

        long zxid;

        String path;

        StatPersisted stat; // make sure to create a new object when  changing

        int childCount;

        List<ACL> acl; // make sure to create a new object when changing

        public ChangeRecord(long zxid, String path, StatPersisted stat,
                            int childCount, List<ACL> acl) {
            this.zxid = zxid;
            this.path = path;
            this.stat = stat;
            this.childCount = childCount;
            this.acl = acl;
        }


        ChangeRecord duplicate(long zxid){
            StatPersisted stat = new StatPersisted();
            if(this.stat != null){
                DataTree.copyStatPersisted(this.stat, stat);
            }
            return new ChangeRecord(zxid, path, stat, childCount,
                    acl == null? new ArrayList<ACL>(), new ArrayList<>(acl));
        }

    }



}
