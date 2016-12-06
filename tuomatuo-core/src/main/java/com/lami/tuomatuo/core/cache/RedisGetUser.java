package com.lami.tuomatuo.core.cache;

import com.lami.tuomatuo.core.model.base.Result;
import com.lami.tuomatuo.core.utils.constant.Constant;
import com.lami.tuomatuo.core.utils.constant.RedisConstant;

/**
 * Created by xjk on 2016/10/17.
 */
public class RedisGetUser extends RedisCall<String, Result> {

    @Override
    protected Result execute(String IP) {
        Result result = new Result(Result.SUCCESS);
        if(IP == null) return result.setStatus(Result.PARAMCHECKERROR);
        String json = redisRead.hget(RedisConstant.GET_CODE_LIMIT_FOR_IP, IP);
        if(json == null){
            redisRead.hset(RedisConstant.GET_CODE_LIMIT_FOR_IP, IP, 1+"");
            return result;
        }else{
            Integer count = Integer.parseInt(json);
            logger.info("increaseGetCodeCountGroupByIP count:"+ count);
            if(count >= Constant.GET_CODE_LIMIT_FOR_IP){ // 针对单个号码获取验证码的次数超过 50 次
                logger.info("count:"+count+ "limit:"+ Constant.GET_CODE_LIMIT_FOR_IP);
                return result.setStatus(Result.COUNT_OUT_OF_LIMIT_GET_CODE_BY_IP);
            }else{
                count++;
                redisRead.hset(RedisConstant.GET_CODE_LIMIT_FOR_IP, IP, count+"");
                return result;
            }
        }
    }
}
