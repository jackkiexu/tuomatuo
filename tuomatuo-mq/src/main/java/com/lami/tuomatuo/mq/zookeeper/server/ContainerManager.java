package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * manages cleanup of container ZNodes. This class is meant to only
 * be run from the leader. There's no harm in running from followers/observers
 * but that will be extra work that's not needed, Once started, it periodically
 * checks container nodes that have a cversion > 0 and have no children, A
 * dalete is attempted on the node. The result of the delete is unimportant
 * If the proposal fails or the container node is not empty there's no harm
 * Created by xujiankang on 2017/3/19.
 */
public class ContainerManager {
}
