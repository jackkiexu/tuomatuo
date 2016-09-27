package com.lami.tuomatuo.dao.crawler.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.crawler.HuPuAccountDaoInterface;
import com.lami.tuomatuo.model.crawler.HuPuAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/3/22.
 */
@Repository("huPuAccountDaoInterface")
public class HuPuAccountDaoImpl extends MySqlBaseDao<HuPuAccount, Long> implements HuPuAccountDaoInterface {
    public HuPuAccountDaoImpl(){
        super(HuPuAccount.class);
    }
}

