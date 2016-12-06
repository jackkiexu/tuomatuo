package com.lami.tuomatuo.core.base;

/**
 * Created by xjk on 2016/10/17.
 */
public abstract class AbstractCallable<K, V> implements Callable<K, V> {

    public V call(K k) throws Exception {
        try {
            return execute(k);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                after();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    abstract protected Object before();
    abstract protected V execute(K k);
    abstract protected Object after();
}
