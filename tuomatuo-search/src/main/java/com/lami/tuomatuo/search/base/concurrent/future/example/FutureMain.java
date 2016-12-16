package com.lami.tuomatuo.search.base.concurrent.future.example;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by xujiankang on 2016/12/16.
 */
public class FutureMain {

    private static final Logger logger = Logger.getLogger(FutureMain.class);

    public static void main(String[] args) throws Exception{

        LocalCacheConnection localCacheConnection = new LocalCacheConnection();

        List<Future<?>> futureList = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            try {
                futureList.add(localCacheConnection.getResult("connection :" + i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < 3; i++){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    for(Future<?> future : futureList){
                        try {
                            logger.info("future.get() :" + future.get());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }


        new CountDownLatch(1).await();
    }

}
