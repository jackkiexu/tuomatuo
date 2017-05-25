package com.lami.tuomatuo.mq.zookeeper.server.quorum;

import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.jmx.MBeanRegistry;
import com.lami.tuomatuo.mq.zookeeper.server.*;
import com.lami.tuomatuo.mq.zookeeper.server.persistence.FileTxnSnapLog;

import java.io.IOException;

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

    LeaderZooKeeperServer(FileTxnSnapLog logFactory, QuorumPeer self,
                          DataTreeBuilder treeBuilder, ZKDatabase zkDb) throws IOException {
        super(logFactory, self.tickTime, self.minSessionTimeout,
                self.maxSessionTimeout, treeBuilder, zkDb, self);
    }

    public Leader getLeader(){
        return self.leader;
    }


    @Override
    protected void setupRequestProcessors() {
        RequestProcessor finalProcessor = new FinalRequestProcessor(this);
        RequestProcessor toBeAppliedProcessor = new Leader.ToBeAppliedRequestProcessor(finalProcessor, getLeader().toBeApplied);
        commitProcessor = new CommitProcessor(toBeAppliedProcessor, Long.toString(getServerId()), false);
        commitProcessor.start();
        ProposalRequestProcessor proposalRequestProcessor = new ProposalRequestProcessor(this, commitProcessor);
        proposalRequestProcessor.initialize();
        firstProcessor = new PrepRequestProcessor(this, proposalRequestProcessor);
        ((PrepRequestProcessor)firstProcessor).start();
    }

    @Override
    protected void createSessionTracker() {
        sessionTracker = new SessionTrackerImpl(this, getZKDatabase().getSessionsWithTimeouts(), tickTime, self.getId());
    }

    @Override
    public void startSessionTracker() {
        ((SessionTrackerImpl)sessionTracker).start();
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
        // unregister from JMX
        try {
            if (jmxDataTreeBean != null) {
                MBeanRegistry.getInstance().unregister(jmxDataTreeBean);
            }
        } catch (Exception e) {
            LOG.warn("Failed to unregister with JMX", e);
        }
        jmxDataTreeBean = null;
    }

    protected void unregisterJMX(Leader leader) {
        // unregister from JMX
        try{
            if(jmxServerBean != null){
                MBeanRegistry.getInstance().unregister(jmxServerBean);
            }
        }catch (Exception e){
            LOG.info("Failed to unregister with JMX");
        }
        jmxDataTreeBean = null;
    }


    @Override
    public String getState() {
        return "leader";
    }

    /**
     * Returns the id of the associated QuorumPeer, which will do for a unique
     * id of this server
     * @return
     */
    @Override
    public long getServerId() {
        return self.getId();
    }

    @Override
    public void revalidateSession(ServerCnxn cnxn, long sessionId, int sessionTimeout) throws Exception {
        super.revalidateSession(cnxn, sessionId, sessionTimeout);
        try{
            // setowner as the leader itself, unless update
            // via the follower handlers
            setOwner(sessionId, ServerCnxn.me);
        }catch (KeeperException.SessionExpiredException e){
            // this is ok, it just means that the session revalidation failed
        }
    }
}
