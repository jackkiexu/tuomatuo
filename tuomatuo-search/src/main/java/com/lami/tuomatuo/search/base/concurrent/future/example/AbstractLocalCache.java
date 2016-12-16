package com.lami.tuomatuo.search.base.concurrent.future.example;

import com.lami.tuomatuo.search.base.concurrent.future.FutureTask;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by xujiankang on 2016/12/16.
 */
public abstract class AbstractLocalCache<K, V> {

    protected Logger logger = Logger.getLogger(getClass());

    /** 缓存过期时间, 单位秒 */
    private long valueDefaultExpireTime = 10;

    /** 本地缓存默认最大值*/
    private static long LOCAL_CACHE_MAX_SIZE = 10 * 1000l;

    /** 本地缓存存储地址 */
    private ConcurrentHashMap<K, Future<V>> pool = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    public abstract V computeV(K k);

    public Future<V> getResult(K k){
        Future<V> result = null;
        if(pool.containsKey(k)){
            return pool.get(k);
        }
        FutureTask<V> future = new FutureTask<V>(new java.util.concurrent.Callable<V>() {
            @Override
            public V call() throws Exception {
                return computeV(k);
            }
        });

        if(pool.putIfAbsent(k, future) == null){ // 说明map中以前没有对应的 futureTask
//            result = (Future<V>) executorService.submit(future);
            new Thread(){
                @Override
                public void run() {
                    future.run();
                }
            }.start();

            result = future;
        }
        return result;
    }

}
