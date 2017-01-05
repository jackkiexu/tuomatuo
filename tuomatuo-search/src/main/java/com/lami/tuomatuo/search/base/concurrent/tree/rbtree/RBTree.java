package com.lami.tuomatuo.search.base.concurrent.tree.rbtree;

import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import sun.jvm.hotspot.utilities.RBNode;

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

    private static final Logger logger = Logger.getLogger(RBTree.class);

    private RBINode<T> mRoot;  // 根节点
    private static final boolean RED        = false;
    private static final boolean BLACK      = true;

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

    public RBTree() {
        mRoot = null;
    }

    private RBINode<T> parentOf(RBINode<T> node){
        return node != null? node.parent : null;
    }

    private boolean colorOf(RBINode<T> node){
        return node != null? node.color : BLACK;
    }

    private boolean isRed(RBINode<T> node){
        return ((node != null) && (node.color == RED))? true : false;
    }

    private boolean isBlack(RBINode<T> node){
        return !isRed(node);
    }

    private void setBlack(RBINode<T> node){
        if(node != null){
            node.color = BLACK;
        }
    }

    private void setRed(RBINode<T> node){
        if(node != null){
            node.color = RED;
        }
    }

    private void setParent(RBINode<T> node, RBINode<T> parent){
        if(node != null){
            node.parent = parent;
        }
    }

    private void setColor(RBINode<T> node, boolean color){
        if(node != null){
            node.color = color;
        }
    }

    /** 前序遍历 "红黑树" */
    private void preOrder(RBINode<T> tree){
        if(tree != null){
            logger.info(tree.key + " ");
            preOrder(tree.left);
            preOrder(tree.right);
        }
    }

    public void preOrder(){
        preOrder(mRoot);
    }

    /** 中序遍历 "红黑树" */
    private void inOrder(RBINode<T> tree){
        if(tree != null){
            inOrder(tree.left);
            logger.info(tree.key + " ");
            inOrder(tree.right);
        }
    }

    public void inOrder(){
        inOrder(mRoot);
    }

    private void postOrder(RBINode<T> tree){
        if(tree != null){
            postOrder(tree.left);
            postOrder(tree.right);
            logger.info(tree.key + " ");
        }
    }

    public void postOrder(){
        postOrder(mRoot);
    }

    private RBINode<T> search(RBINode<T> x, T key){
        if(x == null){
            return x;
        }

        int cmp = key.compareTo(x.key);
        if(cmp < 0){
            return search(x.left, key);
        }else if(cmp > 0){
            return search(x.right, key);
        }else{
            return x;
        }
    }

    public RBINode<T> search(T key){
        return search(mRoot, key);
    }

    /** (非递归实现) 查找 "红黑树x" 中键值为key的节点 */
    private RBINode<T> iterativeSearch(RBINode<T> x, T key){
        while(x != null){
            int cmp = key.compareTo(x.key);

            if(cmp < 0){
                x = x.left;
            }else if(cmp > 0){
                x = x.right;
            }else {
                return x;
            }
        }
        return x;
    }

    public RBINode<T> iterativeSearch(T key){
        return iterativeSearch(mRoot, key);
    }

    /** 查找最小结点: 返回tree为根结点的红黑树的最小结点 */
    private RBINode<T> minimum(RBINode<T> tree){
        if(tree == null){
            return null;
        }

        while (tree.left != null){
            tree = tree.left;
        }
        return tree;
    }

    public T minimum(){
        RBINode<T> p = minimum(mRoot);
        if(p != null){
            return p.key;
        }
        return null;
    }

    /** 查找最大结点: 返回tree为根结点的红黑树的最大结点 */
    private RBINode<T> maximum(RBINode<T> tree){
        if(tree == null){
            return null;
        }

        while(tree.right != null){
            tree = tree.right;
        }
        return tree;
    }

    public T maximum(){
        RBINode<T> p = maximum(mRoot);
        if(p != null){
            return p.key;
        }

        return null;
    }

    /** 找结点(x)的后继结点. 即 查找"红黑树中数据值大于该结点"的"最小值" */
    public RBINode<T> successor(RBINode<T> x){
        // 如果x存在右孩子, 则 "x的后继结点" 为 "以其右孩子为根的子树的最小结点"
        if(x.right != null){
            return minimum(x.right);
        }

        /**
         *  如果x没有右孩子. 则x有以下两种可能
         *  1. x是"一个左孩子", 则 "x的后继结点" 为 "它的父结点"
         *  2. x是"一个右孩子", 则查找 "x的最低的父结点, 并且该父结点要具有左孩子, 且此时x不是y的右结点", 找到的这个"最低的父结点"就是"x的后继结点"
         */
        RBINode<T> y = x.parent;
        while((y != null) && (x == y.right)){
            x = y;
            y = y.parent;
        }

        return y;
    }

    /** 找结点(x)的前驱结点, 即, 查找 "红黑树中数据值小于该结点" 的 "最大结点" */
    public RBINode<T> predecessor(RBINode<T> x){
        // 如果x存在左孩子, 则 "x的前驱结点" 为 "以其左孩子为根的最大结点"
        if(x.left != null){
            return maximum(x.left);
        }

        /**
         * 如果x没有右孩子, 则x有以下两种可能:
         * 1. x是"一个右孩子", 则"x的前驱结点" 为 "它的父结点"
         * 2. x是"一个左孩子", 则查找"x的最低的父节点, 并且该父结点要具有右孩子, 并且x不是y的左结点", 找到的这个 "这个最低的父结点"就是"x的前驱结点"
         */
        RBINode<T> y = x.parent;
        while((y != null) && (x == y.left)){
            x = y;
            y = y.parent;
        }

        return y;
    }

    /*
     * 对红黑树的节点(x)进行左旋转
     *
     * 左旋示意图(对节点x进行左旋)：
     *      px                              px
     *     /                               /
     *    x                               y
     *   /  \      --(左旋)-.             / \                #
     *  lx   y                          x   ry
     *     /   \                       /  \
     *    ly   ry                     lx  ly
     *
     *
     */
    private void leftRotate(){

    }

}