package com.lami.tuomatuo.core.model.vo;

import lombok.Data;

/**
 * Created by xjk on 2016/1/20.
 */
@Data
public class UserPositionVo {
    private Long userId;
    private String longitude; // 经度
    private String latitude; // 纬度

    public UserPositionVo(){}

    public UserPositionVo(Long userId, String longitude, String latitude){
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
