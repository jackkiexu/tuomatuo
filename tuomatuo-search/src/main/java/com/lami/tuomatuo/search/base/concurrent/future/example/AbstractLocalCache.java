package com.lami.tuomatuo.search.base.concurrent.future.example;

import org.apache.log4j.Logger;

import java.util.concurrent.*;


/**
 * Created by xujiankang on 2016/12/16.
 */
public abstract class AbstractLocalCache<K, V> {

    protected Logger logger = Logger.getLogger(getClass());

    private ConcurrentHashMap<K, Future<V>> pool = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    public abstract V computeV(K k);

    public Future<V> getResult(K k){
        Future<V> result = null;
        if(pool.containsKey(k)){
            return pool.get(k);
        }
        FutureTask<V> future = new FutureTask<V>(new Callable<V>() {
            @Override
            public V call() throws Exception {
                return computeV(k);
            }
        });

        if(pool.putIfAbsent(k, future) == null){ // 说明map中以前没有对应的 futureTask
            result = (Future<V>) executorService.submit(future);
        }
        return result;
    }

}
