package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.UserPropertyDaoInterface;
import com.lami.tuomatuo.core.model.UserProperty;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/18.
 */
@Repository("userPropertyDaoInterface")
public class UserPropertyDaoImpl extends MySqlBaseDao<UserProperty, Long> implements UserPropertyDaoInterface {
    public UserPropertyDaoImpl(){
        super(UserProperty.class);
    }
}