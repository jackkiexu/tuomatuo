package com.lami.tuomatuo.search.base.concurrent.tree.btree;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by xjk on 1/8/17.
 */
public class KBTreeNodeTest extends BaseTest {

    protected KBTree.BTreeNode bTreeNode = null;
    protected String str = "abcdefghijklmnopqrstuvwxyz";

    @Before
    public final void initBTreeNode(){
        bTreeNode = new KBTree.BTreeNode();
        for(int i = 0; i < str.length(); i++){
            KBTree.Entry<String, String> entry = new KBTree.Entry<String, String>(i + "", str.charAt(i) +"");
            bTreeNode.addEntry(entry);
        }
        logger.info(bTreeNode);
    }

    @Test
    public void bTreeNodeAdd(){
        for(int i = 0; i < str.length(); i++){
            KBTree.Entry<String, String> entry = new KBTree.Entry<String, String>(i + "", str.charAt(i) +"");
            bTreeNode.addEntry(entry);
        }
        logger.info(bTreeNode);
    }

    @Test
    public void bTreeNodeSearchKey(){
        logger.info(bTreeNode.searchKey("3"));
        logger.info(bTreeNode.searchKey("5"));
    }



}
