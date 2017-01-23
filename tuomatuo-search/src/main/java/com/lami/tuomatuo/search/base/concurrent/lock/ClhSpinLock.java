package com.lami.tuomatuo.search.base.concurrent.lock;

import org.apache.log4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

/**
 * the Craig, Landin, and Hagersten CLH queue lock
 *
 * http://www.cs.tau.ac.il/~shanir/nir-pubs-web/Papers/CLH.pdf
 * http://www.programering.com/a/MjM5gTNwATE.html
 *
 * http://www.cnblogs.com/zhanjindong/p/java-concurrent-package-aqs-clh-and-spin-lock.html
 * http://blog.csdn.net/aesop_wubo/article/details/7533186
 *
 * 缺点:
 *  1. 不能实现锁的重入
 *  2. 大量线程同时获取锁时, 都不断的自旋, 会造成CPU资源浪费
 *
 * CLH lock queue
 * Created by xujiankang on 2017/1/23.
 */
public class ClhSpinLock {

    private static final Logger logger = Logger.getLogger(ClhSpinLock.class);

    private final ThreadLocal<Node> prev;
    private final ThreadLocal<Node> node;
    private final AtomicReference<Node> tail = new AtomicReference<Node>(new Node());

    public ClhSpinLock() {
        this.node = new ThreadLocal<Node>(){
            @Override
            protected Node initialValue() {
                return new Node();
            }
        };

        this.prev = new ThreadLocal<Node>(){
            @Override
            protected Node initialValue() {
                return null;
            }
        };
    }

    public void lock(){
        final Node node = this.node.get();
        node.locked = true;
        // 一个 CAS 操作即可将当前线程对应的节点加入到队列中, 并且同时获得前继节点的引用, 然后就是等待前继节点释放锁
        Node pred = this.tail.getAndSet(node);
        this.prev.set(pred);
        while (pred.locked){} // 第二个线程获取锁时, 因为 40 行的原因, 会一直自旋
    }

    public void unlock(){
        final Node node = this.node.get();
        node.locked = false;
        this.node.set(this.prev.get());
    }

    static final class Node{
        private volatile boolean locked;
    }
}
