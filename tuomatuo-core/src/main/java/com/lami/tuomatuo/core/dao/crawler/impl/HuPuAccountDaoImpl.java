package com.lami.tuomatuo.core.dao.crawler.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.crawler.HuPuAccountDaoInterface;
import com.lami.tuomatuo.core.model.crawler.HuPuAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/3/22.
 */
@Repository("huPuAccountDaoInterface")
public class HuPuAccountDaoImpl extends MySqlBaseDao<HuPuAccount, Long> implements HuPuAccountDaoInterface {
    public HuPuAccountDaoImpl(){
        super(HuPuAccount.class);
    }
}

