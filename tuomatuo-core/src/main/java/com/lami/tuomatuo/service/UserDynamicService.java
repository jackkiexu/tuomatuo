package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.dao.UserDynamicDaoInterface;
import com.lami.tuomatuo.model.QQAccount;
import com.lami.tuomatuo.model.UserDynamic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("userDynamicService")
public class UserDynamicService extends BaseService<UserDynamic, Long> {

    @Autowired
    private UserDynamicDaoInterface userDynamicDaoInterface;

    public List<UserDynamic> getUserDynamicByUserId(Long userId){
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setUserId(userId);
        return userDynamicDaoInterface.search(userDynamic);
    }

}