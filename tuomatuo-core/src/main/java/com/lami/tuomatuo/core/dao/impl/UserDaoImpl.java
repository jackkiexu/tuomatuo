package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.UserDaoInterface;
import com.lami.tuomatuo.core.model.User;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 11/2/15.
 */
@Repository("userDaoInterface")
public class UserDaoImpl extends MySqlBaseDao<User, Long> implements UserDaoInterface {
    public UserDaoImpl(){
        super(User.class);
    }
}
