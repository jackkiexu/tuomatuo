package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.UserPositionDaoInterface;
import com.lami.tuomatuo.dao.UserPropertyDaoInterface;
import com.lami.tuomatuo.model.UserPosition;
import com.lami.tuomatuo.model.UserProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("userPropertyService")
public class UserPropertyService extends BaseService<UserProperty, Integer> {

    @Autowired
    private UserPropertyDaoInterface userPropertyDaoInterface;

}
