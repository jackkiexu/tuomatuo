package com.lami.tuomatuo.search.base.concurrent.aqs;

import java.io.Serializable;

/**
 * A synchronizer that may be exclusively owned by a thread. This class provides a basis for creating locks and related synchronizers
 * that may entail a notion of ownership (需要一个所有权的概念). The
 * {@code KAbstractOwnableSynchronizer} class itself does not manage or
 * use this information. However, subclass and tools may use
 * appropriately maintained values to help control and monitor access
 * and provide diagnostics
 *
 * Created by xujiankang on 2017/1/18.
 */
public abstract class KAbstractOwnableSynchronizer implements Serializable {

    /** Use serial ID even though all fields transient. */
    private static final long serialVersionUID = 3737899427754241961L;

    /** Empty constructor for use by subclasses */
    protected KAbstractOwnableSynchronizer(){}

    /** The current owner of exclusive mode synchronization */
    private transient Thread exclusiveOwnerThread;

    /**
     * Sets the thread that currently owns exclusive access.
     * A {@code null} argument indicates that no thread owns access
     * This method does not otherwise impose any synchronization or
     * {@code volatile} field access.
     * @param thread the owner thread
     */
    protected final void setExclusiveOwnerThread(Thread thread){
        exclusiveOwnerThread = thread;
    }

    /**
     * Return the thread last set by {@code setExclusiveOwnerThread},
     * or {@code null} if never set. This method does not otherwise
     * impose any synchronization or {@code volatile} field access.
     *
     * @return the owner thread
     */
    protected final Thread getExclusiveOwnerThread(){
        return exclusiveOwnerThread;
    }
}
