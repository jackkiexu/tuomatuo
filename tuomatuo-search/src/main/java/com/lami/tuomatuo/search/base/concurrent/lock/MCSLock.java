package com.lami.tuomatuo.search.base.concurrent.lock;

import java.util.concurrent.atomic.AtomicReference;


/**
 * https://github.com/a-ray-of-sunshine/learn/blob/master/sunshine.learn/src/main/java/com/sunshine/learn/thread/lock/MCSLock.java
 * http://blog.csdn.net/aesop_wubo/article/details/7538934
 */

/**
 * 在CLH算法中，由于每个线程都在其前驱线程的QNode节点的locked域自旋，在NUMA体系下，
 * 即每个线程都在其前驱线程的remote memory位置自旋，因此性能上会打折扣。
 * 那么在NUMA体系下，如果每个线程自旋的位置都能固定在自己的local memory中，则性能相比于CLH算法，应该会有一定的提升。
 * MCS锁就是基于这种理念设计出来的。
 */
public class MCSLock {

    private AtomicReference<QNodeMCS> tail;
    private ThreadLocal<QNodeMCS> myNode;

    public MCSLock(){
        tail = new AtomicReference<QNodeMCS>(null);

        myNode = new ThreadLocal<QNodeMCS>(){
            @Override
            protected QNodeMCS initialValue() {
                return new QNodeMCS();
            }
        };
    }

    public void lock() {
        QNodeMCS qnode = myNode.get();
        QNodeMCS pred = tail.getAndSet(qnode);

        if(null != pred){
            qnode.locked = true;
            pred.next = qnode;
        }

        // 线程一直在自己节点的locked域自旋，直到locked域变为false，即前驱节点释放了锁。
        while(qnode.locked);
    }

    /**
     * 锁的释放过程分为三种情况
     */
    public void unlock() {
        QNodeMCS qnode = myNode.get();

        // 如果 qnode 没有后继结点
        if(null == qnode.next){
            if(tail.compareAndSet(qnode, null)){
                // 0. 如果，满足上面两个条件表示：此刻没有其它线程正在申请锁
                // 即没有后继结点，将 tial 指向 null, 后直接返回
                return;
            }

            // 1. tail.compareAndSet(qnode, null) 调用失败，
            // 表示 此刻有其它线程正在申请锁, 且其已将 tail 更新了,
            // 但是还不确定，后继结点，是否设置成功， 也就是 qnode.next 是否设置好
            // 下面, 所以调用下面的，循环来，等待，新的tail处的结点将其前驱设置成功后退出循环
            // 其实此时的操作就是在和 lock方法中对 qnode.next 操作的代码进行线程同步
            while(null == qnode.next);
        }

        // 2. 有后继结点，通知后继结点，可以获得锁了
        qnode.next.locked = false;


        // !!!!!!! important !!!!!!
        // 下面的代码有两个作用：

        // 0. 重置 qnode 的 next 值
        // 此时 myNode 又恢复到未获取到锁的状态
        // myNode 就是可以重新被当前线程在第二次获取锁的时候使用了

        // 1. help for GC
        // java里面对象，都会维持引用计数，所以，这里  qnode.next = null, 将不会导致（肯定不会导致）qnode.next 结点 的内存被收回
        // 只是将 qnode.next 内存区域的引用 减小 1， 而 qnode.next 指向的结点还在其 qnode.next.locked 上自旋，
        // 所以 qnode.next = null, 不会导致 next 结点的内存被回收
        qnode.next = null;
    }

    static final class QNodeMCS{
        public volatile boolean locked = false;
        public QNodeMCS next = null;
    }


    public static void main(String[] args) {
        MCSLock lock = new MCSLock();
        lock.lock();

        lock.unlock();
    }

}


