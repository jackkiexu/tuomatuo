package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.UserDynamicDaoInterface;
import com.lami.tuomatuo.dao.UserPositionDaoInterface;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.model.UserPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("userPositionService")
public class UserPositionService  extends BaseService<UserPosition, Long> {

    @Autowired
    private UserPositionDaoInterface userPositionDaoInterface;

}
