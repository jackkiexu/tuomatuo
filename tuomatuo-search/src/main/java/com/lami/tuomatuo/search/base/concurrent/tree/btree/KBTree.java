package com.lami.tuomatuo.search.base.concurrent.tree.btree;

import lombok.Data;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * http://blog.csdn.net/wangpingfang/article/details/7426943
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
        private boolean exist;
        private int index;
        private V value;

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
         * </p>
         *
         * @param key
         * @return
         */
        public SearchResult<V> searchKey(K key){
            return null;
        }

    }

}
