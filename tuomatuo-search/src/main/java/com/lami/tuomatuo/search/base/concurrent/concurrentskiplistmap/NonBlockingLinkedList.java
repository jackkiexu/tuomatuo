package com.lami.tuomatuo.search.base.concurrent.concurrentskiplistmap;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import sun.misc.Unsafe;

import java.util.AbstractMap;

/**
 * 非阻塞链表的实现, 基于 ConcurrentSkipListMap
 * 功能:
 *      1. 节点 poll(头节点), push(push到尾节点)
 *      2. 中间节点删除, 中间节点添加 (ConcurrentLinkedQueue 不支持的)
 * 特点:
 *      删除中间节点时增加 marker 节点, 以实现 并发的中间节点插入
 *
 * Created by xujiankang on 2017/1/20.
 */
public class NonBlockingLinkedList<K, V> {

    private transient volatile Node<K, V> head;

    private transient volatile Node<K, V> tail;




    static final class Node<K, V>{
        final K key;
        volatile Object value;
        volatile Node<K, V> next;

        public Node(K key, Object value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public Node(Node<K, V> next) {
            this.key = null;
            this.value = this;
            this.next = next;
        }

        boolean casValue(Object cmp, Object val){
            return unsafe.compareAndSwapObject(this, valueOffset, cmp, val);
        }

        boolean casNext(Node<K, V> cmp, Node<K, V> val){
            return unsafe.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        boolean isMarker(){
            return value == this;
        }

        boolean appendMarker(Node<K, V> f){
            return casNext(f, new Node<K, V>(f));
        }

        void helpDelete(Node<K, V> b, Node<K, V> f){
            if(f == next && this == b.next){
                if(f == null || f.value != f){ // 还没有对删除的节点进行节点 marker
                    casNext(f, new Node<K, V>(f));
                }else{
                    b.casNext(this, f.next); // 删除 节点 b 与 f.next 之间的节点
                }
            }
        }

        // UNSAFE mechanics
        private static final Unsafe unsafe;
        private static final long valueOffset;
        private static final long nextOffset;

        static {
            try {
                unsafe = UnSafeClass.getInstance();
                Class<?> k = Node.class;
                valueOffset = unsafe.objectFieldOffset(k.getDeclaredField("value"));
                nextOffset = unsafe.objectFieldOffset(k.getDeclaredField("next"));
            }catch (Exception e){
                throw new Error(e);
            }
        }

    }
}
