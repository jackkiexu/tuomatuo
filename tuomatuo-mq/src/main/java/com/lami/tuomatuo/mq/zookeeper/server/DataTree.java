package com.lami.tuomatuo.mq.zookeeper.server;

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

}
