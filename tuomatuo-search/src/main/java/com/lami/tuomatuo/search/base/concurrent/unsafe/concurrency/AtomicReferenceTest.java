package com.lami.tuomatuo.search.base.concurrent.unsafe.concurrency;

import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by xjk on 2016/5/13.
 */
public class AtomicReferenceTest {

    public static void main(String[] args) {
        // 创建两个 Person 对象, 它们的 id 分别是 101 和102
        Person p1 = new Person(101);
        Person p2 = new Person(102);
        // 新建 AtomicReference ar = new AtomicReference(p1)
        AtomicReference ar = new AtomicReference(p1);
        // 通过 CAS 设置 ar, 如果 ar 的值为 p1 的话, 则将其设置为 p2
        ar.compareAndSet(null, p2);

        Person p3 = (Person)ar.get();
        System.out.println("P3 is " + p3);
        System.out.println("p3.equals(p1) = " + p3.equals(p1));
    }

}

@Data
class Person {
    volatile long id;

    public Person(long id) {
        this.id = id;
    }
}
