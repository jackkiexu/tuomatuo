package com.lami.tuomatuo.search.base.concurrent.tree.rbtree;

import org.apache.log4j.Logger;
import org.apache.regexp.RE;

/**
 * http://www.cnblogs.com/skywang12345/p/3624343.html
 * http://tech.meituan.com/redblack-tree.html
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
    private void leftRotate(RBINode<T> x){
        // 设置x的右孩子为y
        RBINode<T> y = x.right;

        // 将 "y的左孩子" 设为 "x的右孩子"
        // 如果y的左孩子非空, 将 "x" 设置为 "y左孩子的父亲"
        x.right = y.left;
        if(y.left != null){
            y.left.parent = null;
        }

        // 将 "x的父亲" 设置为 "y的父亲"
        y.parent = x.parent;

        if(x.parent == null){
            this.mRoot = y;             // 如果 "x的父亲" 是空节点, 则将y设置为根节点
        }else{
            if(x.parent.left == x){
                x.parent.left = y;      // 如果 x 是它父节点的左孩子, 则将y设置为 "x的父亲节点的左孩子"
            }else{
                x.parent.right = y;     // 如果 x 是它父亲节点的左孩子, 则将y设置为 "x的父亲节点的左孩子"
            }
        }

        // 将 "x" 设置为 "y的左孩子"
        y.left = x;
        // 将 "x 的父亲节点" 设置为 "y"
        x.parent = y;
    }

    /*
     * 对红黑树的节点(y)进行右旋转
     *
     * 右旋示意图(对节点y进行左旋)：
     *            py                               py
     *           /                                /
     *          y                                x
     *         /  \      --(右旋)-.            /  \                     #
     *        x   ry                           lx   y
     *       / \                                   / \                   #
     *      lx  rx                                rx  ry
     *
     */
    private void rightRotate(RBINode<T> y){
        // 设置x是当前节点的左孩子
        RBINode<T> x = y.left;

        // 将 "x的右孩子" 设置为 "y的左孩子"
        // 如果 "x的右孩子" 不为空的话, 将 "y" 设置为 "x的右孩子的父亲"
        y.left = x.right;
        if(x.right != null){
            x.right.parent = y;
        }

        // 将 "y的父亲" 设置为 "x的父亲"
        x.parent = y.parent;

        if(y.parent == null){
            this.mRoot = x;         // 如果 "y的父亲" 是空节点, 则将x设置为 根节点
        }else{
            if(y == y.parent.right){
                y.parent.right = x;     // 如果 y是它父亲节点的右孩子, 则将x设置为 "y的父亲节点的右孩子"
            }else{
                y.parent.left = x;      // (y是它父节点的左孩子) 将x设置为 "x父亲节点的左孩子"
            }
        }

        // 将 "y" 设置为 "x的右孩子"
        x.right = y;

        // 将 "y的父亲节点" 设置为 "x"
        y.parent = x;
    }

    /**
     * 红黑树 插入数据后的修正函数
     *
     * 向红黑树中插入节点之后(失去平衡), 再调用该函数
     * 目的: 将它重新塑造成一颗红黑树
     *
     * @param node 插入的节点        // 对应 <<算法导论>> 中的 z
     */
    private void insertFixUp(RBINode<T> node){
        RBINode<T> parent, gparent;

        // 若 "父节点存在, 并且父节点的颜色是红色"
        while((parent = parentOf(node)) != null && isRed(parent)){
            gparent = parentOf(parent);

            // 若 "父节点" 是 "祖父节点的左孩子"
            if(parent == gparent.left){
                // CASE 1 条件: 叔叔节点是红色
                RBINode<T> uncle = gparent.right;
                if((uncle != null) && isRed(uncle)){
                    setBlack(uncle);
                    setBlack(parent);
                    setRed(gparent);
                    node = gparent;
                    continue;
                }

                // CASE 2 条件: 叔叔是黑色, 且当前节点是右孩子
                if(parent.right == node){
                    RBINode<T> tmp;
                    leftRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }

                // CASE 3 条件: 叔叔是黑色, 且当前节点是左孩子
                setBlack(parent);
                setRed(gparent);
                rightRotate(gparent);
            }
            // 若 "z的父节点" 是 "z的祖父节点的右孩子"
            else{
                // CASE 1 条件: 叔叔节点是红色
                RBINode<T> uncle = gparent.left;
                if((uncle != null) && isRed(uncle)){
                    setBlack(uncle);
                    setBlack(parent);
                    setRed(gparent);
                    node = gparent;
                    continue;
                }

                // CASE 2 条件: 叔叔是黑色, 且当前节点是左孩子
                if(parent.left == node){
                    RBINode<T> tmp;
                    rightRotate(parent);
                    tmp = parent;
                    node = tmp;
                }

                // CASE 3 条件: 叔叔是黑色, 且当前节点是右孩子
               setBlack(parent);
                setRed(gparent);
                leftRotate(gparent);
            }
        }

        // 将根节点设置为黑色
        setBlack(this.mRoot);
    }

    /**
     * 将节点插入到红黑树中
     * @param node
     */
    private void insert(RBINode<T> node){
        int cmp;
        RBINode<T> y = null;
        RBINode<T> x = this.mRoot;

        // 1. 将红黑树当做一颗普通的二叉树, 将节点添加到二叉树中
        while(x != null){
            y = x;
            cmp = node.key.compareTo(x.key);
            if(cmp < 0){
                x = x.left;
            }else{
                x = x.right;
            }
        }

        node.parent = y;
        if(y != null){
            cmp = node.key.compareTo(y.key);
            if(cmp < 0){
                y.left = node;
            }else{
                y.right = node;
            }
        }else{
            this.mRoot = y;
        }

        // 2. 设置节点的颜色为红色
        node.color = RED;

        // 3. 将它重新修正为一颗二叉树
        insertFixUp(node);
    }

    /**
     * 新建节点 (key), 并将其修正为红黑树
     * @param key
     */
    public void insert(T key){
        RBINode<T> node = new RBINode<T>(BLACK, key, null, null, null);

        // 如果新建节点失败, 则返回
        if(node != null){
            insert(node);
        }
    }

    /**
     * 红黑树删除修正函数
     *
     * 从红黑树中删除插入节点后(红黑树失去平衡), 再调用该函数
     * 目的: 将它重新塑造成一颗新的红黑树
     *
     * @param node 待修正的节点
     * @param parent
     */
    private void removeFixUp(RBINode<T> node, RBINode<T> parent){
        RBINode<T> other;

        while(node == null || isBlack(node) && (node != this.mRoot)){
            if(parent.left == node){
                other = parent.right;
                if(isRed(other)){
                    // Case 1: x的兄弟w是红色的
                    setBlack(other);
                    setRed(parent);
                    leftRotate(parent);
                    other = parent.right;
                }

                if((other.left == null || isBlack(other.right)) &&
                        (other.right == null || isBlack(other.right))){
                    // Case 2: x的兄弟w是黑色, 且w的两个孩子也都是黑色的
                    setRed(other);
                    node = parent;
                    parent = parentOf(node);
                }else{
                   if(other.right == null || isBlack(other.right)){
                        // Case 3: x的兄弟w是黑色的, 并且w的左孩子是红色, 右孩子为黑色
                       setBlack(other.left);
                       setRed(other);
                       rightRotate(other);
                       other = parent.right;
                   }
                    // Case 4: x的兄弟节点w是黑色的, 并且w的右孩子是红色的, 左孩子是任意颜色
                    setColor(other, colorOf(parent));
                    setBlack(parent);
                    setBlack(other.right);
                    leftRotate(parent);
                    node = this.mRoot;
                    break;
                }

            }else{

                other = parent.left;
                if(isRed(other)){
                    // Case 1; x的兄弟w是红色的
                    setBlack(other);
                    setRed(parent);
                    rightRotate(parent);
                    other = parent.left;
                }

                if((other.left == null || isBlack(other.left)) &&
                        (other.right == null || isBlack(other.right))){
                    // Case 2: x的兄弟w是黑色, 且w的两个孩子也都是黑色的
                    setRed(other);
                    node = parent;
                    parent = parentOf(node);
                }else{
                    if(other.left == null || isBlack(other.left)){
                        // Case 3: x的兄弟w是黑色的, 并且w的左孩子是红色的, 右孩子是黑色的
                        setBlack(other.right);
                        setRed(other);
                        leftRotate(other);
                        other = parent.left;
                    }

                    // Case 4: x的兄弟w是黑色的, 并且w的右孩子是红色的, 左孩子任意颜色
                    setColor(other, colorOf(parent));
                    setBlack(parent);
                    setBlack(other.left);
                    rightRotate(parent);
                    node = this.mRoot;
                    break;
                }

            }
        }

        if(node != null){
            setBlack(node);
        }
    }

    /**
     * 删除节点 node, 并返回删除的节点 node
     * @param node
     */
    private void remove(RBINode<T> node){
        RBINode<T> child = null, parent = null;
        boolean color;

        // 被删除的节点的"左右孩子都不为空"的情况
        if((node.left != null) && (node.right != null)){
            // 被删节点的后继节点, (称为"取代节点")
            // 用它来取代"被删除节点"的位置, 然后再将"被删节点"去掉
            RBINode<T> replace = node;

            // 1. 获取后继节点
            replace = replace.right;
            while(replace.left != null){
                replace = replace.left;
            }

            // 2. 将 后继节点代替 node节点
            // node节点 不是根节点(只有根节点不存在父节点)
            if(parentOf(node) != null){
                if(parentOf(node).left == node){
                    parentOf(node).left = replace;
                }else{
                    parentOf(node).right = replace;
                }
            } // "node节点" 是根节点, 更新根节点
            else {
                this.mRoot = replace;
            }

            // 3. 获取 "取代节点" 的信息
            // child 是 "replace节点"的右孩子, 也是需要 "调整的节点"
            // "取代节点" 肯定不存在左孩子! 因为它是一个后继节点
            child = replace.right;
            parent = parentOf(replace);
            color = colorOf(replace);

            // "被删除节点" 是 "它的后继节点的父节点"
            if(parent == node){
                parent = replace;
            }else{
                // child 不为空
                if(child != null){
                    setParent(child, parent);
                }
                /**
                 *  若 child 不是 null, 则 parent节点可能定是大于 child
                 *  1. 因为 " replace = replace.left"
                 */
                parent.left = child;

                replace.right = node.right;
                setParent(node.right, replace);
            }

            replace.parent = node.parent;
            replace.color = node.color;
            replace.left = node.left;
            node.left.parent = replace;

            if(color == BLACK){
                removeFixUp(child, parent);
            }

            node = null;
            return;
        }

        parent = node.parent;
        // 保存 "取代节点"的颜色
        color = node.color;

        if(child != null){
            child.parent = parent;
        }

        // "node节点" 不是根节点
        if(parent != null){
            if(parent.left == node){
                parent.left = child;
            }else{
                parent.right = child;
            }
        }else{
            this.mRoot = child;
        }

        if(color == BLACK){
            removeFixUp(child, parent);
        }
        node = null;
    }

    /**
     * 删除节点(z), 并返回被删除的节点
     * @param key
     */
    public void remove(T key){
        RBINode<T> node;
        if((node = search(mRoot, key)) != null){
            remove(node);
        }
    }

    private void destory(RBINode<T> tree){
        if(tree == null){
            return;
        }

        if(tree.left != null){
            destory(tree.left);
        }

        if(tree.right != null){
            destory(tree.right);
        }

        tree = null;
    }


    public void clear(){
        destory(mRoot);
        mRoot = null;
    }

    /*
     * 打印"红黑树"
     *
     * key        -- 节点的键值
     * direction  --  0，表示该节点是根节点;
     *               -1，表示该节点是它的父结点的左孩子;
     *                1，表示该节点是它的父结点的右孩子。
     */
    private void print(RBINode<T> tree, T key, int direction){
        if(tree == null) return;

        if(direction==0)    // tree是根节点
            System.out.printf("%2d(B) is root\n", tree.key);
        else                // tree是分支节点
            System.out.printf("%2d(%s) is %2d's %6s child\n", tree.key, isRed(tree)?"R":"B", key, direction==1?"right" : "left");

        print(tree.left, tree.key, -1);
        print(tree.right, tree.key, 1);
    }

    public void print(){
        if(mRoot != null){
            print(mRoot, mRoot.key, 0);
        }
    }

}