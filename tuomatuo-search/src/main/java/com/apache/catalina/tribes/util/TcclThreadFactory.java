package com.apache.catalina.tribes.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactory implementation that creates threads with the thread context
 * class loader set to the class loader that loaded this factory. It is intended
 * to be used when tasks may be passed to executors when the web application
 * class loader is set as the thread context class loader. such as in async
 * session replication
 *
 * Created by xjk on 3/12/17.
 */
public class TcclThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private static final boolean IS_SECURITY_ENABLED = (System.getSecurityManager() != null);

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public TcclThreadFactory() {
        this("pool-" + poolNumber.getAndIncrement() + "-thread-");
    }

    public TcclThreadFactory(String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {

        final Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());

        if(IS_SECURITY_ENABLED){
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    t.setContextClassLoader(this.getClass().getClassLoader());
                    return null;
                }
            });
        }else{
            t.setContextClassLoader(this.getClass().getClassLoader());
        }
        t.setDaemon(true);
        return t;
    }

}
