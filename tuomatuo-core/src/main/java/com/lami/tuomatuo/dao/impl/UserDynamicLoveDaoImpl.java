package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.UserDynamicLoveDaoInterface;
import com.lami.tuomatuo.model.UserDynamicLove;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("userDynamicLoveDaoInterface")
public class UserDynamicLoveDaoImpl extends BaseDaoMysqlImpl<UserDynamicLove, Long> implements UserDynamicLoveDaoInterface {
        public UserDynamicLoveDaoImpl(){
            super(UserDynamicLove.class);
        }
}