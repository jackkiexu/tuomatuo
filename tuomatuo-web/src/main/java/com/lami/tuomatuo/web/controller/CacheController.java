package com.lami.tuomatuo.web.controller;

/**
 * Created by xjk on 2016/12/6.
 */
import com.lami.tuomatuo.cache.guava.MyCacheManager;
import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.web.model.po.CacheParam;
import com.lami.tuomatuo.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * localcache statistic check reset
 * Created by xjk on 2016/12/6.
 */
@RequestMapping("/cache")
public class CacheController extends BaseController {

    @Autowired
    private MyCacheManager cacheManager;

    @Override
    protected boolean checkAuth() {
        return false;
    }

    /**
     *  get cacheStats by cacheName
     * @param httpServletRequest
     * @param param
     * @return
     */
    @RequestMapping(value = "/stats.form")
    @ResponseBody
    public Result cacheStats(HttpServletRequest httpServletRequest, @RequestBody CacheParam param){
        Result result = new Result(Result.SUCCESS);
        switch(param.getCacheName()){
            case "*":
                result.setValue(GsonUtils.toGson(cacheManager.getAllCacheStats()));
            default:
                break;
        }
        return result;
    }

    /**
     * reset the cache by the cacheName
     * @param httpServletRequest
     * @param param
     * @return
     */
    @RequestMapping(value = "/reset.form")
    @ResponseBody
    public Result cacheReset(HttpServletRequest httpServletRequest, @RequestBody CacheParam param){
        Result result = new Result(Result.SUCCESS);
        cacheManager.resetCache(param.getCacheName());
        return result;
    }


}
