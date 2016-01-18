package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.dao.UserDynamicLoveDaoInterface;
import com.lami.tuomatuo.model.QQAccount;
import com.lami.tuomatuo.model.UserDynamicLove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("userDynamicLoveService")
public class UserDynamicLoveService  extends BaseService<UserDynamicLove, Integer> {

    @Autowired
    private UserDynamicLoveDaoInterface userDynamicLoveDaoInterface;

}