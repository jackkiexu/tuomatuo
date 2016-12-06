package com.lami.tuomatuo.core.utils.help;

import com.lami.tuomatuo.core.model.enums.UserStatus;
import com.lami.tuomatuo.core.model.User;
import com.lami.tuomatuo.core.model.enums.UserAccount;

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
