package com.lami.tuomatuo.search.base.concurrent.myaqs;

import sun.misc.Unsafe;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by xjk on 12/13/16.
 */
public abstract class Sync<E> {

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private volatile Waiter         headWaiter;
    private volatile Waiter         tailWaiter;
    private static long             headWaiterOffset;
    private static long             tailWaiterOffset;

    static {
        try {
            headWaiterOffset = unsafe.objectFieldOffset(Sync.class.getDeclaredField("headWaiter"));
            tailWaiterOffset = unsafe.objectFieldOffset(Sync.class.getDeclaredField("tailWaiter"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    static class Waiter{
        private Thread              thread;
        private volatile Waiter     next;
        private volatile int        status;
        private static long         statusOffset;
        private static long         nextOffset;
        private static final int WAITING = 1;
        private static final int CANCELLED = 2;


        static {
            try {
                statusOffset = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("status"));
                nextOffset = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("next"));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        public Waiter(Thread thread) {
            unsafe.putInt(this, statusOffset, WAITING);
            this.thread = thread;
        }

        public void orderSetNext(Waiter waiter){
            unsafe.putOrderedObject(this, nextOffset, waiter);
        }
    }


    public Sync() {
        headWaiter = tailWaiter = new Waiter(null);
    }

    protected abstract E get();

    public E take(long time, TimeUnit unit){
        E result;
        Waiter self = enqueue();
        long nanos = unit.toNanos(time);
        long t0 = System.nanoTime();
        do{
            // head 之后的next是本线程设置的 , 所以这里直接获取, 可以读取到就意味着确实是head节点的后继节点
            if(self == headWaiter.next){
                result = get();
                if(result == null){
                    if(nanos < 1000){
                        for(int i = 0; i < 1000; i++){
                            ;
                        }
                    }else{
                        LockSupport.parkNanos(nanos);
                    }

                    nanos -= System.nanoTime() - t0;
                    if(nanos < 0){
                        cancelWaiter(self);
                        return null;
                    }
                    t0 = System.nanoTime();
                }else{
                    headWaiter = self;
                    unparkHeadNext(self);
                    return result;
                }
            }else{
                if(nanos < 1000){
                    for(int i = 0; i < 1000; i++){
                        ;
                    }
                }else{
                    LockSupport.parkNanos(nanos);
                }

                nanos -= System.nanoTime() - t0;
                if(nanos < 0){
                    cancelWaiter(self);
                    return null;
                }
                t0 = System.nanoTime();
            }
            if(Thread.currentThread().isInterrupted()){
                cancelWaiter(self);
                return null;
            }
        }while(true);
    }

    public E take(){
        Waiter self = enqueue();
        do{
            if(self == headWaiter.next){
                E result = get();
                if(result == null){
                    LockSupport.park();
                }else{
                    headWaiter = self;
                    unparkHeadNext(self);
                    return result;
                }
            }else{
                LockSupport.park();
            }
            if(Thread.currentThread().isInterrupted()){
                cancelWaiter(self);
                return null;
            }
        }while(true);
    }

    private Waiter enqueue(){
        // TODO
        /**
         * 1. the tail node may be null -> lead to crash
         */
        Waiter newTail = new Waiter(Thread.currentThread());
        Waiter oldTail = tailWaiter;
        if(unsafe.compareAndSwapObject(this, tailWaiterOffset, oldTail, newTail)){
            oldTail.orderSetNext(newTail);
            return newTail;
        }

        for(;;){
            oldTail = tailWaiter;
            if(unsafe.compareAndSwapObject(this, tailWaiterOffset, oldTail, newTail)){
                oldTail.orderSetNext(newTail);
                return newTail;
            }
        }
    }

    public boolean isThreadOnWaiting(){
        return headWaiter != tailWaiter;
    }

    private Waiter findNextWaiter(Waiter waiter){
        if(waiter == tailWaiter){
            return null;
        }
        Waiter next = waiter.next;
        if(next != null){
            return next;
        }
        while((next = waiter.next) == null){
            ;
        }
        return next;
    }

    public void signal(){
        Waiter h = headWaiter;
        Waiter next = findNextWaiter(h);
        if(next != null){
            LockSupport.unpark(next.thread);
        }
    }

    /**
     * 唤醒后继节点, 注意, 这里的入口head节点就是当前的headwaiter
     * @param head
     */
    private void unparkHeadNext(Waiter head){
        Waiter next = findNextWaiter(head);
        if(next == null){
            return;
        }
        // 如果后继节点状态此时是等待, 则直接唤醒
        else if(next.status == Waiter.WAITING){
            LockSupport.unpark(next.thread);
            return ;
        }else{
            Waiter pred;
            do{
                do{
                    pred = next;
                    next = findNextWaiter(pred);
                }while(next != null && next.status == Waiter.CANCELLED && head == headWaiter);

                /**
                 * 在头结点未变化的情况下, 找到距离头节点最近的一个非cancel状态的节点
                 * 如果节点发生了变化, 意味着其他线程取得了控制权, 则后继行为由其他线程完成, 本线程可以退出
                 */
                if(head == headWaiter && casHead(head, pred)){
                    /**
                     * 如果成功的设置了新的头节点, 则尝试唤醒头节点的后继节点
                     */
                    head = pred;
                    next = findNextWaiter(pred);
                    if(next == null){
                        return ;
                    }else if(next.status == Waiter.WAITING){
                        LockSupport.unpark(next.thread);
                        return ;
                    }else{
                        continue;
                    }
                }else {
                    return;
                }

            }while(true);
        }
    }

    private void cancelWaiter(Waiter waiter){
        waiter.status = Waiter.CANCELLED;
        Waiter h = headWaiter;
        if(h.next == waiter && casHead(h, waiter)){
            unparkHeadNext(waiter);
        }
    }

    private boolean casHead(Waiter origin, Waiter newHead){
        return unsafe.compareAndSwapObject(this, headWaiterOffset, origin, newHead);
    }

}
