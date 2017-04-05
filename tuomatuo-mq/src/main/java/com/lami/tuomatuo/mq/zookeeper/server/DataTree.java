package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.*;
import com.lami.tuomatuo.mq.zookeeper.KeeperException;
import com.lami.tuomatuo.mq.zookeeper.Op;
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
import org.apache.zookeeper.data.StatPersisted;
import org.apache.zookeeper.proto.DeleteRequest;
import org.apache.zookeeper.txn.*;
import org.apache.zookeeper.txn.TxnHeader;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
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

    /**
     * This is map from longs to acl's It saves acl's being stored for each
     * datanode
     */
    public final Map<Long, List<ACL>> longKeyMap = new HashMap<>();

    // this is a map from acls to long
    public final Map<List<ACL>, Long> aclKeyMap = new HashMap<>();

    /**
     * these are number of acls that we have in the datatree
     */
    protected long aclIndex = 0;

    public HashSet<String> getEphemerals(long sessionId){
        HashSet<String> retv = ephemerals.get(sessionId);
        if(retv == null){
            return new HashSet<String>();
        }
        HashSet<String> cloned = null;
        synchronized (retv){
            cloned = (HashSet<String>)retv.clone();
        }
        return cloned;
    }

    public Map<Long, HashSet<String>> getEphemeralsMap(){
        return ephemerals;
    }

    private long incrementIndex(){
        return ++aclIndex;
    }

    /**
     * compare two list of acls. if there elements are in the same order and the
     * same size then return true else return false
     *
     * @param lista
     *            the list to be compared
     * @param listb
     *            the list to be compared
     * @return true if and only if the lists are of the same size and the
     *         elements are in the same order in lista and listb
     */
    private boolean listACLEquals(List<ACL> lista, List<ACL> listb) {
        if (lista.size() != listb.size()) {
            return false;
        }
        for (int i = 0; i < lista.size(); i++) {
            ACL a = lista.get(i);
            ACL b = listb.get(i);
            if (!a.equals(b)) {
                return false;
            }
        }
        return true;
    }

    public synchronized Long convertAcls(List<ACL> acls){
        if(acls == null){
            return -1L;
        }
        // get the value from the map
        Long ret = aclKeyMap.get(acls);
        // counld not find the map
        if(ret != null){
            return ret;
        }
        long val = incrementIndex();
        longKeyMap.put(val, acls);
        aclKeyMap.put(acls, val);
        return val;
    }


    public synchronized List<ACL> convertLong(Long longVal){
        if(longVal == null){
            return null;
        }
        if(longVal == -1L){
            return ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }
        List<ACL> acls = longKeyMap.get(longVal);
        if(acls == null){
            LOG.error("ERROR: ACL not available for long " + longVal);
            throw new RuntimeException("Failed to fetch acls for " + longVal);
        }
        return acls;

    }

    public Collection<Long> getSessions(){
        return ephemerals.keySet();
    }

    /**
     * Just ac accessor method to allow raw creation of datatree's from a bunch
     * @param path the path of the datanode
     * @param node the data node corresponding to this path
     */
    public void addDataNode(String path, DataNode node){
        nodes.put(path, node);
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

    public int getEphemeralsCount(){
        Map<Long, HashSet<String>> map = this.getEphemeralsMap();
        int result = 0;
        for(HashSet<String> set : map.values()){
            result += set.size();
        }
        return result;
    }

    /**
     * 获取 大概的 节点数据大小
     * Get the size of the nodes based on path and data length
     * @return
     */
    public long approximateDataSize(){
        long result = 0;
        for(Map.Entry<String, DataNode> entry : nodes.entrySet()){
            DataNode value = entry.getValue();
            synchronized (value){
                result += entry.getKey().length();
                result += (value.data == null ? 0 : value.data.length);
            }
        }
        return result;
    }

    /**
     * This is a pointer to the root of the DataTree. It is the source of truth
     * but we usually use the nodes hashmap to find nodes in the trees
     */
    private DataNode root = new DataNode(null, new byte[0], -1L, new StatPersisted());
    /**
     * create a / zookeeper filesystem that is the proc filesystem of zookeeper
     */
    public DataNode procDataNode = new DataNode(root, new byte[0], -1L, new StatPersisted());

    /** create a /zookeeper/quota node for maintaining quota properties for zookeeper */
    private DataNode quotaDataNode = new DataNode(procDataNode, new byte[0], -1L, new StatPersisted());

    public DataTree() {
        /** Rather than fight it, let root have an alias */
        nodes.put("", root);
        nodes.put(rootZookeeper, root);

        /** add the proc node and quota node */
        root.addChild(procChildZooKeeper);
        nodes.put(procZookeeper, procDataNode);

        procDataNode.addChild(quotaChildZooKeeper);
        nodes.put(quotaZooKeeper, quotaDataNode);
    }

    /**
     * is the path one of the special paths owned by zookeeper
     * @param path the path to be
     * @return
     */
    boolean isSpecialPath(String path){
        if(rootZookeeper.equals(path) || procZookeeper.equals(path)
                || quotaZooKeeper.equals(path)){
            return true;
        }
        return false;
    }

    static public void copyStatPersisted(StatPersisted from, StatPersisted to) {
        to.setAversion(from.getAversion());
        to.setCtime(from.getCtime());
        to.setCversion(from.getCversion());
        to.setCzxid(from.getCzxid());
        to.setMtime(from.getMtime());
        to.setMzxid(from.getMzxid());
        to.setPzxid(from.getPzxid());
        to.setVersion(from.getVersion());
        to.setEphemeralOwner(from.getEphemeralOwner());
    }

    static public void copyStat(Stat from, Stat to) {
        to.setAversion(from.getAversion());
        to.setCtime(from.getCtime());
        to.setCversion(from.getCversion());
        to.setCzxid(from.getCzxid());
        to.setMtime(from.getMtime());
        to.setMzxid(from.getMzxid());
        to.setPzxid(from.getPzxid());
        to.setVersion(from.getVersion());
        to.setEphemeralOwner(from.getEphemeralOwner());
        to.setDataLength(from.getDataLength());
        to.setNumChildren(from.getNumChildren());
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

    public String createNode(String path, byte data[], List<ACL> acl,
                             long ephemeralOwner, int parentCVersion, long zxid, long time) throws KeeperException.NoNodeException, KeeperException.NodeExistsException{
        int lastSlash = path.lastIndexOf("/");
        String parentName = path.substring(0, lastSlash);
        String childName = path.substring(lastSlash + 1);
        StatPersisted stat = new StatPersisted();
        stat.setCtime(time);
        stat.setMtime(time);
        stat.setCzxid(zxid);
        stat.setMzxid(zxid);
        stat.setPzxid(zxid);
        stat.setVersion(0);
        stat.setAversion(0);
        stat.setEphemeralOwner(ephemeralOwner);
        // 获取 parent 的 DataNode
        DataNode parent = nodes.get(parentName);
        if(parent == null){
            throw new KeeperException.NoNodeException();
        }
        synchronized (parent){
            Set<String> children = parent.getChildren();
            if(children != null){
                if(children.contains(childName)){
                    throw new KeeperException.NodeExistsException();
                }
            }
            // parentde CVersion ++;
            if(parentCVersion == -1){ // 若没有传 parentCVersion
                parentCVersion = parent.stat.getCversion();
                parentCVersion++;
            }

            parent.stat.setCversion(parentCVersion);
            parent.stat.setPzxid(zxid);
            Long longval = convertAcls(acl);
            DataNode child = new DataNode(parent, data, longval, stat);
            parent.addChild(childName);
            nodes.put(path, child);
            if(ephemeralOwner != 0){
                HashSet<String> list = ephemerals.get(ephemeralOwner);
                if(list == null){
                    list = new HashSet<String>();
                    ephemerals.put(ephemeralOwner, list);
                }
                synchronized (list){
                    list.add(path);
                }
            }
        }

        // 若涉及 quota 则会加入 trie (前缀树)
        // now check if its one of the zookeeper node child
        if(parentName.startsWith(quotaZooKeeper)){
            // now check if its the limit node
            if(Quotas.limitNode.equals(childName)){
                // this is the limit node
                // get the parent and add it to the trie
                pTrie.addPath(parentName.substring(quotaZooKeeper.length()));
            }
            if(Quotas.statNode.equals(childName)){
                updateQuotaForPath(parentName.substring(quotaZooKeeper.length()));
            }
        }

        // also check to update the quotas for this node
        String lastPrefix;
        // 若是 quota 的, 则更新对应的 count, bytes
        if((lastPrefix = getMaxPrefixWithQuota(path)) != null){
            // ok we have some match and need to update
            updateCount(lastPrefix, 1);
            updateBytes(lastPrefix, data == null ? 0 : data.length);
        }

        // 触发 watch 事件
        dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeCreated);
        childWatches.triggerWatch(parentName.equals("") ? "/" : parentName, Watcher.Event.EventType.NodeChildrenChanged);
        return path;
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

        Set<Watcher> processed = dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeDeleted);
        childWatches.triggerWatch(path, Watcher.Event.EventType.NodeDeleted, processed);
        childWatches.triggerWatch(parentName.equals("")? "/" : parentName, Watcher.Event.EventType.NodeChildrenChanged);
    }


    public Stat setData(String path, byte data[], int version, long zxid, long time) throws KeeperException.NoNodeException{
        Stat stat = new Stat();
        DataNode node = nodes.get(path);
        if(node == null){
            throw new KeeperException.NoNodeException();
        }

        byte lastdata[] = null;
        synchronized (node){
            lastdata = node.data;
            node.data = data;
            node.stat.setMtime(time);
            node.stat.setMzxid(zxid);
            node.stat.setVersion(version);
            node.copyStat(stat);
        }

        // now update if the path is in a quota subtree
        String lastPrefix;
        if((lastPrefix = getMaxPrefixWithQuota(path)) != null){
            this.updateBytes(lastPrefix, (data == null ? 0 : data.length) - (lastdata == null ? 0 : lastdata.length));
        }

        dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeDataChanged);
        return stat;
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


    public byte[] getData(String path, Stat stat, Watcher watcher) throws KeeperException.NoNodeException{
        DataNode node = nodes.get(path);
        if(node == null){
            throw new KeeperException.NoNodeException();
        }
        synchronized (node){
            node.copyStat(stat);
            if(watcher != null){
                dataWatches.addWatch(path, watcher);
            }
            return node.data;
        }
    }


    public Stat statNode(String path, Watcher watcher) throws KeeperException.NoNodeException{
        Stat stat = new Stat();
        DataNode n = nodes.get(path);
        if(watcher != null){
            dataWatches.addWatch(path, watcher);
        }
        if(n == null){
            throw new KeeperException.NoNodeException();
        }
        synchronized (n){
            n.copyStat(stat);
            return stat;
        }
    }

    public List<String> getChildren(String path, Stat stat, Watcher watcher) throws KeeperException.NoNodeException{
        DataNode n = nodes.get(path);
        if(n == null){
            throw new KeeperException.NoNodeException();
        }
        synchronized (n){
            if(stat != null){
                n.copyStat(stat);
            }
            ArrayList<String> children;
            Set<String> childs = n.getChildren();
            if(childs != null){
                children = new ArrayList<>(childs.size());
                children.addAll(childs);
            }else {
                children = new ArrayList<>();
            }

            if(watcher != null){
                childWatches.addWatch(path, watcher);
            }
            return children;
        }
    }

    public Stat setACL(String path, List<ACL> acl, int version) throws KeeperException.NoNodeException{
        Stat stat = new Stat();
        DataNode n = nodes.get(path);
        if(n == null){
            throw new KeeperException.NoNodeException();
        }
        synchronized (n){
            n.stat.setVersion(version);
            n.acl = convertAcls(acl);
            n.copyStat(stat);
            return stat;
        }
    }

    public List<ACL> getACL(String path, Stat stat) throws KeeperException.NoNodeException{
        DataNode n = nodes.get(path);
        if(n == null){
            throw new KeeperException.NoNodeException();
        }
        synchronized (n){
            n.copyStat(stat);
            return new ArrayList<ACL>(convertLong(n.acl));
        }
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

    public volatile long lastProcessedZxid = 0;

    /**
     * 根据客户端的请求, 根据事件类型来进行处理
     */
    public ProcessTxnResult processTxn(TxnHeader header, Record txn) {
        ProcessTxnResult rc = new ProcessTxnResult();

        try{
            rc.clientId = header.getClientId();
            rc.cxid = header.getCxid(); // 创建时的zxid
            rc.zxid = header.getZxid(); // 服务端处理的最新的 zxid
            rc.type = header.getType();
            rc.err = 0;
            rc.multiResult = null;

            switch (header.getType()){
                case ZooDefs.OpCode.create:
                    CreateTxn createTxn = (CreateTxn) txn;
                    rc.path = createTxn.getPath();
                    createNode(createTxn.getPath(),
                            createTxn.getData(),
                            createTxn.getAcl(),
                            createTxn.getEphemeral() ? header.getClientId() : 0,
                            createTxn.getParentCVersion(),
                            header.getZxid(), header.getTime());
                    break;
                case ZooDefs.OpCode.delete:
                    DeleteTxn deleteTxn = (DeleteTxn)txn;
                    rc.path = deleteTxn.getPath();
                    deleteNode(deleteTxn.getPath(), header.getZxid());
                    break;
                case ZooDefs.OpCode.setData:
                    SetDataTxn setDataTxn = (SetDataTxn)txn;
                    rc.path = setDataTxn.getPath();
                    rc.stat = setData(setDataTxn.getPath(), setDataTxn.getData(),
                            setDataTxn.getVersion(), header.getZxid(), header.getTime());
                    break;
                case ZooDefs.OpCode.setACL:
                    SetACLTxn setACLTxn = (SetACLTxn)txn;
                    rc.path = setACLTxn.getPath();
                    rc.stat = setACL(setACLTxn.getPath(), setACLTxn.getAcl(), setACLTxn.getVersion());
                    break;
                case ZooDefs.OpCode.closeSession:
                    killSession(header.getClientId(), header.getZxid());
                    break;
                case ZooDefs.OpCode.error:
                    ErrorTxn errorTxn = (ErrorTxn)txn;
                    rc.err = errorTxn.getErr();
                    break;
                case ZooDefs.OpCode.check:
                    CheckVersionTxn checkVersionTxn = (CheckVersionTxn)txn;
                    rc.path = checkVersionTxn.getPath();
                    break;
                case ZooDefs.OpCode.multi:
                    MultiTxn multiTxn = (MultiTxn)txn;
                    List<Txn> txns = multiTxn.getTxns();
                    rc.multiResult = new ArrayList<>();
                    boolean failed = false;

                    for(Txn subtxn : txns){
                        if(subtxn.getType() == ZooDefs.OpCode.error){
                            failed = true;
                            break;
                        }
                    }

                    boolean post_failed = failed;
                    for(Txn subtxn : txns){
                        ByteBuffer bb = ByteBuffer.wrap(subtxn.getData());
                        Record record = null;
                        switch (subtxn.getType()){
                            case ZooDefs.OpCode.create:
                                record = new CreateTxn();
                                break;
                            case ZooDefs.OpCode.delete:
                                record = new DeleteTxn();
                                break;
                            case ZooDefs.OpCode.setData:
                                record = new SetDataTxn();
                                break;
                            case ZooDefs.OpCode.error:
                                record = new ErrorTxn();
                                post_failed = true;
                                break;
                            case ZooDefs.OpCode.check:
                                record = new CheckVersionTxn();
                                break;
                            default:
                                throw new IOException("Invalid type of op:" + subtxn.getType());
                        }

                        assert (record != null);
                        ByteBufferInputStream.byteBuffer2Record(bb, record);

                        if(failed && subtxn.getType() != ZooDefs.OpCode.error){
                            int ec = post_failed ? KeeperException.Code.RUNTIMEINCONSISTENCY.intValue() : KeeperException.Code.OK.intValue();
                            subtxn.setType(ZooDefs.OpCode.error);
                            record = new ErrorTxn();
                        }

                        if(failed){
                            assert (subtxn.getType() == ZooDefs.OpCode.error);
                        }

                        TxnHeader subHdr = new TxnHeader(header.getClientId(), header.getCxid(),
                                                    header.getZxid(), header.getTime(),
                                subtxn.getType());

                        ProcessTxnResult subRc = processTxn(subHdr, record);
                        rc.multiResult.add(subRc);
                        if(subRc.err != 0 && rc.err == 0){
                            rc.err = subRc.err;
                        }
                    }
                    break;
            }

        }catch (KeeperException e){
            LOG.info("Failed :" + header + " : " + txn, e);
            rc.err = e.code().intValue();
        }catch (Exception e){
            LOG.info("Failed :" + header + " : " + txn, e);
        }

        /**
         *A snpashot might be in process while we are modifying the data
         * tree. If we set lastProcessedZxid prior to making corresponding
         * change to the tree, then the zxid associated with the snapshot
         * file will be ahead of its contents, Thus, while restoring from
         * the snapshot, the restore method will not apply the transaction
         * for zxid assocaited with the snapshot file, since the restore
         * method assumes that transaction to be present in the snapshot
         *
         * To avoid this, we first apply the transaction and then modify
         * lastProcessedZxid, During restore we correctly handle the
         * case where the snapshot contains data ahead of the zxid associated
         * with the file
         */
        if(rc.zxid > lastProcessedZxid){
            lastProcessedZxid = rc.zxid;
        }

        /**
         * Snapshots are taken lazily It can happen that child
         * znodes of a parent are created after the parent
         * is serialized Therefore, which replaying logs during restore, a
         * create might fail because the nodes was already
         * created
         *
         * After seeing this failure, we should increment
         * the cversion of the parent znode since parent was serialzied
         * before its children
         *
         * Note, such failure on DT should be seen only during restore
         */
        if(header.getType() == ZooDefs.OpCode.create &&
                rc.err == KeeperException.Code.NODEEXISTS.intValue()){
            LOG.debug("Adjusting parent cversion for Txn :" + header.getType() +
            " path:" + rc.path + " error "+ rc.err);

            int lastSlash = rc.path.lastIndexOf("/");
            String parentName = rc.path.substring(0, lastSlash);
            CreateTxn cTxn = (CreateTxn)txn;

            try{
                setCversionPzxid(parentName, cTxn.getParentCVersion(), header.getZxid());
            }catch (KeeperException.NoNodeException e){
                LOG.error("Failed to set parent cversion for:" + parentName, e);
                rc.err = e.code().intValue();
            }
        }else if(rc.err != KeeperException.Code.OK.intValue()){
            LOG.info("Ignoring processTxn failure hdr : " + header.getType());
        }


        return rc;
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

    /**
     * a encapsultaing class for return value
     */
    private static class Counts {
        long bytes;
        int count;
    }

    /**
     * this method gets the count of nodes and the bytes under a subtree
     */
    private void getCounts(String path, Counts counts){
        DataNode node = getNode(path);
        if(node == null){
            return;
        }

        String[] children = null;
        int len = 0;
        synchronized (node){
            Set<String> childs = node.getChildren();
            if(childs != null){
                children = childs.toArray(new String[childs.size()]);
            }
            len = (node.data == null ? 0 : node.data.length);
        }

        counts.count += 1;
        counts.bytes += len;
        if(children == null || children.length == 0){
            return;
        }

        for(String child : children){
            getCounts(path + "/" + child, counts);
        }
    }

    private void updateQuotaForPath(String path){
        Counts c = new Counts();
        getCounts(path, c);
        StatsTrack stack = new StatsTrack();
        stack.setBytes(c.bytes);
        stack.setCount(c.count);
        String statPath = Quotas.quotaZookeeper + path + "/" + Quotas.statNode;
        DataNode node = getNode(statPath);
        // it should exist
        if(node == null){
            LOG.info("Missing quota stat node " + statPath);
            return;
        }

        synchronized (node){
            node.data = stack.toString().getBytes();
        }
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

    /** this set contains the paths of all container nodes */
    // 这里使用了桥接模式
    private final Set<String> containers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    /** This set contains tha path of all ttl nodes */
    private final Set<String> ttls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    private final ReferenceCountedACLCache aclCache = new ReferenceCountedACLCache();

    public Set<String> getContainers(){
        return new HashSet<String>(containers);
    }

    public Set<String> getTtls(){
        return new HashSet<>(ttls);
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
