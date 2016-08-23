package com.lami.tuomatuo.service.dict;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.dict.DictWordDaoInterface;
import com.lami.tuomatuo.model.dict.DictWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/8/23.
 */
@Service("dictWordService")
public class DictWordService extends BaseService<DictWord, Long> {

    @Autowired
    private DictWordDaoInterface dictWordDaoInterface;

}
