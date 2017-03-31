package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.*;
import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.Quotas;
import com.lami.tuomatuo.mq.zookeeper.StatsTrack;
import com.lami.tuomatuo.mq.zookeeper.Watcher;
import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.common.PathTrie;
import org.apache.jute.Record;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.txn.TxnHeader;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class maintains the tree data structure. It doesn't have any networking
 * or client connection code in it so that it can be tested in a stand alone
 * way
 * The tree maintains two parallel data structures: a hashtable that maps from
 * full paths to DataNodes and a tree of DataNodes. All access to a path is
 * through the hashtable. The tree is traversed only when serializing to disk
 *
 * Created by xujiankang on 2017/3/19.
 */
public class DataTree {

    private static final Logger LOG = LoggerFactory.getLogger(DataTree.class);

    /**
     * This hastable provides a fast lookup to the datanodes. The tree is the
     * source of truth and is where all the locking occurs
     */
    private final ConcurrentHashMap<String, DataNode> nodes = new ConcurrentHashMap<>();

    private final WatchManager dataWatches = new WatchManager();

    private final WatchManager childWatches = new WatchManager();

    /** the root of zookeeper tree */
    private static final String rootZookeeper = "/";

    /** the zookeeper nodes that acts as the management and status node */
    private static final String procZookeeper = Quotas.procZookeeper;

    /** this will be the string thats stored as a child of root */
    private static final String procChildZooKeeper = procZookeeper.substring(1);

    /**
     * the zookeeper quota node that acts as the quota management node for
     * zookeeper
     */
    private static final String quotaZooKeeper = Quotas.quotaZookeeper;

    /** this will be the string thats stored as a child of /zookeeper */
    private static final String quotaChildZooKeeper = quotaZooKeeper.substring(procZookeeper.length() + 1);

    /** the zookeeper config node that acts as the config management node for
     * zookeeper */
    private static final String configZookeeper = ZooDefs.CONFIG_DONE;

    /** this will be the string thats stored as a child of /zookeeper  */
    private static final String configChildZooKeeper = configZookeeper.substring(procZookeeper.length() + 1);

    /**
     * The path trie that keeps track of the quota nodes in this data tree
     */
    private final PathTrie pTrie = new PathTrie();

    /** the hashtable lists the paths of the ephemeral nodes of a session */
    private final Map<Long, HashSet<String>> ephemerals = new ConcurrentHashMap<>();

    /** this set contains the paths of all container nodes */
    // 这里使用了桥接模式
    private final Set<String> containers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    /** This set contains tha path of all ttl nodes */
    private final Set<String> ttls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private final ReferenceCountedACLCache aclCache = new ReferenceCountedACLCache();


    public Set<String> getEphemerals(long sessionId){
        HashSet<String> retv = ephemerals.get(sessionId);
        if(retv == null){
            return new HashSet<>();
        }

        HashSet<String> cloned = null;
        synchronized (retv){
            cloned = (HashSet<String>)retv.clone();
        }
        return cloned;
    }


    public Set<String> getContainers(){
        return new HashSet<String>(containers);
    }

    public Set<String> getTtls(){
        return new HashSet<>(ttls);
    }

    public Collection<Long> getSessions(){
        return ephemerals.keySet();
    }

    public DataNode getNode(String path){
        return nodes.get(path);
    }

    public int getNodeCount(){
        return nodes.size();
    }


    public int getWatchCount(){
        return dataWatches.size() + childWatches.size();
    }



    /**
     * If there is a quota set, return the appropriate prefix for that quota
     * Else return null
     * @param path
     * @return
     */
    public String getMaxPrefixWithQuota(String path){
        /**
         * do nothing for the root
         * we are not keeping a quota on the zookeeper
         * root node for now
         */
        String lastPrefix = pTrie.findMaxPrefix(path);
        if(!rootZookeeper.equals(lastPrefix) && !("".equals(lastPrefix))){
            return lastPrefix;
        }else{
            return null;
        }
    }

    /**
     * Update the count of this stat datanode
     * @param lastPrefix
     * @param diff
     */
    public void updateCount(String lastPrefix, int diff){
        String statNode = Quotas.statPath(lastPrefix);
        DataNode node = nodes.get(statNode);
        StatsTrack updatedStat = null;
        if(node == null){
            // should not happen
            LOG.info("Missing count node for stat " + statNode);
            return;
        }
        synchronized (node){
            updatedStat = new StatsTrack(new String(node.data));
            updatedStat.setCount(updatedStat.getCount() + diff);
            node.data = updatedStat.toString().getBytes();
        }
        // now check if the counts match the quota
        String quotaNode = Quotas.quotaPath(lastPrefix);
        node = nodes.get(quotaNode);
        StatsTrack thisStats = null;
        if(node == null){
            LOG.info("Missing count node for quota " + quotaNode);
            return;
        }

        synchronized (node){
            thisStats = new StatsTrack(new String(node.data));
        }
    }

    /**
     * Update the count of bytes of this stat datanode
     * @param lastPrefix
     * @param diff
     */
    public void updateBytes(String lastPrefix, long diff){
        String statNode = Quotas.statPath(lastPrefix);
        DataNode node = nodes.get(statNode);
        if(node == null){
            /**
             * should never be null but just to make
             * findbugs happy
             */
        }
        StatsTrack updatedStat = null;
        synchronized (node){
            updatedStat = new StatsTrack(new String(node.data));
            updatedStat.setBytes(updatedStat.getBytes() + diff);
            node.data = updatedStat.toString().getBytes();
        }

        // now check if the bytes metch the quota
        String quotaNode = Quotas.quotaPath(lastPrefix);
        node = nodes.get(quotaNode);
        if(node == null){
            // should never be null but just to make
            // findbugs happy
            LOG.info("Missing quota node for bytes " + quotaNode);
            return;
        }
        StatsTrack thisStats = null;
        synchronized (node){
            thisStats = new StatsTrack(new String(node.data));
        }

    }

    /**
     * Remove the path from the datatree
     * @param path the path to the node to be deleted
     * @param zxid the current zxid
     * @throws KeeperException.NoNodeException
     */
    public void deleteNode(String path, long zxid) throws KeeperException.NoNodeException{
        int lastSlash = path.lastIndexOf("/");
        String parentName = path.substring(0, lastSlash);
        String childName = path.substring(lastSlash + 1);
        DataNode node = nodes.get(path);

        if(node == null){
            throw new KeeperException.NoNodeException();
        }

        nodes.remove(path);
        DataNode parent = nodes.get(parentName);
        if(parent == null){
            throw new KeeperException.NoNodeException();
        }

        synchronized (parent){
            parent.removeChild(childName);
            parent.stat.setPzxid(zxid);
            long eowner = node.stat.getEphemeralOwner();
            if(eowner != 0){
                HashSet<String> nodes = ephemerals.get(eowner);
                if(nodes != null){
                    synchronized (nodes){
                        nodes.remove(path);
                    }
                }
            }

            node.parent = null;
        }

        if(parentName.startsWith(procZookeeper)){
            // delete the node in the trie
            if(Quotas.limitNode.equals(childName)){
                // we need to update the trie
                // as well
                pTrie.deletePath(parentName.substring(quotaZooKeeper.length()));
            }
        }

        // also check to update the quotas for this node
        String lastPrefix;
        if((lastPrefix = getMaxPrefixWithQuota(path)) != null){
            // ok we have some math and need to update
            updateCount(lastPrefix, -1);
            int bytes = 0;
            synchronized (node){
                bytes = (node.data == null? 0 : -(node.data.length));
            }
            updateBytes(lastPrefix, bytes);
        }

        childWatches.triggerWatch(path, Watcher.Event.EventType.NodeDeleted, processed);
        childWatches.triggerWatch(parentName.equals("")? "/" : parentName, Watcher.Event.EventType.NodeChildrenChanged);
    }

    void killSession(long session, long zxid){
        /**
         * the list is already removed from the ephemerals
         * so we do not have to worry about synchronizing on
         * the list. This is only called from FinalRequestProcessor
         * so there is no need for synchronization The list is not
         * changed here. Only create and delete change the list which
         * are agein called from FinalRequestProcessor in sequence
         */
        HashSet<String> list = ephemerals.remove(session);
        if(list != null){
            for(String path : list){
                try{
                    deleteNode(path, zxid);
                    LOG.info("Deleting ephemeral node " + path
                                + " for session 0X"
                                + Long.toHexString(session));
                }catch (KeeperException.NoNodeException e){
                    LOG.warn("Ignoring NoNodeException for path " + path
                            + " while removing ephemeral for dead session 0x"
                            + Long.toHexString(session));
                }
            }
        }
    }



    public volatile long lastProcessedZxid = 0;

    public org.apache.zookeeper.server.DataTree.ProcessTxnResult processTxn(TxnHeader header, Record txn) {
        return null;
    }

    /**
     * a encapsultaing class for return value
     */
    private static class Counts {
        long bytes;
        int count;
    }

    static public class ProcessTxnResult{
        public long clientId;
        public int cxid;
        public long zxid;
        public int err;
        public int type;
        public String path;
        public Stat stat;

        public List<ProcessTxnResult> multiResult;

        /**
         * Equality is defined as the clientId and the cxid being the same. This
         * allows us to use hash tables to track completion of transactions.
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            if (o instanceof ProcessTxnResult) {
                ProcessTxnResult other = (ProcessTxnResult) o;
                return other.clientId == clientId && other.cxid == cxid;
            }
            return false;
        }

        /**
         * See equals() to find the rational for how this hashcode is generated.
         *
         * @see ProcessTxnResult#equals(Object)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return (int) ((clientId ^ cxid) % Integer.MAX_VALUE);
        }
    }


}
