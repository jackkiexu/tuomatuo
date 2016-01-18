package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.UserPositionDaoInterface;
import com.lami.tuomatuo.dao.UserPropertyDaoInterface;
import com.lami.tuomatuo.model.UserPosition;
import com.lami.tuomatuo.model.UserProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("userPropertyService")
public class UserPropertyService extends BaseService<UserProperty, Long> {

    @Autowired
    private UserPropertyDaoInterface userPropertyDaoInterface;

    public UserProperty getUserProperty(Long userId){
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
