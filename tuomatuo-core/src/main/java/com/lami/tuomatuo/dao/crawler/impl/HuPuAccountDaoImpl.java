package com.lami.tuomatuo.dao.crawler.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.crawler.HuPuAccountDaoInterface;
import com.lami.tuomatuo.dao.crawler.UIAccountDaoInterface;
import com.lami.tuomatuo.model.crawler.HuPuAccount;
import com.lami.tuomatuo.model.crawler.UIAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/3/22.
 */
@Repository("huPuAccountDaoInterface")
public class HuPuAccountDaoImpl extends BaseDaoMysqlImpl<HuPuAccount, Long> implements HuPuAccountDaoInterface {
    public HuPuAccountDaoImpl(){
        super(HuPuAccount.class);
    }
}

