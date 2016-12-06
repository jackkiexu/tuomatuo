package com.lami.tuomatuo.cache.guava;

import lombok.Data;

/**
 * Created by xjk on 2016/12/6.
 */
@Data
public class MyCacheResult {
    private String cacheName;
    private Long size;
    private Long maximumSize;
    private Long survivalDuration;
    private Long hitCount;
    private Double hitRate;
    private Double missRate;
    private Long loadSuccessCount;
    private Long loadExceptionCount;
    private String totalLoadTime;
    private String resetTime;
    private Long highestSize;

    public MyCacheResult(AbstractLocalCache<?, ?> localCache){

    }
}
