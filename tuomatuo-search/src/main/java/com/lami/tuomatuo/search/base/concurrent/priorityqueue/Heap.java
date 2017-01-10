package com.lami.tuomatuo.search.base.concurrent.priorityqueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xjk on 1/11/17.
 */
public class Heap<E extends Comparable<E>>{

    /** heap cursor */
    int cursor = 0;
    /** The Heap item */
    transient Object[] items = new Object[1024];

    /** Main lock, guarding all access  */
    ReentrantLock lock = new ReentrantLock();

    /** Condition for waiting takes */
    Condition notEmpty = lock.newCondition();

    /** Condition for waiting puts */
    Condition notFull = lock.newCondition();


    public boolean put(E e) throws InterruptedException{
        lock.lockInterruptibly();
        try {
            if(e == null) throw new NullPointerException();
            int k = cursor;
            if(k >= items.length){
                notFull.await(); // 这个 await 是响应线程中断的
            }
            if(k == 0){ // heap中没有数据
                items[0] = e;
            }else{
                siftUp(k, e);
            }

            notEmpty.signal();
        } finally {
            lock.unlock();
        }
        return true;
    }

    public E take(){
        notFull.signal();
        return null;
    }

    /**
     *
     * @param k
     * @param e
     */
    private void siftUp(int k, E e){

    }

    private void siftDown(){

    }

    /**
     * k的父节点的下标
     * @param k 节点在数组中的下标
     * @return
     */
    protected int parent(int k){
        return (k - 1) >>> 1;
    }

    /**
     * k的左子节点在数组中的下标
     * @param k 节点在数组中的下标
     * @return
     */
    protected int leftChildIndex(int k){
        return 2*k + 1;
    }

    /**
     * k的左子节点在数组中的下标
     * @param k 节点在数组中的下标
     * @return
     */
    protected int rightChildIndex(int k){
        return 2*k + 2;
    }

    /**
     * 构建堆, 我们这里是最大堆(即二叉树的parent节点比子节点大)
     * @return
     */
    public Heap buidHeap(){
        return this;
    }

}
