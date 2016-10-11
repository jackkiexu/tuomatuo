package com.lami.tuomatuo.dao.dict.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.dict.DictWordDaoInterface;
import com.lami.tuomatuo.model.dict.DictWord;
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

