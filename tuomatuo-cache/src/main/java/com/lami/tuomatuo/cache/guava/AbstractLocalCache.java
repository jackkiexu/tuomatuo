package com.lami.tuomatuo.cache.guava;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * http://blog.csdn.net/clementad/article/details/46491701
 * Created by xjk on 2016/12/6.
 */
@Data
public abstract class AbstractLocalCache<K, V> {
	protected final Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
	
	// the default max cache size
	private int maximumSize = 50 * 1000;

	// the default cache expire time
	private int expireAfterWriteDuration = 60;

	// the default time unit
	private TimeUnit timeUnit = TimeUnit.MINUTES;

	// cache reset time just for record
	private Date resetTime;

	// the highest size
	private long highestSize=0;

	private Date highestTime;
	
	private LoadingCache<K, V> cache;
	
	/**
	 * init cache core
	 * @return cache
	 */
	public LoadingCache<K, V> getCache() {
		if(cache == null){
			synchronized (this) {
				if(cache == null){
					cache = CacheBuilder.newBuilder().maximumSize(maximumSize)
							.expireAfterWrite(expireAfterWriteDuration, timeUnit)
							.recordStats()
							.build(new CacheLoader<K, V>() {
								@Override
								public V load(K key) throws Exception {
									return fetchData(key);
								}
							});
					this.resetTime = new Date();
					this.highestTime = new Date();
					logger.info("local cache init success " + this.getClass().getSimpleName());
				}
			}
		}
		return cache;
	}
	
	/**
	 * get value according the key
	 * @param key
	 * @return value
	 */
	protected abstract V fetchData(K key);

	/**
	 * get V by K
	 * @param key
	 * @return Value
	 * @throws ExecutionException 
	 */
	protected V getValue(K key) throws ExecutionException {
		V result = getCache().get(key);
		if(getCache().size() > highestSize){
			highestSize = getCache().size();
			highestTime = new Date();
		}
		return result;
	}
}
