package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.UserDynamicLoveDaoInterface;
import com.lami.tuomatuo.core.model.UserDynamicLove;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/18.
 */
@Repository("userDynamicLoveDaoInterface")
public class UserDynamicLoveDaoImpl extends MySqlBaseDao<UserDynamicLove, Long> implements UserDynamicLoveDaoInterface {
        public UserDynamicLoveDaoImpl(){
            super(UserDynamicLove.class);
        }
}