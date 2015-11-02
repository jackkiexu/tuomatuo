package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.UserDaoInterface;
import com.lami.tuomatuo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xjk on 11/2/15.
 */
@Service("userService")
public class UserService extends BaseService<User, Integer> {

    @Autowired
    private UserDaoInterface userDaoInterface;

}
