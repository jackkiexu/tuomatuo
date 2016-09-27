package com.lami.tuomatuo.dao.crawler.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.crawler.ChanYouJiAccountDaoInterface;
import com.lami.tuomatuo.model.crawler.ChanYouJiAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/3/22.
 */
@Repository("chanYouJiAccountDaoInterface")
public class ChanYouJiAccountDaoImpl extends MySqlBaseDao<ChanYouJiAccount, Long> implements ChanYouJiAccountDaoInterface {
    public ChanYouJiAccountDaoImpl(){
        super(ChanYouJiAccount.class);
    }
}
