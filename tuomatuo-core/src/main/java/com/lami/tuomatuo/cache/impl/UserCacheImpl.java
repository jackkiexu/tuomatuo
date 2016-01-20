package com.lami.tuomatuo.cache.impl;

import com.lami.tuomatuo.cache.UserCache;
import com.lami.tuomatuo.model.co.UserCo;
import com.lami.tuomatuo.utils.GsonUtils;
import com.lami.tuomatuo.utils.constant.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by xujiankang on 2016/1/20.
 */
@Component( value = "userCache")
public class UserCacheImpl implements UserCache {

    @Autowired
    private ShardedJedisPool readPool;
    @Autowired
    private ShardedJedisPool writePool;


    public UserCo getUserCo(Long userId) {
        if (userId == null) return null;
        ShardedJedis redis = null;
        try {
            redis = readPool.getResource();
            String json = redis.hget(RedisConstant.USER_CACHE_HASH_SET, userId + "");
            if(json != null) return (UserCo)GsonUtils.getObjectFromJson(json, UserCo.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(redis != null){
                readPool.returnResource(redis);
            }
        }
        return null;
    }

    /**
     * 更新 UserCo 中的 GeoHash
     * @param userId
     * @param newGeoHash
     */
    public void updateGeoHash(Long userId, String newGeoHash) {
        if(userId == null || newGeoHash == null) return;
        ShardedJedis redis = null;
        try {
            redis = writePool.getResource();
            String json = redis.hget(RedisConstant.USER_CACHE_HASH_SET, userId + "");
            if(json == null) return;
            UserCo userCo = (UserCo)GsonUtils.getObjectFromJson(json, UserCo.class);
            userCo.setGeoHash(newGeoHash);
            redis.hset(RedisConstant.USER_CACHE_HASH_SET, userId+"", GsonUtils.toGson(userCo));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writePool.returnResource(redis);
        }
    }

    /**
     * 将 用户从 GeoHash Collection 移除
     * @param userId
     * @param sourceGeoHash
     */
    public void removeUserGeoHashCollection(Long userId, String sourceGeoHash) {
        if(userId == null || sourceGeoHash == null) return;
        ShardedJedis redis = null;
        Set<String> userList = new HashSet<String>();
        try {
            redis = writePool.getResource();
            String json = redis.hget(RedisConstant.USER_GEOHASH_COLLECTION, sourceGeoHash);
            if(json != null) userList = GsonUtils.getSetGson(json);
            userList.remove(userId + "");
            redis.hset(RedisConstant.USER_GEOHASH_COLLECTION, sourceGeoHash, GsonUtils.toGson(userList));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writePool.returnResource(redis);
        }
    }

    /**
     * 在 geoHash 对应的集合中加入 userId
     * @param geoHash
     * @param userId
     */
    public void addUserIdToGeoHashCollection(String geoHash, Long userId) {
        if(userId == null || geoHash == null) return;
        ShardedJedis redis = null;
        Set<String> userList = new HashSet<String>();
        try {
            redis = writePool.getResource();
            String json = redis.hget(RedisConstant.USER_GEOHASH_COLLECTION, geoHash);
            if(json != null) userList = GsonUtils.getSetGson(json);
            userList.add(userId + "");
            redis.hset(RedisConstant.USER_GEOHASH_COLLECTION, geoHash, GsonUtils.toGson(userList));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writePool.returnResource(redis);
        }
    }

    /**
     * 获取 geoHash 对应的 userId 的集合
     * @param geoHash
     * @return
     */
    public Set<String> getGeoHashCollection(String geoHash) {
        if(geoHash == null || geoHash == null) return new HashSet<String>();
        ShardedJedis redis = null;
        Set<String> userList = new HashSet<String>();
        try {
            redis = writePool.getResource();
            String json = redis.hget(RedisConstant.USER_GEOHASH_COLLECTION, geoHash);
            if(json != null) userList = GsonUtils.getSetGson(json);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writePool.returnResource(redis);
        }
        return userList;
    }
}
