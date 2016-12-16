package com.lami.tuomatuo.search.base.concurrent.future.example;

import org.apache.log4j.Logger;

import java.util.ArrayList;
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

        List<Future<?>> futureList = new ArrayList<>();

        Future<?> future = localCacheConnection.getResult("connection : 0" );
        for(int i = 1; i < 3 ; i++){
            final Random random = new Random(1000);
            new Thread(){
                @Override
                public void run() {
                    try {

                        logger.info("future.get() : " + future.get(random.nextInt(10) , TimeUnit.SECONDS));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }


    }

}
