package com.lami.tuomatuo.search.base.concurrent.future.example;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by xujiankang on 2016/12/16.
 */
public class FutureMain {

    private static final Logger logger = Logger.getLogger(FutureMain.class);

    public static void main(String[] args) throws Exception{

        LocalCacheConnection localCacheConnection = new LocalCacheConnection();
        Future<?> future = localCacheConnection.getResult("connection");

        new Thread(){
            @Override
            public void run() {
                try {
                    logger.info("future.get() : " + future.get(2 , TimeUnit.SECONDS));
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    logger.info("future.get() over");
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                try {
                    logger.info("future.get() : " + future.get(4, TimeUnit.SECONDS));
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    logger.info("future.get() over");
                }
            }
        }.start();
    }
}
