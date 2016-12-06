package com.lami.tuomatuo.core.dao.dict.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.dict.DictReviewUnitDaoInterface;
import com.lami.tuomatuo.core.model.dict.DictReviewUnit;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/8/23.
 */
@Repository("dictReviewUnitDaoInterface")
public class DictReviewUnitDaoimpl extends MySqlBaseDao<DictReviewUnit, Long> implements DictReviewUnitDaoInterface {
    public DictReviewUnitDaoimpl(){
        super(DictReviewUnit.class);
    }
}
