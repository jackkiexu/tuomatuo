package com.lami.tuomatuo.service.dict;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.dict.DictReviewUnitDaoInterface;
import com.lami.tuomatuo.dao.dict.DictUnitDaoInterface;
import com.lami.tuomatuo.dao.dict.DictWordDaoInterface;
import com.lami.tuomatuo.model.dict.DictReviewUnit;
import com.lami.tuomatuo.model.dict.DictUnit;
import com.lami.tuomatuo.model.dict.DictWord;
import com.lami.tuomatuo.utils.DateUtils;
import com.lami.tuomatuo.utils.GsonUtils;
import com.lami.tuomatuo.utils.uuid.UUIDFactory;
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
    @Autowired
    private DictUnitDaoInterface dictUnitDaoInterface;
    @Autowired
    private DictWordService dictWordService;

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

    /**
     * 用户开启自己的 reviewUnit
     * @param userId
     * @param unitId
     */
    public DictReviewUnit beginReviewUnit(Long userId, Long unitId){
        /** 1. 查询对应的 DictUnit DictWord
         *  2. 初始化 DictReviewUnit 进行存储
         *  3. 返回初始化的 DictReviewUnit
         */

        DictUnit dictUnit = dictUnitDaoInterface.get(unitId);
        List<String> dictWordList = dictWordService.getUnitWordStr(unitId);

        DictReviewUnit dictReviewUnit = new DictReviewUnit();
        dictReviewUnit.setCreateTime(new Date());
        dictReviewUnit.setEndTime(DateUtils.addDate(new Date(), 2));
        dictReviewUnit.setHasReviewSum(0);
        dictReviewUnit.setReviewOver(0);
        dictReviewUnit.setReviewSum(0);
        dictReviewUnit.setUnit(unitId);
        dictReviewUnit.setUticket(UUIDFactory.shortUUID());
        dictReviewUnit.setUserId(userId);
        dictReviewUnit.setWordAll(GsonUtils.toGson(dictWordList));
        return dictReviewUnitDaoInterface.save(dictReviewUnit);
    }



}
