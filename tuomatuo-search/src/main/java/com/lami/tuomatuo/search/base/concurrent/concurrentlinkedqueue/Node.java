package com.lami.tuomatuo.search.base.concurrent.concurrentlinkedqueue;


import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

/**
 * http://hg.openjdk.java.net/jdk7/jdk7/jdk/file/9b8c96f96a0f/src/share/classes/sun/misc/Unsafe.java
 * http://hg.openjdk.java.net/jdk7/jdk7/hotspot/file/9b0ca45cd756/src/share/vm/prims/unsafe.cpp
 * http://mishadoff.com/blog/java-magic-part-4-sun-dot-misc-dot-unsafe/
 *
 * Created by xjk on 1/13/17.
 */
public class Node<E> {
    volatile E item;
    volatile Node<E> next;

    Node(E item){
        /**
         * Stores a reference value into a given Java variable.
         * <p>
         * Unless the reference <code>x</code> being stored is either null
         * or matches the field type, the results are undefined.
         * If the reference <code>o</code> is non-null, car marks or
         * other store barriers for that object (if the VM requires them)
         * are updated.
         * @see #putInt(Object, int, int)
         *
         * 将 Node 对象的指定 itemOffset 偏移量设置 一个引用值
         */
        unsafe.putObject(this, itemOffset, item);
    }

    boolean casItem(E cmp, E val){
        /**
         * Atomically update Java variable to <tt>x</tt> if it is currently
         * holding <tt>expected</tt>.
         * @return <tt>true</tt> if successful
         * 原子性的更新 item 值
         */
        return unsafe.compareAndSwapObject(this, itemOffset, cmp, val);
    }

    void lazySetNext(Node<E> val){
        /**
         * Version of {@link #putObjectVolatile(Object, long, Object)}
         * that does not guarantee immediate visibility of the store to
         * other threads. This method is generally only useful if the
         * underlying field is a Java volatile (or if an array cell, one
         * that is otherwise only accessed using volatile accesses).
         *
         * 调用这个方法和putObject差不多, 只是这个方法设置后对应的值的可见性不一定得到保证,
         * 这个方法能起这个作用, 通常是作用在 volatile field上, 也就是说, 下面中的参数 val 是被volatile修饰
         */
        unsafe.putOrderedObject(this, nextOffset, val);
    }

    /**
     * Atomically update Java variable to <tt>x</tt> if it is currently
     * holding <tt>expected</tt>.
     * @return <tt>true</tt> if successful
     *
     * 原子性的更新 nextOffset 上的值
     *
     */
    boolean casNext(Node<E> cmp, Node<E> val){
        return unsafe.compareAndSwapObject(this, nextOffset, cmp, val);
    }

    private static Unsafe unsafe;
    private static long itemOffset;
    private static long nextOffset;

    static {
        try {
            unsafe = UnSafeClass.getInstance();
            Class<?> k = Node.class;
            itemOffset = unsafe.objectFieldOffset(k.getDeclaredField("item"));
            nextOffset = unsafe.objectFieldOffset(k.getDeclaredField("next"));
        }catch (Exception e){

        }
    }
}
