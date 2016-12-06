package com.lami.tuomatuo.core.dao.dict.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.dict.DictUserDaoInterface;
import com.lami.tuomatuo.core.model.dict.DictUser;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/8/23.
 */
@Repository("dictUserDaoInterface")
public class DictUserDaoImpl extends MySqlBaseDao<DictUser, Long> implements DictUserDaoInterface {
    public DictUserDaoImpl(){
        super(DictUser.class);
    }
}
