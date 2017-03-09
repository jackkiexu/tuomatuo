package com.apache.catalina;

/**
 * PersistentManager would have been a better name  but that have clashed
 * with implementation name
 *
 * Created by xjk on 3/6/17.
 */
public interface StoreManager extends DistributedManager {

    /**
     * Return the Store object which manages persistent Session
     * storage for this Manager
     * @return
     */
    Store getStore();

    /**
     * Remove this session from the active Sessions for this Manager,
     * but not from the Store. (Used by the PersistentValve)
     *
     * @param session Session to be removed
     */
    void removeSuper(Session session);
}
