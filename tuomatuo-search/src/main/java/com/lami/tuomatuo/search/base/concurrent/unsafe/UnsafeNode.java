package com.lami.tuomatuo.search.base.concurrent.unsafe;

import sun.misc.Unsafe;

/**
 * http://www.cnblogs.com/zhanjindong/p/java-concurrent-package-aqs-locksupport-and-thread-interrupt.html
 * http://www.cnblogs.com/wanly3643/p/3992186.html
 *
 * Created by xjk on 12/28/16.
 */
public class UnsafeNode {

    private volatile UnsafeNode next;
    private volatile Thread waiter;
    private volatile Object value;

    // unsafe mechanics
    private static final Unsafe unsafe;
    private static final long nextOffset;
    private static final long waiterOffset;
    private static final long valueOffset;

    public void setValue(String value){
        unsafe.compareAndSwapObject(this, valueOffset, null, value);
    }

    static {
        try{
            unsafe = UnSafeClass.getInstance();
            Class<?> k = UnsafeNode.class;
            nextOffset = unsafe.objectFieldOffset(k.getDeclaredField("next"));
            waiterOffset = unsafe.objectFieldOffset(k.getDeclaredField("waiter"));
            valueOffset = unsafe.objectFieldOffset(k.getDeclaredField("value"));
        }catch (Exception e){
            throw new Error(e);
        }
    }

    public static void main(String[] args) {
        UnsafeNode unsafeNode = new UnsafeNode();
        Object value = unsafeNode.value;
        new Thread(){
            @Override
            public void run() {
                for(;;){
                    System.out.println(value);
                    try {
                        Thread.sleep(2*100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
        unsafeNode.setValue("1000");

        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
