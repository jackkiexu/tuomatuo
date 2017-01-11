package com.lami.tuomatuo.search.base.concurrent.priorityqueue;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xjk on 1/11/17.
 */
public class Heap<E extends Comparable<E>>{

    /** heap size */
    volatile int size = 0;
    /** The Heap item */
    transient Object[] queues;

    /** Main lock, guarding all access  */
    ReentrantLock lock = new ReentrantLock();

    /** Condition for waiting takes */
    Condition notEmpty = lock.newCondition();

    /** Condition for waiting puts */
    Condition notFull = lock.newCondition();

    public Heap(Object[] queues) {
        this.queues = queues;
        this.size = queues.length;
    }

    /**
     * 插入元素到堆中
     * @param e
     * @return
     * @throws InterruptedException
     */
    public boolean put(E e) throws InterruptedException{
        lock.lockInterruptibly();
        try {
            if(e == null) throw new NullPointerException();
            // 1. 判断堆是否满了, 满了的话就等待, 直到有其他线程拿走元素
            if(size > queues.length){ // queue已经满了, 等待清楚
                notFull.await(); // 这个 await 是响应线程中断的
            }
            // 2. 若堆中没有元素, 则直接在堆的头节点放入元素
            if(size == 0){ // heap中没有数据
                queues[0] = e;
            }else{
                queues[size] = e;
                size++;
                siftUp(size - 1, e);
            }
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * 获取队列中的头节点
     * @return
     * @throws InterruptedException
     */
    public E take() throws InterruptedException{

        /**
         * 1. 直接将头节点获取
         * 2. 将队列中的尾节点拿出来, 从节点0开始siftdown -> 调整heap,使得,这个堆的最小值还在heap的顶上
         */
        E result = null;
        lock.lockInterruptibly();
        try {
            // 1. 若heap为空, 则等待, 直到有数据
            if(size == 0){ // heap中没数据
                notEmpty.await(); // 等待放入数据
                return null;
            }
            int s = size - 1;
            // 2. 将heap的头节点拿出来
            result = (E)queues[0];
            // 3. 将heap的尾节点拿出来, 若堆中还有元素, 在从(index=0)从开始siftdown调整堆
            E x = (E)queues[s];
            queues[s] = null;
            if(s != 0){
                siftDown(0, x);
            }
            size--;
            notFull.signal(); // 进行唤醒
        } finally {
            lock.unlock();
        }
        return result;
    }

    /**在数组中插入元素x到下标为k的位置, 为保持堆的性质,
     * 通过siftUp来进行调整, 直到x大于或等于x的 parent的值或到root节点
     *
     * @param k
     * @param x
     */
    private void siftUp(int k, E x){
        Comparable<? super E> key = (Comparable<? super E>)x;
        while(k > 0){
            int parent = parent(k);  // 获取对应的父节点的下标
            Object e = queues[parent]; // 获取对应的父节点对应的值
            // 将当前节点与父节点的值进行比较
            // 若当前节点比其父节点大, 则说明不在需要在向上 sift 比较了
            //
            if(key.compareTo((E) e) >= 0){
                break;
            }
            queues[k] = e; // 将父节点下沉
            k = parent; // 将这次比较的父节点赋值给k, 为下次 k 与其父节点作比较而准备
        }
        // 这里的k 有可能是最初节点 x的父节点, 也有可能就是x节点父节点的下标
        queues[k] = key;
    }

    /**
     * 插入元素x到位置k, 为保持二叉堆的特性, 对x进行siftDown, 直到x<=子节点
     * @param k
     * @param x
     */
    private void siftDown(int k, E x){
        Comparable<? super E> key = (Comparable<? super E>)x;
        int half = parent(size -1); // 最后一个节点的父节点下标
        while(k < half){
            // 1. 获取子节点的坐标, 并取出两者中的最小值
            int child = leftChildIndex(k);
            Object c = queues[child];
            int right = child + 1;

            // 2. 选中子节点中最小的那个节点进行比较
            if(right < size -1 ){
                Object r = queues[right];
                if(((E)c).compareTo((E)r) >= 1) {
                    c = queues[child = right];
                }
            }
            // 3. 若节点小于子节点, 则比较结束
            if(key.compareTo((E) c) <= 0){
                break;
            }
            queues[k] = c; // 将子节点上行
            k = child; // 父节点的光标下移, 为下次比较准备
        }
        queues[k] = key;
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
     * 构建堆, 我们这里是最小堆(即二叉树的parent节点比子节点小)
     * @return
     */
    public Heap buidHeap(){
        /**
         * 从最低层开始构建最大堆
         */
        for(int i = parent(queues.length - 1); i >= 0; i--){
            siftDown(i, (E)queues[i]);
        }
        return this;
    }

    public String toString() {
        return Arrays.toString(queues).toString();
    }

    public static void main(String[] args) throws Exception {
        Integer[] data = new Integer[10];
        for (int i = 0; i < data.length; i++) {
            data[i] = (int) (Math.random() * 100);
        }
        System.out.println("init:"+Arrays.toString(data));
        Heap<Integer> heap = new Heap<Integer>(data);
        heap.buidHeap();
        System.out.println("after buidHeap :" + heap);
        heap.take();
        heap.take();
        System.out.println("after take: " + heap);
        heap.put((int) (Math.random() * 100));
        System.out.println("after put:" + heap);

    }

}
