package com.lami.tuomatuo.dao.dict.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.dict.DictUserDaoInterface;
import com.lami.tuomatuo.model.dict.DictUser;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Repository("dictUserDaoInterface")
public class DictUserDaoImpl extends MySqlBaseDao<DictUser, Long> implements DictUserDaoInterface {
    public DictUserDaoImpl(){
        super(DictUser.class);
    }
}
