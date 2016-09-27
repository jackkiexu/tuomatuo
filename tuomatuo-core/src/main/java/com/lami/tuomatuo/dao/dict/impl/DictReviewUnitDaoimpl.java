package com.lami.tuomatuo.dao.dict.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.dict.DictReviewUnitDaoInterface;
import com.lami.tuomatuo.model.dict.DictReviewUnit;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Repository("dictReviewUnitDaoInterface")
public class DictReviewUnitDaoimpl extends MySqlBaseDao<DictReviewUnit, Long> implements DictReviewUnitDaoInterface {
    public DictReviewUnitDaoimpl(){
        super(DictReviewUnit.class);
    }
}
