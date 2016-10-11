package com.lami.tuomatuo.model.vo;

import com.lami.tuomatuo.model.Friend;
import com.lami.tuomatuo.model.MobileAccount;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.UserProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by xjk on 2016/1/20.
 */
@Data
public class FriendVo {
    private Long userId;
    private Long friendId;
    private String friendImgUrl;
    private String friendName;
    private Integer friendAge;
    private Integer friendSex;
    private Date friendLastLoginTime; // 朋友上次登录的时间
    private Integer friendUnSeeDynamic; // 还没看过的朋友的动态数

    public FriendVo(){}
    public FriendVo(Friend friend, UserProperty userProperty, User user, MobileAccount mobileAccount){
        this.userId = friend.getUserId();
        this.friendId = friend.getFriendId();
        this.friendLastLoginTime = user.getLastLoginTime();
        this.friendAge = userProperty.getAge();
        this.friendSex = userProperty.getSex();

        this.friendImgUrl = mobileAccount.getImgUrl();
        this.friendName = mobileAccount.getNick();
    }
}
