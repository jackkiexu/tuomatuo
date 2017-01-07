package com.lami.tuomatuo.search.base.concurrent.threadlocal;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class provides thread-local variables. These variables differ from
 * their normal counterparts in that each thread that access one (via its
 * {@code get} or {@code set} method) has its own.idependently initialized
 * copy of the variable. {@code ThreadLocal} instances are typically private
 * static fields in classes that wish to associate state with a thread (e.g,
 * a user ID or Transaction ID).
 *
 * <p>
 *     For example, the class below generates unique identifiers local to each
 *     thread
 *     A thread's id is assigned the first time it invokes {@code ThreadId.get()}
 *     and remains unchanged on subsequent calls
 * </p>
 *
 *  public class ThreadId{
 *      // Atomic Integer containing the next threadID to be assigned
 *      private static final AtomicInteger nextid = new AtomicInteger(0);
 *
 *      // Thread local variable containing each thread's ID
 *      private static final ThreadLocal
 *  }
 *
 * <p>
 *     Each thread holds an implicit reference to its copy of a thread-local
 *     variable as long as the thread is alive and {@code KThreadlocal}
 *     instance is accessible: after a thread goes aways, all of its copies of
 *     thread-local instances are subject to garbage collection (unless other
 *     references to these copies exist)
 * </p>
 *
 * Created by xujiankang on 2017/1/6.
 */
public class KThreadLocal<T> {

    /** The next hash code to be given out, Updated atomically, Starts at zero */
    private static AtomicInteger nextHashCode = new AtomicInteger();

    /**
     * ThreadLocalMap is a customed hash map suitable only for maintaining
     * thread local values. No operations are exported
     * outside of the ThreadLocal class. The class is package private to
     * allow declaration of fields in class Thread. To help deal with
     * vary large and long-lived usages. the hash table entries use
     * WeakReferences for keys. However, since reference queues are not
     * used, stale entries are guaranteed to be removed only when
     * the table starts running out of space
     */
    static class ThreadLocalMap{

    }


}
