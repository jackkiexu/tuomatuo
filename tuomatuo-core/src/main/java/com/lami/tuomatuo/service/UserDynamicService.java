package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.dao.UserDynamicDaoInterface;
import com.lami.tuomatuo.model.QQAccount;
import com.lami.tuomatuo.model.UserDynamic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("userDynamicService")
public class UserDynamicService extends BaseService<UserDynamic, Integer> {

    @Autowired
    private UserDynamicDaoInterface userDynamicDaoInterface;

}