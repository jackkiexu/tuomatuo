package com.lami.tuomatuo.utils.help;

import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.enums.UserAccount;
import com.lami.tuomatuo.model.enums.UserStatus;

import java.util.Date;

/**
 * Created by xjk on 2016/1/25.
 */
public class InitHelper {

    public static User initUser (String mobile){
        User user = new User();
        user.setMobile(mobile);
        user.setAccountType(UserAccount.NORMAL.getId());
        user.setCreateTime(new Date());
        user.setLastLoginTime(new Date());
        user.setLastSynMemTime(new Date());
        user.setUpdateTime(new Date());
        user.setStatus(UserStatus.INIT.getId());
        return user;
    }
}
