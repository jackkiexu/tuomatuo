package com.lami.tuomatuo.search.base.concurrent.tree.rbtree;

import org.apache.regexp.RE;

/**
 * http://www.cnblogs.com/skywang12345/p/3624343.html
 *
 * http://blog.csdn.net/v_july_v/article/details/6105630
 * http://blog.csdn.net/yxc135/article/details/7939671
 * http://www.cnblogs.com/skywang12345/p/3603935.html
 * http://www.cnblogs.com/skywang12345/p/3245399.html#aa2
 *
 * Java 版本的 红黑树
 *
 * 红黑树, 一种二叉查找树, 但在每个节点上增加一个存储位表示节点的颜色, 可以是Red或Black
 * 通过对任何一条从根到叶子路径上各个节点着色方式的限制, 红黑树确保没有一条路径会比其他路径长出两倍, 因而接近平衡的(http://blog.csdn.net/v_july_v/article/details/6105630)
 *
 * Created by xjk on 1/2/17.
 */
public class RBTree<T extends Comparable<T>> {

    static final class RBINode<T extends Comparable<T>>{
        boolean color;         // 颜色
        T key;                  // 关键字 (键值)
        RBINode<T> left;        // 左孩子
        RBINode<T> right;       // 右孩子
        RBINode<T> parent;      // 父节点

        public RBINode(boolean color, T key, RBINode<T> left, RBINode<T> right, RBINode<T> parent) {
            this.color = color;
            this.key = key;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }

        public T getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "RBINode{" +
                    "key=" + key +
                    ", color=" + color +
                    '}';
        }
    }



}