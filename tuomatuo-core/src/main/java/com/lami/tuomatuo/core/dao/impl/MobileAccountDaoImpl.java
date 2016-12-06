package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.MobileAccountDaoInterface;
import com.lami.tuomatuo.core.model.MobileAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/25.
 */
@Repository("mobileAccountDaoInterface")
public class MobileAccountDaoImpl extends MySqlBaseDao<MobileAccount, Long> implements MobileAccountDaoInterface {
    public MobileAccountDaoImpl(){
        super(MobileAccount.class);
    }
}
