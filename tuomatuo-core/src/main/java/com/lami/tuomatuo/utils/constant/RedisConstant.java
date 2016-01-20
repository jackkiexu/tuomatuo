package com.lami.tuomatuo.utils.constant;

/**
 * Created by xujiankang on 2016/1/20.
 */
public class RedisConstant {

    /** 项目名称 */
    public static final String SERVER_NAME = "laimi_tuomatuo_";

    /** 用户在 redis 的 hash set 中存储的值 */
    public static final String USER_CACHE_HASH_SET = SERVER_NAME + "user_cache_hash_set";

    /** 在 redis 中存储 geohash 对应的 userId */
    public static final String USER_GEOHASH_COLLECTION = SERVER_NAME + "user_geohash_collection";

}
