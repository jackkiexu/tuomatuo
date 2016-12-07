package com.lami.tuomatuo.web.controller.cache;

/**
 * Created by xjk on 2016/12/6.
 */
import com.lami.tuomatuo.cache.dao.AreaLocalCache;
import com.lami.tuomatuo.cache.guava.MyCacheManager;
import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.utils.StringUtil;
import com.lami.tuomatuo.web.controller.BaseController;
import com.lami.tuomatuo.web.model.po.CacheParam;
import com.lami.tuomatuo.utils.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * localcache statistic check reset
 * Created by xjk on 2016/12/6.
 */
@Controller
@RequestMapping("/cache")
public class CacheController extends BaseController {

    @Autowired
    private MyCacheManager cacheManager;
    @Autowired
    private AreaLocalCache areaLocalCache;

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
    @RequestMapping(value = "/initData.form")
    @ResponseBody
    public Result cacheStats(HttpServletRequest httpServletRequest, Integer count){
        Result result = new Result(Result.SUCCESS);
        if(count == null) count = 100;
        Random random = new Random(1000);
        for(int i = 0; i < count; i++){
            areaLocalCache.get(random.nextInt());
        }
        result.setValue(GsonUtils.toGson(cacheManager.getAllCacheStats()));
        logger.info("result : " + result);
        return result;
    }

    /**
     *  get cacheStats by cacheName
     * @param httpServletRequest
     * @param param
     * @return
     */
    @RequestMapping(value = "/stats.form")
    @ResponseBody
    public Result cacheStats(HttpServletRequest httpServletRequest){
        Result result = new Result(Result.SUCCESS);
        result.setValue(GsonUtils.toGson(cacheManager.getAllCacheStats()));
        logger.info("result : " + result);
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
