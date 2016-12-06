package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.UserPropertyDaoInterface;
import com.lami.tuomatuo.core.model.UserProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xjk on 2016/1/18.
 */
@Service("userPropertyService")
public class UserPropertyService extends BaseService<UserProperty, Long> {

    @Autowired
    private UserPropertyDaoInterface userPropertyDaoInterface;

    public UserProperty getUserPropertyByUserId(Long userId){
        UserProperty userProperty = new UserProperty();
        userProperty.setUserId(userId);
        List<UserProperty> userPropertyList = userPropertyDaoInterface.search(userProperty);
        if(userPropertyList != null && userPropertyList.size() > 0){
            return userPropertyList.get(0);
        }else{
            return null;
        }
    }

}
