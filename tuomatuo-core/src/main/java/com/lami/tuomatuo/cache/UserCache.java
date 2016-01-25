package com.lami.tuomatuo.cache;

import com.lami.tuomatuo.model.co.UserCo;

import java.util.Set;

/**
 * Created by xjk on 11/2/15.
 */
public interface UserCache {

    UserCo getUserCo(Long userId);

    void updateGeoHash(Long userId, String newGeoHash);

    void removeUserGeoHashCollection(Long userId, String sourceGeoHash);

    void addUserIdToGeoHashCollection(String geoHash, Long userId);

    Set<String> getGeoHashCollection(String geoHash);

    /**
     * 用户验证码失败次数
     * @param userId
     * @param count
     * @return
     */
    Integer userVerifyCodeFailCount(Long userId, Integer count);
}
