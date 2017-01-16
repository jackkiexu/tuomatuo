package com.lami.tuomatuo.search.base.concurrent.concurrentlinkedqueue;

import com.lami.tuomatuo.search.base.concurrent.unsafe.UnSafeClass;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

/**
 * 参考
 * http://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf
 *
 * 非阻塞的链表, 轻量级的 ConcurrentLinkedList
 *
 * ConcurrentLinkedList 中的 tail 节点有时会滞后于 head 节点, 而这里 head永远指向 dummy节点
 *
 * 整个队列有三种状态
 * 1. 初始化状态 head指向头节点(dummy node), tail指向尾节点(dummy node)
 * 2. 中间状态 head指向头节点(dummy node), tail指向队列中的第二后节点(此时 tail.next != null)
 * 3. 静止状态 head指向头节点(dummy node), tail指向队列中的第二后节点(此时 tail.next = null)
 *
 * Created by xjk on 1/15/17.
 */
public class NonBlockingLinkedList<E> {

    private static final Logger logger = Logger.getLogger(NonBlockingLinkedList.class);

    private volatile Node<E> head;
    private volatile Node<E> tail;

    public NonBlockingLinkedList() {
        head = tail = new Node<E>(null);
    }

    /**
     * 队列加入节点
     * @param item
     */
    public boolean enqueue(E item){
        if(item == null) throw new NullPointerException();
        Node<E> newNode = new Node<E>(item);

        while(true){
            Node<E> curlTail = tail;                // 1. 获取队列的tail
            Node<E> tailNext = curlTail.next;       // 2. 获取队列 tail节点的next(当队列处于)
            if(curlTail == tail){                   // 3. 判断在执行第1步与第3步之间有没有其他线程改变队列的 tail 指针
                if(tailNext == null){               // 4. 判断 队列是否处于中间状态, 若处于中间状态, 则执行第5步 来进行修复 -> 变成静止状态
                    if(tail.casNext(null, newNode)){// 5. cas 设置 tail的next, 失败的话再来, 直到成功
                        casTail(tail, newNode);     // 6. cas 设置队列的 tail, 这个操作可能失败, 因为此时可能有另外一个线程通过第7步已经执行好了, 所以失败也直接返回true
                        return true;
                    }
                }else{
                    casTail(tail, tailNext);        // 7. 帮助队列进入"静止状态"
                }
            }
        }

    }

    public Node<E> dequeue(){
        Node<E> result = null;

        while(true){
            Node<E> headNode = head;
            Node<E> tailNode = tail;
            Node<E> headNextNode = head.next;
            if(headNode == head){
                if(headNode == tailNode){
                    if(headNextNode == null) return null;
                    if(casHead(headNode, headNextNode)) return headNode;
                }
                else{
                    if(casHead(headNode, headNextNode)) return headNode;
                }

            }
        }
    }

    /** NonBlockingLinkedList 的 cas 操作 */

    /** 设置队列的尾节点 */
    private boolean casHead(Node<E> cmp, Node<E> value){
        return unsafe.compareAndSwapObject(this, headOffset, cmp, value);
    }

    /** 设置队列的尾节点 */
    private boolean casTail(Node<E> cmp, Node<E> value){
        return unsafe.compareAndSwapObject(this, tailOffset, cmp, value);
    }

    private static final Unsafe unsafe;
    private static final long headOffset;
    private static final long tailOffset;

    static {
        try {
            unsafe = UnSafeClass.getInstance();
            Class<?> k = NonBlockingLinkedList.class;
            headOffset = unsafe.objectFieldOffset(k.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(k.getDeclaredField("tail"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

}
