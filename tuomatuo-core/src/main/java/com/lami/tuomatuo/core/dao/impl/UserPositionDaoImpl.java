package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.UserPositionDaoInterface;
import com.lami.tuomatuo.core.model.UserPosition;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/18.
 */
@Repository("userPositionDaoInterface")
public class UserPositionDaoImpl extends MySqlBaseDao<UserPosition, Long> implements UserPositionDaoInterface {
    public UserPositionDaoImpl(){
        super(UserPosition.class);
    }
}
