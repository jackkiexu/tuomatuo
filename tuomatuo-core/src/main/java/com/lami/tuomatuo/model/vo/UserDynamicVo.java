package com.lami.tuomatuo.model.vo;

import com.lami.tuomatuo.model.UserDynamic;
import lombok.Data;

/**
 * Created by xujiankang on 2016/1/19.
 */
@Data
public class UserDynamicVo extends UserDynamic{
    private Long id;
    private Long userId;
    private Integer type;
    private Long love;
    private Integer fromType;
    private String longitude; // 经度
    private String latitude; // 纬度
    private Long dynamicContentId; // 用户动态内容的id

    public UserDynamicVo(){}
    public UserDynamicVo(UserDynamic userDynamic){
        this.userId = userDynamic.getUserId();
        this.type = userDynamic.getType();
        this.love = userDynamic.getLove();
        this.fromType = userDynamic.getFromType();
        this.longitude = userDynamic.getLongitude();
        this.latitude = userDynamic.getLatitude();
        this.dynamicContentId = userDynamic.getDynamicContentId();
    }
}
