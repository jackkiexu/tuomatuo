package com.lami.tuomatuo.service.dict;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.dict.DictReviewUnitDaoInterface;
import com.lami.tuomatuo.dao.dict.DictWordDaoInterface;
import com.lami.tuomatuo.model.dict.DictReviewUnit;
import com.lami.tuomatuo.model.dict.DictWord;
import com.lami.tuomatuo.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Service("dictReviceUnitService")
public class DictReviceUnitService extends BaseService<DictReviewUnit, Long> {

    @Autowired
    private DictReviewUnitDaoInterface dictReviewUnitDaoInterface;

    /**
     * 获取用户所有 的 reviewUnit
     * @param userId
     * @return
     */
    public List<DictReviewUnit> getUserAllDictReviewUnit(Long userId, Long unit){
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(userId);
        parameters.add(unit);
        parameters.add(new Date());

        List<DictReviewUnit> dictReviewUnitList = dictReviewUnitDaoInterface.search("select * from dict_review_unit where userId = ? and unit = ?  and endTime > ? order by id desc", parameters);
        return dictReviewUnitList;
    }

}
