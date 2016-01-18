package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.dao.UserPropertyDaoInterface;
import com.lami.tuomatuo.model.DynamicImg;
import com.lami.tuomatuo.model.UserProperty;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("userPropertyDaoInterface")
public class UserPropertyDaoImpl extends BaseDaoMysqlImpl<UserProperty, Long> implements UserPropertyDaoInterface {
    public UserPropertyDaoImpl(){
        super(UserProperty.class);
    }
}