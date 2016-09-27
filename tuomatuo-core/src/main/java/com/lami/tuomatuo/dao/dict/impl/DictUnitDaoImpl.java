package com.lami.tuomatuo.dao.dict.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.dict.DictUnitDaoInterface;
import com.lami.tuomatuo.model.dict.DictUnit;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Repository("dictUnitDaoInterface")
public class DictUnitDaoImpl extends MySqlBaseDao<DictUnit, Long> implements DictUnitDaoInterface {
    public DictUnitDaoImpl(){
        super(DictUnit.class);
    }
}
