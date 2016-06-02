package com.lami.tuomatuo.search.base.concurrent.spinlock;

import org.apache.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by xujiankang on 2016/5/12.
 */
public class ClhSpinLock {

    private static final Logger logger = Logger.getLogger(ClhSpinLock.class);

    private ThreadLocal<Node> prev;
    private ThreadLocal<Node> node;
    private final AtomicReference<Node> tail = new AtomicReference<Node>(new Node());

    public ClhSpinLock() {
        this.node = new ThreadLocal<Node>(){
            protected Node initialValue(){
                return new Node();
            }
        };

        this.prev = new ThreadLocal<Node>(){
          protected  Node initialValue(){
              return null;
          }
        };
    }

    public void lock(){
        final Node node = this.node.get();
        node.locked = true;
        // 一个 CAS 操作即可将当前线程对应的节点加入到队列中
        // 并且同时获得了前继节点的引用, 然后就是等待前继释放锁
        Node pred = this.tail.getAndSet(node);
        this.prev.set(pred);
        while (pred.locked){ // 进入自旋
        }
    }

    public void unlock(){
        final Node node = this.node.get();
        node.locked = false;
        this.node.set(this.prev.get());
    }

    private static class Node{
        private volatile boolean locked;
    }

    public static void main(String[] args) throws Exception{
        final ClhSpinLock lock = new ClhSpinLock();
        lock.lock();

        for(int i = 0; i < 10; i++){
            new Thread(new Runnable() {
                public void run() {
                    lock.lock();
                    logger.info("" + Thread.currentThread().getId() + ", " + Thread.currentThread().getName() + ", acquired the lock!");
                    lock.unlock();
                }
            }).start();
            Thread.sleep(100);
        }
        Queue queue = new ConcurrentLinkedQueue();
        logger.info("main thread unlock");
        lock.unlock();
    }
}
