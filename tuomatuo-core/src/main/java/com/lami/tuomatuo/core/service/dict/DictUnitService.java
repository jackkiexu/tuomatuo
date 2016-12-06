package com.lami.tuomatuo.core.service.dict;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.model.vo.DictReviewUnitVo;
import com.lami.tuomatuo.core.dao.dict.DictUnitDaoInterface;
import com.lami.tuomatuo.core.model.dict.DictReviewUnit;
import com.lami.tuomatuo.core.model.dict.DictUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/8/23.
 */
@Service("dictUnitService")
public class DictUnitService extends BaseService<DictUnit, Long> {

    @Autowired
    private DictUnitDaoInterface dictUnitDaoInterface;
    @Autowired
    private DictReviceUnitService dictReviceUnitService;

    /**
     * 获取用户所用的 review information
     * @return
     */
    public List<DictReviewUnitVo> getAllDictReviewUnitVo(Long userId){
        List<DictReviewUnitVo> dictReviewUnitVoList = new ArrayList<DictReviewUnitVo>();

        List<DictUnit> dictUnitList = dictUnitDaoInterface.getAll();
        for(DictUnit dictUnit : dictUnitList){
            DictReviewUnitVo vo = new DictReviewUnitVo();
            vo.setUnitId(dictUnit.getId());
            vo.setName(dictUnit.getName());
            vo.setWordSum(dictUnit.getWordSum());
            vo.setCreateTime(dictUnit.getCreateTime());
            List<DictReviewUnit> dictReviewUnitList = dictReviceUnitService.getUserAllDictReviewUnit(userId, dictUnit.getId());
            if(dictReviewUnitList != null && dictReviewUnitList.size() != 0) vo.setDictReviewUnit(dictReviewUnitList.get(0));
        }

        return dictReviewUnitVoList;
    }

}