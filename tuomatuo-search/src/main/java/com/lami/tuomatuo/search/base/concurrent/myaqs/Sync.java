package com.lami.tuomatuo.search.base.concurrent.myaqs;

import sun.misc.Unsafe;

/**
 * Created by xjk on 12/13/16.
 */
public class Sync<E> {

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private volatile Waiter         headWaiter;
    private volatile Waiter         tailWaiter;
    private static long             headWaiterOffset;
    private static long             tailWaiterOffset;




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
        hea
    }
}
