package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.UserDynamicDaoInterface;
import com.lami.tuomatuo.core.model.UserDynamic;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/18.
 */
@Repository("userDynamicDaoInterface")
public class UserDynamicDaoImpl extends MySqlBaseDao<UserDynamic, Long> implements UserDynamicDaoInterface {
    public UserDynamicDaoImpl(){
        super(UserDynamic.class);
    }
}