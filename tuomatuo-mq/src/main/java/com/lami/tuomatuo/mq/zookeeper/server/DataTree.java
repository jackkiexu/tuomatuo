package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.*;
import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.Quotas;
import com.lami.tuomatuo.mq.zookeeper.StatsTrack;
import com.lami.tuomatuo.mq.zookeeper.WatchedEvent;
import com.lami.tuomatuo.mq.zookeeper.Watcher;
import com.lami.tuomatuo.mq.zookeeper.ZooDefs;
import com.lami.tuomatuo.mq.zookeeper.common.PathTrie;
import org.apache.jute.Index;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.txn.TxnHeader;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
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

    private void updateQuotaForPath(String path){
        
    }

    private void traverseNode(String path){
        DataNode node = getNode(path);
        String children[] = null;
        synchronized (node){
            Set<String> childs = node.getChildren();
            if(childs != null){
                children = childs.toArray(new String[childs.size()]);
            }
        }

        if(children == null || children.length == 0){
            // this node does not have a child
            // is the leaf node
            // check if its the leaf node
            String endString = "/" + Quotas.limitNode;
            if(path.endsWith(endString)){
                // OK this is the limit node
                // get the real node and update
                // the count and the bytes
                String realPath = path.substring(Quotas.quotaZookeeper.length(),
                        path.indexOf(endString));
                updateQuotaForPath(realPath);
                this.pTrie.addPath(realPath);
            }
            return;
        }

        for(String child : children){
            traverseNode(path + "/" + child);
        }
    }

    /**
     * this method  sets up the path trie and sets up stats for quota nodes
     */
    private void setupQuota(){
        String quotaPath = Quotas.quotaZookeeper;
        DataNode node = getNode(quotaPath);
        if(node == null){
            return;
        }
        traverseNode(quotaPath);
    }


    /**
     * This method uses a stringbuilder to create a new path for children. This is fater than string appends
     * @param oa
     * @param path
     * @throws IOException
     */
    void serializeNode(OutputArchive oa, StringBuilder path) throws IOException{
        String pathString = path.toString();
        DataNode node = getNode(pathString);
        if(node == null){
            return;
        }

        String children[] = null;
        synchronized (node){
            scount++;
            oa.writeString(pathString, "path");
            oa.writeRecord(node, "node");
            Set<String> childs = node.getChildren();
            if(childs != null){
                children = childs.toArray(new String[childs.size()]);
            }
        }

        path.append('/');
        int off = path.length();
        if(children != null){
            for(String child : children){
                /**
                 * Since this is single buffer being resused
                 * we need
                 * to truncate the previous bytes of string
                 */
                path.delete(off, Integer.MAX_VALUE);
                path.append(child);
                serializeNode(oa, path);
            }
        }
    }


    int scount ;

    public boolean initialized = false;

    private void deserializeList(Map<Long, List<ACL>> longKeyMap, InputArchive ia)
            throws IOException{
        int i = ia.readInt("map");
        while(i > 0){
            Long val = ia.readLong("long");
            if(aclIndex < val){
                aclIndex = val;
            }
            List<ACL> aclList = new ArrayList<>();
            Index j = ia.startVector("acls");
            while(!j.done()){
                ACL acl = new ACL();
                acl.deserialize(ia, "acl");
                aclList.add(acl);
                j.incr();
            }

            longKeyMap.put(val, aclList);
            aclKeyMap.put(aclList, val);
            i--;
        }
    }

    private synchronized void serializeList(Map<Long, List<ACL>> longKeyMap,
                                            OutputArchive oa) throws IOException{
        oa.writeInt(longKeyMap.size(), "map");
        Set<Map.Entry<Long, List<ACL>>> set = longKeyMap.entrySet();
        for(Map.Entry<Long, List<ACL>> val : set){
            oa.writeLong(val.getKey(), "long");
            List<ACL> aclList = val.getValue();
            oa.startVector(aclList, "acls");
            for(ACL acl : aclList){
                acl.serialize(oa, "acl");
            }
            oa.endVector(aclList, "acls");
        }
    }


    public void serialize(OutputArchive oa, String tag) throws IOException{
        scount = 0;
        serializeNode(oa, new StringBuilder(""));
        // mark and of stream
        // we need to check if clear and been called in between the snapshot
        if(root != null){
            oa.writeString("/", "path");
        }

    }

    public void deserialize(InputArchive ia, String tag) throws IOException{
        deserializeList(longKeyMap, ia);
        nodes.clear();
        pTrie.clear();
        String path = ia.readString("path");
        while(!path.equals("/")){
            DataNode node = new DataNode();
            ia.readRecord(node, "node");
            nodes.put(path, node);
            int lastSlash = path.lastIndexOf('/');
            if(lastSlash == -1){

            }
            else{
                String parentPath = path.substring(0, lastSlash);
                node.parent = nodes.get(parentPath);
                if(node.parent == null){
                    throw new IOException("Invalid Datatree, unable to find " +
                    " parent " + parentPath + " of path " + path);
                }

                node.parent.addChild(path.substring(lastSlash + 1));
                long eowner = node.stat.getEphemeralOwner();
                if(eowner != 0){
                    HashSet<String> list = ephemerals.get(eowner);
                    if(list == null){
                        list = new HashSet<String>();
                        ephemerals.put(eowner, list);
                    }
                    list.add(path);
                }
            }
            path = ia.readString("path");
        }

        nodes.put("/", root);
        /**
         * we are done with deserializing the
         * the datatree
         * update the quotas - create path tire
         * and also update the stat nodes
         */
        setupQuota();
    }


    /**
     * Summary of the watches on the datatree
     * @param printWriter
     */
    public synchronized void dumpWatchesSummary(PrintWriter printWriter){
        printWriter.print(dataWatches.toString());
    }

    /**
     * Write a text dump of all the watches on the datatree
     * Warning. this is expensive, use sparingly
     * @param printWriter
     * @param byPath
     */
    public synchronized void dumpWatches(PrintWriter printWriter, boolean byPath){
        dataWatches.dumpWatches(printWriter, byPath);
    }


    /**
     * Write a text dump of all the ephemerals in the datatree
     * @param pwriter
     */
    public void dumpEphemerals(PrintWriter pwriter){
        Set<Long> keys = ephemerals.keySet();
        pwriter.println("Session with Ephemerals ("
                + keys.size() + " )");
        for(long k : keys){
            pwriter.print("0x" + Long.toHexString(k));
            pwriter.print(":");
            HashSet<String> tmp = ephemerals.get(k);
            if(tmp != null){
                synchronized (tmp){
                    for(String path : tmp){
                        pwriter.println("\t" + path);
                    }
                }
            }
        }
    }

    public void removeCnxn(Watcher watcher){
        dataWatches.removeWatcher(watcher);
        childWatches.removeWatcher(watcher);
    }

    public void clear(){
        root = null;
        nodes.clear();
        ephemerals.clear();
    }

    public void setWatches(long relativeZxid, List<String> dataWatches,
                           List<String> existWatches, List<String> childWatches,
                           Watcher watcher){
        for(String path : dataWatches){
            DataNode node = getNode(path);
            if(node == null){
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeDeleted,
                        Watcher.Event.KeeperState.SyncConnected, path));
            }
            else if(node.stat.getMzxid() > relativeZxid){
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeDataChanged,
                        Watcher.Event.KeeperState.SyncConnected, path));
            }
            else{
                this.dataWatches.addWatch(path, watcher);
            }
        }

        for(String path : existWatches){
            DataNode node = getNode(path);
            if(node != null){
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeCreated,
                        Watcher.Event.KeeperState.SyncConnected, path));
            }
            else{
                this.dataWatches.addWatch(path, watcher);
            }
        }

        for(String path : childWatches){
            DataNode node = getNode(path);
            if(node == null){
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeDeleted,
                        Watcher.Event.KeeperState.SyncConnected, path));
            }
            else if(node.stat.getPzxid() > relativeZxid){
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeChildrenChanged,
                        Watcher.Event.KeeperState.SyncConnected, path));
            }
            else{
                this.childWatches.addWatch(path, watcher);
            }
        }
    }


    /**
     * This method sets he Cversion and Pzxid fot the specified node to the
     * values passed as arguments. The values are modified only if newCversion
     * is greater than the current Cversion. A NoNodeException is thrown if
     * a znode for the specified path is not found
     * @param path
     * @param newCversion
     * @param zxid
     */
    public void setCversionPzxid(String path, int newCversion, long zxid)
    throws KeeperException.NoNodeException{
        if(path.endsWith("/")){
            path = path.substring(0, path.length() - 1);
        }
        DataNode node = nodes.get(path);
        if(node == null){
            throw new KeeperException.NoNodeException(path);
        }
        synchronized (node){
            if(newCversion == -1){
                newCversion = node.stat.getCversion() + 1;
            }
            if(newCversion > node.stat.getCversion()){
                node.stat.setCversion(newCversion);
                node.stat.setPzxid(zxid);
            }
        }
    }



}
