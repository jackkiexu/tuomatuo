package com.lami.tuomatuo.cache.guava;

import com.google.common.cache.CacheStats;
import com.lami.tuomatuo.utils.spring.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by xjk on 2016/12/6.
 */
@Component
public class MyCacheManager {

	@Autowired
	private SpringContextUtil springContextUtil;

	private  Map<String, ? extends AbstractLocalCache<Object, Object>> cacheNameToObjectMap = null;

	/**
	 * fetch all AbstractLoadingCache subClass
	 * @return
	 */
	private  Map<String, ? extends AbstractLocalCache<Object, Object>> getCacheMap(){
		if(cacheNameToObjectMap==null){
			cacheNameToObjectMap = (Map<String, ? extends AbstractLocalCache<Object, Object>>) springContextUtil.getBeanOfType(AbstractLocalCache.class);
		}
		return cacheNameToObjectMap;

	}

	/**
	 *	fetch cache by cacheName
	 * @param cacheName
	 * @return
	 */
	private AbstractLocalCache<Object, Object> getCacheByName(String cacheName){
		return (AbstractLocalCache<Object, Object>) getCacheMap().get(cacheName);
	}
	
	/**
	 * get all cacheNames
	 * @return
	 */
	public  Set<String> getCacheNames() {
		return getCacheMap().keySet();
	}
	
	/**
	 * get all CacheStats
	 * @return List<Map<统计指标，统计数据>>
	 */
	public ArrayList<Map<String, Object>> getAllCacheStats() {
		
		Map<String, ? extends Object> cacheMap = getCacheMap();
		List<String> cacheNameList = new ArrayList<>(cacheMap.keySet());
		Collections.sort(cacheNameList);

		ArrayList<Map<String, Object>> list = new ArrayList<>();
		for(String cacheName : cacheNameList){
			list.add(getCacheStatsToMap(cacheName));
		}
		return list;
	}
	
	/**
	 * get CacheStats by cacheName
	 * @param cacheName
	 * @return Map
	 */
	private  Map<String, Object> getCacheStatsToMap(String cacheName) {
		Map<String, Object> map =  new LinkedHashMap<>();
		AbstractLocalCache<Object, Object> cache = getCacheByName(cacheName);
		CacheStats cs = cache.getCache().stats();
		NumberFormat percent = NumberFormat.getPercentInstance();
		percent.setMaximumFractionDigits(1);
		map.put("cacheName", cacheName);
		map.put("size", cache.getCache().size());
		map.put("maximumSize", cache.getMaximumSize());
		map.put("survivalDuration", cache.getExpireAfterWriteDuration());
		map.put("hitCount", cs.hitCount());
		map.put("hitRate", percent.format(cs.hitRate()));
		map.put("missRate", percent.format(cs.missRate()));
		map.put("loadSuccessCount", cs.loadSuccessCount());
		map.put("loadExceptionCount", cs.loadExceptionCount());
		map.put("totalLoadTime", cs.totalLoadTime()/1000000); 		//ms
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(cache.getResetTime()!=null){
			map.put("resetTime", df.format(cache.getResetTime()));
		}
		map.put("highestSize", cache.getHighestSize());
		if(cache.getHighestTime()!=null){
			map.put("highestTime", df.format(cache.getHighestTime()));	
		}
		
		return map;
	}
	
	/**
	 * clear the cache by cacheName
	 * @param cacheName
	 */
	public void resetCache(String cacheName){
		AbstractLocalCache<Object, Object> cache = getCacheByName(cacheName);
		cache.getCache().invalidateAll();
		cache.setResetTime(new Date());
	}
}
