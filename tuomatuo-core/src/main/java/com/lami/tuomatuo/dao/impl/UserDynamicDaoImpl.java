package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.dao.UserDynamicDaoInterface;
import com.lami.tuomatuo.model.QQAccount;
import com.lami.tuomatuo.model.UserDynamic;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("userDynamicDaoInterface")
public class UserDynamicDaoImpl extends BaseDaoMysqlImpl<UserDynamic, Long> implements UserDynamicDaoInterface {
    public UserDynamicDaoImpl(){
        super(UserDynamic.class);
    }
}