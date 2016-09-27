package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.MobileAccountDaoInterface;
import com.lami.tuomatuo.model.MobileAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/25.
 */
@Repository("mobileAccountDaoInterface")
public class MobileAccountDaoImpl extends MySqlBaseDao<MobileAccount, Long> implements MobileAccountDaoInterface {
    public MobileAccountDaoImpl(){
        super(MobileAccount.class);
    }
}
