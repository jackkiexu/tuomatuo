package com.lami.tuomatuo.core.dao.crawler.impl;

import com.lami.tuomatuo.core.model.crawler.ChanYouJiAccount;
import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.crawler.ChanYouJiAccountDaoInterface;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/3/22.
 */
@Repository("chanYouJiAccountDaoInterface")
public class ChanYouJiAccountDaoImpl extends MySqlBaseDao<ChanYouJiAccount, Long> implements ChanYouJiAccountDaoInterface {
    public ChanYouJiAccountDaoImpl(){
        super(ChanYouJiAccount.class);
    }
}
