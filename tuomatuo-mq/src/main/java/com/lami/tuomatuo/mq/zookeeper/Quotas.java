package com.lami.tuomatuo.mq.zookeeper;

/**
 * This class manages quotas
 * and has many other utils
 * for quota
 *
 * Created by xujiankang on 2017/3/19.
 */
public class Quotas {

    /** the zookeeper nodes that acts as the management and status node */
    public static final String procZookeeper = "/zookeeper";

    /**
     * the zookeeper quota node that acts as the quota
     * management node for zookeeper
     */
    public static final String quotaZookeeper = "/zookeeper/quota";

    /**
     * the limit node that has the limit of a subtree
     */
    public static final String limitNode = "zookeeper_limits";

    /**
     * the stat node that monitors the limit of
     * a subtree
     */
    public static final String statNode = "zookeeper_stats";


    /**
     * return the quota path associated with this
     * prefix
     * @param path the actual path in zookeeper
     * @return the limit quota path
     */
    public static String quotaPath(String path){
        return quotaZookeeper + path + "/" + limitNode;
    }



    /**
     * return the stat quota path associated with this
     * prefix.
     * @param path the actual path in zookeeper
     * @return the stat quota path
     */
    public static String statPath(String path) {
        return quotaZookeeper + path + "/" +
                statNode;
    }
}
