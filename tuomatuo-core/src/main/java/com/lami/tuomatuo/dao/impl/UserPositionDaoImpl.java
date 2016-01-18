package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.UserDynamicLoveDaoInterface;
import com.lami.tuomatuo.dao.UserPositionDaoInterface;
import com.lami.tuomatuo.model.UserDynamicLove;
import com.lami.tuomatuo.model.UserPosition;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("userPositionDaoInterface")
public class UserPositionDaoImpl extends BaseDaoMysqlImpl<UserPosition, Long> implements UserPositionDaoInterface {
    public UserPositionDaoImpl(){
        super(UserPosition.class);
    }
}
