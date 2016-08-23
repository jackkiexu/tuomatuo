package com.lami.tuomatuo.dao.dict.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.UserDaoInterface;
import com.lami.tuomatuo.dao.dict.DictUserDaoInterface;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.dict.DictUser;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Repository("userDaoInterface")
public class DictUserDaoImpl extends BaseDaoMysqlImpl<DictUser, Long> implements DictUserDaoInterface {
    public DictUserDaoImpl(){
        super(DictUser.class);
    }
}
