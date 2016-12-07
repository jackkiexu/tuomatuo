package com.lami.tuomatuo.cache.dao;

import com.lami.tuomatuo.cache.guava.AbstractLocalCache;
import com.lami.tuomatuo.cache.guava.LocalCache;
import com.lami.tuomatuo.cache.model.Area;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * Created by xjk on 2016/12/7.
 */
@Component
public class AreaLocalCache extends AbstractLocalCache<Integer, Area> implements LocalCache<Integer, Area> {

    public AreaLocalCache() {
        setCacheName(AreaLocalCache.class.getName());
        setMaximumSize(50*1000);
    }

    @Override
    protected Area fetchData(Integer key) {
        Area a = new Area();
        try {
            a.setCode(key);
            a.setId(key);
            a.setName("地区：" + key);
            a.setParentCode(Integer.valueOf(key.toString().substring(0, key.toString().length() - 3)));
            a.setPinyin("pinyin:" + key);
            logger.info("fetchData:" + a);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return a;
    }

    @Override
    public Area get(Integer key) {
        try {
            return getValue(key);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
