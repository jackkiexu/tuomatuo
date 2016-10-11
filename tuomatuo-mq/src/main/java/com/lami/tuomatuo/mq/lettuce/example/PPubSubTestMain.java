package com.lami.tuomatuo.mq.lettuce.example;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

/**
 * Created by xjk on 2016/9/12.
 */
public class PPubSubTestMain {

    private static final Logger logger = Logger.getLogger(PPubSubTestMain.class);

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        PPubClient pubClient = new PPubClient(Constants.host, Constants.port);
        final String channel = "pubsub-channel-p";
        final PSubClient subClient = new PSubClient(Constants.host, Constants.port,"subClient-1");
        Thread subThread = new Thread(new Runnable() {

            public void run() {
                logger.info("----------subscribe operation begin-------");
                //在API级别，此处为轮询操作，直到unsubscribe调用，才会返回
                subClient.sub(channel);
                logger.info("----------subscribe operation end-------");

            }
        });
        new PSubClient(Constants.host, Constants.port,"subClient-1");
        subThread.setDaemon(true);
        subThread.start();
        int i = 0;
        while(i < 99999999999l){
            String message = RandomStringUtils.random(64, true, true);//apache-commons
            pubClient.pub(channel, message);
            i++;
            Thread.sleep(10);
        }
        subClient.unsubscribe(channel);
    }

}