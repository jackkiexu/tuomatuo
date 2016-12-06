package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.UserDaoInterface;
import com.lami.tuomatuo.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xjk on 11/2/15.
 */
@Service("userService")
public class UserService extends BaseService<User, Long> {

    @Autowired
    private UserDaoInterface userDaoInterface;

    public User getUserByMobile(String mobile){
        User user = new User();
        user.setMobile(mobile);
        List<User> userList = userDaoInterface.search(user);
        if(userList != null && userList.size() != 0) return userList.get(0);
        return null;
    }

}
