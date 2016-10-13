package com.ailami.tuomatuo;

/**
 * http://blog.gssxgss.me/concurrency-cache-and-guava/
 * Created by xjk on 2016/10/13.
 */
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memorie<K, V> implements Computable<K, V> {
    private final ConcurrentHashMap<K, Future<V>> cache =
            new ConcurrentHashMap<K, Future<V>>();
    private final Computable<K, V> c;

    public Memorie(Computable<K, V> c) {
        this.c = c;
    }

    public V compute(final K k) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(k);
            if (f == null) {
                FutureTask<V> ft = new FutureTask<V>(new Callable<V>() {

                    public V call() throws Exception {
                        return c.compute(k);
                    }

                });
                f = cache.putIfAbsent(k, ft); // if the key-value not exist in the map, then return null
                if (f == null) {
                    f = ft;
                    ft.run();
                }
            }
            try {
                return f.get();
            } catch (CancellationException e) {
                cache.remove(k);
            } catch (ExecutionException e) {
                throw lanuderThrowable(e.getCause());
            }
        }
    }

    public static RuntimeException lanuderThrowable(Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        } else if (t instanceof Error) {
            throw new Error();
        } else {
            throw new IllegalStateException("Not Unchecked", t);
        }
    }

}