package com.lami.tuomatuo.dao.crawler.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.crawler.ChanYouJiAccountDaoInterface;
import com.lami.tuomatuo.dao.crawler.HuPuAccountDaoInterface;
import com.lami.tuomatuo.model.crawler.ChanYouJiAccount;
import com.lami.tuomatuo.model.crawler.HuPuAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/3/22.
 */
@Repository("chanYouJiAccountDaoInterface")
public class ChanYouJiAccountDaoImpl extends BaseDaoMysqlImpl<ChanYouJiAccount, Long> implements ChanYouJiAccountDaoInterface {
    public ChanYouJiAccountDaoImpl(){
        super(ChanYouJiAccount.class);
    }
}
