package com.lami.tuomatuo.model.vo;

import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.UserProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujiankang on 2016/1/19.
 */
@Data
public class UserInfoVo {
    private String imgUrl; // 头像 url
    private String name;
    private Long dynamicSeeTotal; // 所有动态看过的次数
    private Integer sex; // 性别
    private Long dynamicSum; // 该用户的动态数
    private Long followSum; // 用户现在关注人数
    private Long fansSum; // 用户粉丝数
    private List<UserDynamicVo> userDynamicVoList = new ArrayList<UserDynamicVo>(); // 用户动态列表, 每页是10个

    public UserInfoVo(){ }
    public UserInfoVo(User user, UserProperty userProperty){
        this.imgUrl = null;
        this.name = user.getName();
        this.dynamicSeeTotal = userProperty.getDynamicSeeTotal();
        this.sex = userProperty.getSex();
        this.dynamicSum = userProperty.getDynamicSum();
        this.dynamicSeeTotal = userProperty.getDynamicSeeTotal();
        this.followSum = userProperty.getFollowSum();
        this.fansSum = userProperty.getFansSum();
    }
}
