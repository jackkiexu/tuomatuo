package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.Quotas;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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




    public volatile long lastProcessedZxid = 0;

    public org.apache.zookeeper.server.DataTree.ProcessTxnResult processTxn(TxnHeader header, Record txn) {
        return null;
    }



}
