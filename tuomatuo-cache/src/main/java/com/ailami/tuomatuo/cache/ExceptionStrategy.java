package com.ailami.tuomatuo.cache;

/**
 * Created by xjk on 2016/12/6.
 */
public interface ExceptionStrategy<K> {

    /**
     *
     * @param key           the key of the value throws exception
     * @param throable     the exception that was thrrown
     * @param <T>           true if this &lt; key, throwable &gt; pair should not be cached
     * @return
     */
    <T extends Throwable> boolean removeEntry(K key, T throable);

}
