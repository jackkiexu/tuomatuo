package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.server.ServerCnxn;
import com.lami.tuomatuo.mq.zookeeper.server.ZKDatabase;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;

/**
 * Just like the standard ZooKeeperServer. We just replace the request
 * processors: PrepRqequestProcessor -> ProposalRequestProcessor ->
 * CommmitProcessor -> leader. ToBeAppliedrequestProcessor
 * FinalRequestProcessor
 *
 * Created by xujiankang on 2017/3/19.
 */
public class LeaderZooKeeperServer extends QuorumZooKeeperServer {

    public CommitProcessor commitProcessor;

    public LeaderZooKeeperServer(FileTxnSnapLog txnLogFactory, int tickTime,QuorumPeer self,
                                 DataTreeBuilder treeBuilder, ZKDatabase zkDb) {
        super(txnLogFactory, tickTime,self. minSessionTimeout,self. maxSessionTimeout, treeBuilder, zkDb, self);
    }

    public Leader getLeader(){
        return self.leader;
    }


    @Override
    protected void setupRequestProcessors() {

    }

    public int getGlobalOutstandingLimit(){

    }

    @Override
    protected void createSessionTracker() {

    }

    @Override
    public void startSessionTracker() {

    }

    public boolean touch(long sess, int to){
        return sessionTracker.touchSession(sess, to);
    }

    @Override
    protected void registerJMX() {

    }

    public void registerJMX(LeaderBean leaderBean, LocalPeerBean localPeerBean){

    }

    @Override
    protected void unregisterJMX() {

    }

    protected void unregisterJMX(Leader leader) {

    }


    @Override
    public String getState() {
        return "leader";
    }

    @Override
    public long getServerId() {
        return self.getId();
    }

    @Override
    public void revalidateSession(ServerCnxn cnxn, long sessionId, int sessionTimeout) throws Exception {
        super.revalidateSession(cnxn, sessionId, sessionTimeout);
    }
}
