package com.lami.tuomatuo.mq.jafka.network;

import com.lami.tuomatuo.mq.jafka.utils.Closer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xujiankang on 2016/9/30.
 */
public abstract class AbstractServerThread  implements Runnable{

    protected Logger logger = Logger.getLogger(getClass());

    private Selector selector;
    protected final CountDownLatch startupLatch = new CountDownLatch(1);
    protected final CountDownLatch shutdownLatch = new CountDownLatch(1);
    protected final AtomicBoolean alive = new AtomicBoolean(false);

    public Selector getSelector(){
        if(selector == null){
            try {
                selector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return selector;
    }

    protected void closeSelector(){
        Closer.closeQuietly(selector, logger);
    }

    public void shutdown() throws InterruptedException{
        alive.set(false);
        selector.wakeup();
        shutdownLatch.await();
    }

    protected void startupComplete(){
        alive.set(true);
        startupLatch.countDown();
    }

    protected void shutdownComplete(){
        shutdownLatch.countDown();
    }

    protected boolean isRunning(){
        return  alive.get();
    }

    public void awaitStartup() throws InterruptedException{
        startupLatch.await();
    }


}
