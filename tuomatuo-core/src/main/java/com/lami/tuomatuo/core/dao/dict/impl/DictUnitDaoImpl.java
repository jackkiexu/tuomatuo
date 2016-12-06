package com.lami.tuomatuo.core.dao.dict.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.dict.DictUnitDaoInterface;
import com.lami.tuomatuo.core.model.dict.DictUnit;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/8/23.
 */
@Repository("dictUnitDaoInterface")
public class DictUnitDaoImpl extends MySqlBaseDao<DictUnit, Long> implements DictUnitDaoInterface {
    public DictUnitDaoImpl(){
        super(DictUnit.class);
    }
}
