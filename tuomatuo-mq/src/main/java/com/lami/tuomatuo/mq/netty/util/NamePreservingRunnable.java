package com.lami.tuomatuo.mq.netty.util;

import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2016/9/28.
 */
public class NamePreservingRunnable implements Runnable {

    private static Logger logger = Logger.getLogger(NamePreservingRunnable.class);

    private String newName;
    private Runnable runnable;

    public NamePreservingRunnable(Runnable runnable, String newName) {
        this.newName = newName;
        this.runnable = runnable;
    }

    public void run() {
        Thread currentThread = Thread.currentThread();
        String oldName = currentThread.getName();

        if(newName != null){
            setName(currentThread, newName);
        }

        try {
            runnable.run();
        } finally {
            setName(currentThread, oldName);
        }

    }

    private void setName(Thread thread, String name){
        thread.setName(name);
    }
}
