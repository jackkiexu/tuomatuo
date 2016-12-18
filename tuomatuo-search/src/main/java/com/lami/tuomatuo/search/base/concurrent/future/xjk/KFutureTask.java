package com.lami.tuomatuo.search.base.concurrent.future.xjk;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 实现自己的Future要点
 * 1. 需要一个反应当前future状态的变量
 * 2. 一个指向线程的链表节点即一个链表
 * 3. 实现一个线程安全的链表节点删除方法
 *
 * Created by xjk on 12/17/16.
 */
public class KFutureTask<V> implements Future<V> {

    /**
     *
     */
    private volatile int state;
    private static final int NEW             = 0; // 新建 IFutureTask
    private static final int EXECUTING       = 1; // 开始执行 IFutureTask 中的task
    private static final int COMPLETED       = 2; // 任务执行结束
    private static final int CANCELLED       = 3; // 主动调用future.cancel进行任务取消(前提任务的状态是new)
    private static final int INTERRUPTED     = 4; // 主动调用Thread.interrupted进行线程的中断

    private static Unsafe unsafe;
    // 等待线程队列的首节点
    private transient volatile WaiterNode head;
    // 等待线程队列的尾节点
    private transient volatile WaiterNode tail;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }


    /**
     * 链表节点
     */
    static final class WaiterNode{
        Thread thread;
        WaiterNode   preNode;
        WaiterNode   nextNode;

        public WaiterNode(){}

        public WaiterNode(Thread thread) {
            this.thread = thread;
        }

        public WaiterNode(Thread thread, WaiterNode nextNode) {
            this.nextNode = nextNode;
            this.thread = thread;
        }

    }

    /**
     * 当前线程入队列方法
     * @return
     */
    private WaiterNode enqueue(final WaiterNode waiterNode){
        for(;;){
            WaiterNode t = tail;
            if(t == null){ // Must initialize
                if(compareAndSetHead(new WaiterNode())){
                    tail = head;
                }
            }else{
                waiterNode.preNode = t;
                if(compareAndSetTail(t, waiterNode)){
                    t.nextNode = waiterNode;
                    return t;
                }
            }
        }
    }

    private WaiterNode addWaiter(WaiterNode insertNode){
        WaiterNode node = new WaiterNode(Thread.currentThread(), insertNode);

        WaiterNode pred = tail;
        if(pred != null){
            node.preNode = pred;
            if(compareAndSetTail(pred, node)){
                pred.nextNode = node;
                return node;
            }
        }
        enqueue(node);
        return node;
    }

    /**
     * 队列中移除指定的 WaiterNode
     */
    private void removeWaiterNode(){

    }

    /**
     * 当任务完成时移除所有的节点
     */
    private void removeAllWaiterNode(){

    }

    private static long headOffset;
    private static long tailOffset;

    static {
        try {
            unsafe = UnSafeClass.getInstance();
            headOffset = unsafe.objectFieldOffset(
                    KFutureTask.class.getDeclaredField("head")
            );
            tailOffset = unsafe.objectFieldOffset(
                    KFutureTask.class.getDeclaredField("tail")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * CAS head field. Used only by enq
     * @param update
     * @return
     */
    private final boolean compareAndSetHead(WaiterNode update){
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    /**
     * CAS tail field. Used only by enq
     * @param expect
     * @param update
     * @return
     */
    private final boolean compareAndSetTail(WaiterNode expect, WaiterNode update){
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

}
