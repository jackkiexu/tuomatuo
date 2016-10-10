package com.lami.tuomatuo.utils.executor;

import org.apache.log4j.Logger;

import java.util.concurrent.*;

/**
 * Created by xjk on 2016/10/10.
 */
public class RunnableFutureTask {

    private static final Logger logger = Logger.getLogger(RunnableFutureTask.class);

    /**
     * ExecutorService
     */
    static ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    /**
     * @param args
     */
    public static void main(String[] args) {
//        runnableDemo();
        futureDemo();
    }

    /**
     * runnable, 无返回值
     */
    static void runnableDemo() {

        new Thread(new Runnable() {

            public void run() {
                logger.info("runnable demo : " + fibc(20));
            }
        }).start();
    }

    /**
     * 其中Runnable实现的是void run()方法，无返回值；Callable实现的是 V
     * call()方法，并且可以返回执行结果。其中Runnable可以提交给Thread来包装下
     * ，直接启动一个线程来执行，而Callable则一般都是提交给ExecuteService来执行。
     */
    static void futureDemo() {
        try {
            /**
             * 提交runnable则没有返回值, future没有数据
             */
            Future<?> result = mExecutor.submit(new Runnable() {

                public void run() {
                    fibc(20);
                    try {
                        Thread.sleep(3000);
                        logger.info("runnable OK");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            logger.info("future result from runnable : " + result.get());


            /**
             * 提交Callable, 有返回值, future中能够获取返回值
             */
            Future<Integer> result2 = mExecutor.submit(new Callable<Integer>() {
                public Integer call() throws Exception {

                    int result =  fibc(20);
                    try {
                        Thread.sleep(3000);
                        logger.info("callback OK");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return result;
                }
            });

            logger.info("future result from callable : " + result2.get());

            /**
             * FutureTask则是一个RunnableFuture<V>，即实现了Runnbale又实现了Futrue<V>这两个接口，
             * 另外它还可以包装Runnable(实际上会转换为Callable)和Callable
             * <V>，所以一般来讲是一个符合体了，它可以通过Thread包装来直接执行，也可以提交给ExecuteService来执行
             * ，并且还可以通过v get()返回执行结果，在线程体没有执行完成的时候，主线程一直阻塞等待，执行完则直接返回结果。
             */
            FutureTask<Integer> futureTask = new FutureTask<Integer>(
                    new Callable<Integer>() {
                        public Integer call() throws Exception {
                            int result =  fibc(20);
                            try {
                                Thread.sleep(3000);
                                logger.info("futureTask OK");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return result;
                        }
                    });
            // 提交futureTask
            mExecutor.submit(futureTask);
            logger.info("future result from futureTask : " + futureTask.get());

            mExecutor.shutdown();
            logger.info("mExecutor.isTerminated():"+mExecutor.isTerminated());
            if(!mExecutor.isTerminated()){
                mExecutor.awaitTermination(1, TimeUnit.NANOSECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 效率底下的斐波那契数列, 耗时的操作
     *
     * @param num
     * @return
     */
    static int fibc(int num) {
        if (num == 0) {
            return 0;
        }
        if (num == 1) {
            return 1;
        }

       /* System.out.println("子线程在进行计算");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return fibc(num - 1) + fibc(num - 2);
    }

}
