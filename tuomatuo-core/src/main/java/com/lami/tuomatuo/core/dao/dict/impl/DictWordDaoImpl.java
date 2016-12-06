package com.lami.tuomatuo.core.dao.dict.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.model.dict.DictWord;
import com.lami.tuomatuo.core.dao.dict.DictWordDaoInterface;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/8/23.
 */
@Repository("wordDaoInterface")
public class DictWordDaoImpl extends MySqlBaseDao<DictWord, Long> implements DictWordDaoInterface {
    public DictWordDaoImpl(){
        super(DictWord.class);
    }
}

