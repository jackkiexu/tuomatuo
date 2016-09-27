package com.lami.tuomatuo.dao.crawler.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.crawler.ChanYoujiDynamicDaoInterface;
import com.lami.tuomatuo.model.crawler.ChanYoujiDynamic;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/3/24.
 */
@Repository("chanYoujiDynamicDaoInterface")
public class ChanYouJiDynamicDaoImpl extends MySqlBaseDao<ChanYoujiDynamic, Long> implements ChanYoujiDynamicDaoInterface {
    public ChanYouJiDynamicDaoImpl(){
        super(ChanYoujiDynamic.class);
    }
}
