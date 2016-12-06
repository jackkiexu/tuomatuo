package com.lami.tuomatuo.core.dao.crawler.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.crawler.ChanYoujiDynamicDaoInterface;
import com.lami.tuomatuo.core.model.crawler.ChanYoujiDynamic;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/3/24.
 */
@Repository("chanYoujiDynamicDaoInterface")
public class ChanYouJiDynamicDaoImpl extends MySqlBaseDao<ChanYoujiDynamic, Long> implements ChanYoujiDynamicDaoInterface {
    public ChanYouJiDynamicDaoImpl(){
        super(ChanYoujiDynamic.class);
    }
}
