package com.lami.tuomatuo.model.vo;

import com.lami.tuomatuo.model.MobileAccount;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.model.UserProperty;
import lombok.Data;

/**
 * Created by xjk on 2016/1/19.
 */
@Data
public class UserDynamicVo {
    private Long id;
    private Long userId;
    private String nick; // 用户的 nick
    private String imgUrl; // 用户 头像
    private Integer sex; // 用户的性别
    private Integer age; // 用户的年龄
    private Long dynamicSeeSum; // 动态被看过的次数
    private Integer type;
    private Long love;
    private Integer fromType;
    private String longitude; // 经度
    private String latitude; // 纬度
    private Long dynamicContentId; // 用户动态内容的id
    private String title; // 用户动态的标题

    public UserDynamicVo(){}
    public UserDynamicVo(UserDynamic userDynamic, UserProperty userProperty, MobileAccount mobileAccount){
        this.type = userDynamic.getType();
        this.love = userDynamic.getLove();
        this.fromType = userDynamic.getFromType();
        this.longitude = userDynamic.getLongitude();
        this.latitude = userDynamic.getLatitude();
        this.dynamicContentId = userDynamic.getDynamicCommentId();
        this.dynamicSeeSum = userDynamic.getDynamicSeeSum();
        this.title = userDynamic.getTitle();

        this.userId = userDynamic.getUserId();
        this.nick = mobileAccount.getNick();
        this.imgUrl = mobileAccount.getImgUrl();
        this.sex = userProperty.getSex();
        this.age = userProperty.getAge();
    }
}
