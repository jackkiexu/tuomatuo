package com.lami.tuomatuo.mq.netty.example.echo;

import org.apache.log4j.Logger;

/**
 * Created by xujiankang on 2016/9/29.
 */
public class ThroughputMonitor extends Thread {

    private static final Logger logger = Logger.getLogger(ThroughputMonitor.class);

    private EchoHandler echoHandler;

    public ThroughputMonitor(EchoHandler echoHandler) {
        this.echoHandler = echoHandler;
    }

    @Override
    public void run() {
        long oldCounter = echoHandler.getTransferredNytes();
        long startTime = System.currentTimeMillis();
        for(;;){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();
            long newCounter = echoHandler.getTransferredNytes();

            logger.info(String.format("%4.3f MiB/s%n", (newCounter - oldCounter) * 1000 / (endTime - startTime) / 1048576.0));
            oldCounter = newCounter;
            startTime = endTime;
        }

    }
}
