package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.cache.impl.UserCacheImpl;
import com.lami.tuomatuo.dao.UserDynamicDaoInterface;
import com.lami.tuomatuo.dao.UserPositionDaoInterface;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.model.UserPosition;
import com.lami.tuomatuo.model.co.UserCo;
import com.lami.tuomatuo.model.vo.UserPositionVo;
import com.lami.tuomatuo.utils.GeoHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xjk on 2016/1/18.
 */
@Service("userPositionService")
public class UserPositionService  extends BaseService<UserPosition, Long> {

    @Autowired
    private UserPositionDaoInterface userPositionDaoInterface;
    @Autowired
    private UserCacheImpl userCache;

    /**
     * 上传用户的当前的地理位置
     */
    public void uploadUserPosition(UserPositionVo userPositionVo){
        UserPosition userPosition = new UserPosition();
        userPosition.setUserId(userPositionVo.getUserId());
        userPosition.setLongitude(userPositionVo.getLongitude());
        userPosition.setLatitude(userPositionVo.getLatitude());
        userPosition.setCreateTime(new Date());
        userPosition.setGeohash(GeoHash.geInstance().encode(Double.parseDouble(userPosition.getLongitude()), Double.parseDouble(userPosition.getLatitude())));
        this.save(userPosition);
    }

    /**
     * 上传用户的当前的地理位置 -> redis
     */
    public void uploadUserPositionToMem(UserPositionVo userPositionVo){
        // 得出现在的 geohash 值
        String newGeoHash = GeoHash.geInstance().encode(Double.parseDouble(userPositionVo.getLongitude()), Double.parseDouble(userPositionVo.getLatitude()));
        // 获取老的 geohash 值
        UserCo userCo = userCache.getUserCo(userPositionVo.getUserId());
        // 将老的 geoHash 对应的队列里面的数据进行清除
        if(userCo.getGeoHash() != null){
            userCache.removeUserGeoHashCollection(userCo.getUserId(), userCo.getGeoHash().substring(0,6));
        }
        // 将新的 geohash 的值更新如 UserCo
        userCache.updateGeoHash(userCo.getUserId(), newGeoHash);
        // 将 userId 更新如 新的 geohash 的集合中
        userCache.addUserIdToGeoHashCollection(newGeoHash.substring(0,6), userCo.getUserId());
    }

}
