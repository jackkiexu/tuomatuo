package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.Quotas;
import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.common.PathTrie;
import org.apache.jute.Record;
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







    public volatile long lastProcessedZxid = 0;

    public org.apache.zookeeper.server.DataTree.ProcessTxnResult processTxn(TxnHeader header, Record txn) {
        return null;
    }



}
