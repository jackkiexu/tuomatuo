package com.lami.tuomatuo.search.base.concurrent.tree.btree;

import lombok.Data;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * 代码说明
 * http://blog.csdn.net/wangpingfang/article/details/7426943
 * BTree 图形实例
 *
 * http://blog.csdn.net/whos2002110/article/details/37689857
 * http://blog.csdn.net/wangpingfang/article/details/7426943
 * http://www.cnblogs.com/wglspark/p/5146612.html
 *
 * Created by xujiankang on 2017/1/7.
 */
public class KBTree<K, V> {

    private static final Logger logger = Logger.getLogger(KBTree.class);

    /**
     * B树中的键值对
     * @param <K>
     * @param <V>
     */
    @Data
    static final class Entry<K, V>{
        private K key;
        private V value;

        public Entry(K k, V v){
            this.key = k;
            this.value = v;
        }
    }

    /**
     * 在B树中搜索给定键值的返回结果
     * 该结果有两部组成, 第一部分表示此次查找是否成功
     *  1. 成功, 第二部分表示给定键值在 B 树节点中的位置
     *  2. 查找失败, 第二部分表示给定键值应该插入的位置
     *
     * @param <V>
     */
    @Data
    static final class SearchResult<V>{
        private boolean exist; // key对应的 entry 是否存在
        private int index; // index对应 entrys 的位置
        private V value; // key 对应的值

        public SearchResult(boolean exist, int index){
            this.exist = exist;
            this.index = index;
        }

        public SearchResult(boolean exist, int index, V value){
            this(exist, index);
            this.value = value;
        }
    }

    /**
     * B树中的节点
     * @param <K>
     * @param <V>
     */
    @Data
    static final class BTreeNode<K, V>{
        /** 节点的项, 按键非降顺存放 */
        private List<Entry<K, V>> entrys;
        /** 内节点的子节点 */
        private List<BTreeNode<K, V>> children;
        /** 是否为叶子节点 */
        private boolean leaf;
        /** 键的比较函数 */
        private Comparator<K> kComparator;

        public BTreeNode(){
            entrys = new ArrayList<Entry<K, V>>();
            children = new ArrayList<BTreeNode<K, V>>();
            leaf = false;
        }

        public BTreeNode(Comparator<K> kComparator){
            this();
            this.kComparator = kComparator;
        }

        public int size(){
            return entrys.size();
        }

        public int compare(K key1, K key2){
            return kComparator == null? ((Comparable<K>)key1).compareTo(key2) : kComparator.compare(key1, key2);
        }

        /**
         * 在节点中查找给定的键
         * 如果节点中存在给定的键, 则返回一个{@code SearchResult}.
         * 标志此次查找成功, 给定的键在节点中的索引和给定的键关联的值;
         * 如果不存在, 则返回 {@code SearchResult}
         * 标识此次查找失败, 给定键应该插入的位置, 该键的关联值为 null
         *
         * <p>
         *     如果查找失败, 返回结果中的索引域为 [0, {@link #size()}];
         *     如果查找成功, 返回结果中的索引域为 [0, {@link #size() - 1}]
         * </p>
         * 这是一个二分查找算法, 可以保证时间复杂度为0(log(t)).
         *
         * @param key - 给定的键值
         * @return － 查找的结果
         */
        public SearchResult<V> searchKey(K key){
            int index = 0;
            boolean result = false;
            for(Entry<K, V> entry : entrys){
                if(entry.getKey() != null && entry.getKey().equals(key)){
                    result =  true;
                    return new SearchResult<V>(result, index, entry.getValue());
                }
                index++;
            }
            return new SearchResult<V>(result, index, null);
        }

        /**
         * 将给定的项追加到节点的末尾
         * 你需要自己确保调用该方法之后, 节点中的项还是
         * 按照关键字非降序存放
         *
         * @param entry - 给定的项
         */
        public void addEntry(Entry<K, V> entry){
            entrys.add(entry);
        }

        /**
         * 删除给定索引的 entry
         * @param index
         * @return
         */
        public Entry<K, V> removeEntry(int index){
            return entrys.remove(index);
        }

        /**
         * 得到节点中给定索引的项
         *
         * @param index
         * @return
         */
        public Entry<K, V> entryAt(int index){
            return entrys.get(index);
        }

        /**
         * 如果节点中存在给定的键, 则更新其关联的值
         *
         * @param entry
         * @return null 如果节点之前不存在给定的键, 否则返回给定键之前关联的值
         */
        public V putEntry(Entry<K, V> entry){
            SearchResult<V> result = searchKey(entry.getKey());
            if(result.isExist()){
                V oldValue = entrys.get(result.getIndex()).getValue();
                entrys.get(result.getIndex()).setValue(entry.getValue());
                return oldValue;
            }else{
                insertEntry(entry, result.getIndex());
                return null;
            }
        }

        /**
         * 在该节点中插入给定的项
         *
         * @param entry
         * @return
         */
        public boolean insertEntry(Entry<K, V> entry){
            SearchResult<V> result = searchKey(entry.getKey());
            if(result.isExist()){
                return false;
            }else{
                insertEntry(entry, result.getIndex());
                return true;
            }
        }

        /**
         * 在该节点中给定索引的位置插入给定的项
         * 你需要自己保证项插入到正确的位置
         */
        public void insertEntry(Entry<K, V> entry, int index){
            List<Entry<K, V>> newEntrys = new ArrayList<>();
            int i = 0;
            for(; i < index; ++i){
                newEntrys.add(entrys.get(i));
            }
            newEntrys.add(entry);
            for(;i < entrys.size(); ++i){
                newEntrys.add(entrys.get(i));
            }
            entrys.clear();
            entrys = newEntrys;
        }

        /**
         * 返回节点中给定索引的子节点
         * @param index
         * @return
         */
        public BTreeNode<K, V> childAt(int index){
            if(isLeaf()){
                throw new UnsupportedOperationException("Leaf node doesn't have children");
            }
            return children.get(index);
        }

        /**
         * 将给定的子节点追加到该节点的末尾
         * @param child
         */
        public void addChild(BTreeNode<K, V> child){
            children.add(child);
        }

        /**
         * 删除该节点中给定索引位置的子节点
         * 你需要自己保证给定的索引是合法的
         *
         * @param index
         */
        public void removeChild(int index){
            children.remove(index);
        }

        /**
         * 将给定的子节点插入到该节点中给定索引
         * 的位置
         *
         * @param child - 给定的子节点
         * @param index - 子节点带插入的位置
         */
        public void insertChild(BTreeNode<K, V> child, int index){
            List<BTreeNode<K, V>> newChildren = new ArrayList<>();
            int i = 0;
            for(;i < index; ++i){
                newChildren.add(children.get(i));
            }
            newChildren.add(child);
            for(;i < children.size(); ++i){
                newChildren.add(children.get(i));
            }
            children = newChildren;
        }

    }

    private static final int DEFAULT_T = 2;

    /** B树的根节点 */
    private BTreeNode<K, V> root;
    /** 根据B树的定义, B树的每个非根节点的关键字数n满足 (t - 1) <= n <= (2t - 1) */
    private int t = DEFAULT_T;
    /** 非根节点中最小的键值数 */
    private int minKeySize = t - 1;
    /** 非根节点中最大的键值数 */
    private int maxKeySize = 2*t -1;
    /** 键的比较函数对象 */
    private Comparator<K> kComparator;

    /** 构造一颗B树, 键值采用自然排序方式 */
    public KBTree(){
        root = new BTreeNode<K, V>();
        root.setLeaf(true);
    }

    public KBTree(int t){
        this();
        this.t = t;
        minKeySize = t - 1;
        maxKeySize = 2*t -1;
    }

    /** 以给定的键值比较函数对象构造一颗B树 */
    public KBTree(Comparator<K> kComparator){
        root = new BTreeNode<K, V>(kComparator);
        root.setLeaf(true);
        this.kComparator = kComparator;
    }

    public KBTree(Comparator<K> kComparator, int i){
        this(kComparator);
        this.t = t;
        minKeySize = t - 1;
        maxKeySize = 2*t - 1;
    }

    int compare(K key1, K key2){
        return kComparator == null ? ((Comparable<K>)key1).compareTo(key2) : kComparator.compare(key1, key2);
    }

    /**
     * 搜索给定的键
     *
     * @param key - 给定的键值
     * @return
     */
    public V search(K key){
        return search(root, key);
    }


    /**
     * 在以给定节点为根的子树中, 递归搜索
     * 给定的 {@code key}
     *
     * @param node - 子树的根节点
     * @param key - 给定的键值
     * @return 键关联的值, 如果存在, 否则null
     */
    private V search(BTreeNode<K, V> node, K key){
       return null;
    }

    /**
     * 分裂一个满子节点 {@code childNode}
     * 你需要自己保证给定的子节点不是满节点
     *
     * @param parentNode
     * @param childNode
     * @param index
     */
    private void splitNode(BTreeNode<K, V> parentNode, BTreeNode<K, V> childNode, int index){
        assert childNode.size() == maxKeySize;

    }

    /**
     * 在一个非满节点中插入给定的项
     * 前提: 高度为h的m阶的B树, 新节点一般是插入在第h层. 通过检索可以确定关键码应该插入的节点位置
     * 两种情况讨论:
     * 1. 若该节点中关键码个数小于 m-1, 则直接插入即可.
     * 2. 若节点中关键码个数小于 m-1, 则将引起节点的分裂
     *    分裂:
     *      1) 以中间关键码为界, 将节点一份为二, 产生一个新节点
     *      2) 把中间关键码插入到父节点 (h-1层)中
     *      3) 重复上述工作, 最坏情况一直分裂到根节点, 建立一个新的根节点, 则整个B树增加一层
     *
     * @param node
     * @param entry
     * @return
     */
    private boolean insertNotFull(BTreeNode<K, V> node, Entry<K, V> entry){
        assert node.size() < maxKeySize;

        if(node.isLeaf()){ // 如果是叶子节点, 则直接插入
            return node.insertEntry(entry);
        }
        else{
            /**
             *  1. 找到 entry 在给定节点应该插入的位置
             *  2. 将entry应该插入该位置
             */
            SearchResult<V> result = node.searchKey(entry.getKey());
            // 如果存在, 则直接返回失败
            if(result.isExist()){
                return false;
            }

        }
        return false;
    }

    /**
     * 在B树中插入给定的键值对
     *
     * 前提: 高度为h的m阶的B树, 新节点一般是插入在第h层. 通过检索可以确定关键码应该插入的节点位置
     * 两种情况讨论:
     * 1. 若该节点中关键码个数小于 m-1, 则直接插入即可.
     * 2. 若节点中关键码个数小于 m-1, 则将引起节点的分裂
     *    分裂:
     *      1) 以中间关键码为界, 将节点一份为二, 产生一个新节点
     *      2) 把中间关键码插入到父节点 (h-1层)中
     *      3) 重复上述工作, 最坏情况一直分裂到根节点, 建立一个新的根节点, 则整个B树增加一层
     *
     * @param key
     * @param value
     * @return
     */
    public boolean insert(K key, V value){
        if(root.size() == maxKeySize){ // 如果根节点满了, 则B树长高
            BTreeNode<K, V> newRoot = new BTreeNode<K, V>(kComparator);
            newRoot.setLeaf(false);
            newRoot.addChild(root);
            splitNode(newRoot, root, 0);
            root = newRoot;
        }
        return insertNotFull(root, new Entry<K, V>(key, value));
    }

    /**
     * 如果存在给定的键, 则更新键关联的值
     * 否则插入给定的项
     *
     * @param node - 非满节点
     * @param entry - 给定的项
     * @return true 如果B树中不存在给定的项, 否则false
     */
    private V putNotFull(BTreeNode<K, V> node, Entry<K, V> entry){
        return null;
    }

    /**
     * 如果B树中存在给定的键, 则更新值
     * 否则插入
     *
     * @param key - 键
     * @param value - 值
     * @return 如果B树中存在给定的键, 则返回之前的值, 否则null
     */
    public V put(K key, V value){
        return null;
    }

    /**
     * 从B树中删除一个与给定关键的项
     *
     * @param key - 给定的键
     * @return - 如果B树中存在给定键关联的项, 则返回删除的项, 否则null
     */
    public Entry<K, V> delete(K key){
        return delete(root, key);
    }

    /**
     * 以给定{@code node} 为根的子树中删除与给定键关联的项
     * 删除的实现思想参考 <<算法导论>> 第二版的第18章
     *
     * @param node - 给定的节点
     * @param key - 给定的键
     * @return 如果B树中存在给定键关联的项, 则返回删除的项, 否则null
     */
    private Entry<K, V> delete(BTreeNode<K, V> node, K key){
        return null;
    }

    /**
     * 一个简单的层次遍历B树实现, 用于输出B树
     */
    public void output(){
        Queue<BTreeNode<K, V>> queue = new LinkedList<>();
        queue.offer(root);
        while(!queue.isEmpty()){
            BTreeNode<K, V> node = queue.poll();
            for(int i = 0; i < node.size(); ++i){
                logger.info(node.entryAt(i) + " ");
            }
            System.out.println();
            if(!node.isLeaf()){
                for(int i = 0; i < node.size(); ++i){
                    queue.offer(node.childAt(i));
                }
            }
        }
    }
}
