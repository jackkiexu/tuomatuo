package com.lami.tuomatuo.search.base.concurrent.executors;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by xjk on 11/12/16.
 */
public class ExecutorsTest {


    public static void main(String[] args) {
        ExecutorService scheduledExecutorService = Executors.newFixedThreadPool(100);

        scheduledExecutorService.isShutdown();
        List<Runnable> runnableList = scheduledExecutorService.shutdownNow();


        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 128, 1, TimeUnit.HOURS, new ArrayBlockingQueue<Runnable>(256), new ThreadPoolExecutor.DiscardPolicy());

    }

}
