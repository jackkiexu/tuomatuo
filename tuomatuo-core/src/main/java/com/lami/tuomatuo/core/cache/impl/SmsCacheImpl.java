package com.lami.tuomatuo.core.cache.impl;

import com.lami.tuomatuo.core.cache.SmsCache;
import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.utils.constant.Constant;
import com.lami.tuomatuo.core.utils.constant.RedisConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Created by xjk on 2016/1/25.
 */
@Component( value = "smsCache")
public class SmsCacheImpl implements SmsCache {

    private static final Logger logger = Logger.getLogger(SmsCacheImpl.class);

    @Autowired
    private ShardedJedisPool readPool;
    @Autowired
    private ShardedJedisPool writePool;

    public Result increaseGetCodeCountGroupByIP(String IP) {
        Result result = new Result(Result.SUCCESS);
        if(IP == null) return result.setStatus(Result.PARAMCHECKERROR);
        ShardedJedis redis = null;
        try {
            redis = writePool.getResource();
            String json = redis.hget(RedisConstant.GET_CODE_LIMIT_FOR_IP, IP);
            if(json == null){
                redis.hset(RedisConstant.GET_CODE_LIMIT_FOR_IP, IP, 1+"");
                return result;
            }else{
                Integer count = Integer.parseInt(json);
                logger.info("increaseGetCodeCountGroupByIP count:"+ count);
                if(count >= Constant.GET_CODE_LIMIT_FOR_IP){ // 针对单个号码获取验证码的次数超过 50 次
                    logger.info("count:"+count+ "limit:"+ Constant.GET_CODE_LIMIT_FOR_IP);
                    return result.setStatus(Result.COUNT_OUT_OF_LIMIT_GET_CODE_BY_IP);
                }else{
                    count++;
                    redis.hset(RedisConstant.GET_CODE_LIMIT_FOR_IP, IP, count+"");
                    return result;
                }
            }
        } catch (RuntimeException e) {
            logger.info(e.getMessage());
            return result.setStatus(Result.SYSTEM_ERROR);
        }finally{
            if(redis != null){
                writePool.returnResource(redis);
            }
        }

    }

    public Result increaseGetCodeCountGroupByMobile(String mobile) {
        Result result = new Result(Result.SUCCESS);
        if(mobile == null) return result.setStatus(Result.PARAMCHECKERROR);
        ShardedJedis redis = null;
        try {
            redis = writePool.getResource();
            String json = redis.hget(RedisConstant.GET_CODE_LIMIT_FOR_MOBILE, mobile);
            if(json == null){
                redis.hset(RedisConstant.GET_CODE_LIMIT_FOR_MOBILE, mobile, 1+"");
                return result;
            }else{
                Integer count = Integer.parseInt(json);
                logger.info("increaseGetVerificationCodeCountGroupByMobile count:"+ count);
                if(count >= Constant.GET_CODE_LIMIT_FOR_MOBILE){ // 针对单个号码获取验证码的次数超过 50 次
                    logger.info("count:"+count+ "limit:"+ Constant.GET_CODE_LIMIT_FOR_MOBILE);
                    return result.setStatus(Result.COUNT_OUT_OF_LIMIT_GET_CODE_BY_MOBILE);
                }else{
                    count++;
                    redis.hset(RedisConstant.GET_CODE_LIMIT_FOR_MOBILE, mobile, count+"");
                    return result;
                }
            }
        } catch (RuntimeException e) {
            logger.info(e.getMessage());
            return result.setStatus(Result.SYSTEM_ERROR);
        }finally{
            if(redis != null){
                writePool.returnResource(redis);
            }
        }
    }
}
