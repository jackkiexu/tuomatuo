package com.lami.tuomatuo.search.base.concurrent.mcslock;

import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by xjk on 2016/5/24.
 */
public class MCSLock implements Lock {

    private static final Logger logger = Logger.getLogger(MCSLock.class);
    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myNode;

    public void lock() {
        QNode qnode = myNode.get();
        QNode pred = tail.getAndSet(qnode);
        if(pred != null){
            qnode.locked = true;
            pred.next = qnode;

            while(qnode.locked){}
        }
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock() {
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void unlock() {
        QNode qNode = myNode.get();
        if(qNode.next == null){
            if(tail.compareAndSet(qNode, null)){
                return ;
            }
            while (qNode.next == null){}
        }
        qNode.next.locked = false;
        qNode.next = null;
    }


    public Condition newCondition() {
        return null;
    }

    class QNode{
        boolean locked = false;
        QNode next = null;
    }

    public static void main(String[] args) {
        MCSLock lock = new MCSLock();
        lock.lock();
        lock.unlock();
    }
}
