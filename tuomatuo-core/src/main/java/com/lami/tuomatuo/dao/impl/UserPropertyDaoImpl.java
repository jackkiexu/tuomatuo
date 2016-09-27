package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.UserPropertyDaoInterface;
import com.lami.tuomatuo.model.UserProperty;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("userPropertyDaoInterface")
public class UserPropertyDaoImpl extends MySqlBaseDao<UserProperty, Long> implements UserPropertyDaoInterface {
    public UserPropertyDaoImpl(){
        super(UserProperty.class);
    }
}