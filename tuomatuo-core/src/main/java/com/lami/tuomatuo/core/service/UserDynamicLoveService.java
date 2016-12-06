package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.UserDynamicLoveDaoInterface;
import com.lami.tuomatuo.core.model.UserDynamicLove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xjk on 2016/1/18.
 */
@Service("userDynamicLoveService")
public class UserDynamicLoveService  extends BaseService<UserDynamicLove, Long> {

    @Autowired
    private UserDynamicLoveDaoInterface userDynamicLoveDaoInterface;

}