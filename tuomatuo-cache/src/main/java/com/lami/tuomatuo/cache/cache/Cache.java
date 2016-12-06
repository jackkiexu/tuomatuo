package com.lami.tuomatuo.cache.cache;

import com.lami.tuomatuo.cache.executors.DirectExecutorService;
import org.apache.log4j.Logger;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by xjk on 2016/12/6.
 */
public class Cache<K, V> {

    private static final Logger logger = Logger.getLogger(Cache.class);

    private Compute<K, V> compute;
    private Map<K, SoftValue<K, Future<V>>> map;
    private ExecutorService executor;
    private int CACHE_SIZE;
    private ExceptionStrategy<K> exceptionStrategy;
    private ReferenceQueue<Future<V>> referenceQueue = new ReferenceQueue<>();

    public Cache(Compute<K, V> compute, int size) {
        this(compute, new DirectExecutorService(), size);
    }

    /**
     *
     * @param compute       procedure to compute the value
     * @param executor      Do not pass direct executor or less you will have deadlock
     * @param size          the size of the cache
     */
    public Cache(Compute<K, V> compute, ExecutorService executor, int size) {
        this.CACHE_SIZE = size;
        this.compute = compute;
        this.exceptionStrategy = ExceptionStrategies.alwaysRetain();
        this.executor = executor;

        this.map = new LinkedHashMap<K, SoftValue<K, Future<V>>>(CACHE_SIZE, 0.75f, true){
            private static final long serialVersionUID = -676712291765286574L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, SoftValue<K, Future<V>>> eldest) {
                boolean ret = CACHE_SIZE < size();
                if(ret){
                    logger.info("Evict " + eldest.getKey());
                }
                return ret;
            }
        };
    }


    public synchronized SoftValue<K, Future<V>> remove(K key){
        processQueue();
        return map.remove(key);
    }

    public synchronized void clear(){
        processQueue();
        map.clear();
    }

    public V get(final K key) throws Throwable{
        try {
            return getTask(key).get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private synchronized Future<V> getTask(final K key){
        processQueue();
        Future<V> ret;
        SoftReference<Future<V>> sr = map.get(key);
        if(sr != null){
            ret = sr.get();
            if(ret != null){
                return ret;
            }
        }
        ret = executor.submit(() -> compute.compute(key));
        SoftValue<K, Future<V>> value = new SoftValue<>(ret, referenceQueue, key);
        map.put(key, value);
        return ret;
    }

    private void processQueue(){
        while(true){
            Reference<? extends Future<V>> o = referenceQueue.poll();
            if(null == o){
                return;
            }
            SoftValue<K, Future<V>> k = (SoftValue<K, Future<V>>) o;
            K key = k.key;
            map.remove(key);
        }
    }


    public void setExceptionStrategy(ExceptionStrategy<K> exceptionStrategy){
        this.exceptionStrategy = exceptionStrategy;
    }

    public static class SoftValue<K, V> extends SoftReference<V> {
        K key;

        public SoftValue(V referent, ReferenceQueue<V> q, K key) {
            super(referent);
            this.key = key;
        }
    }
}
