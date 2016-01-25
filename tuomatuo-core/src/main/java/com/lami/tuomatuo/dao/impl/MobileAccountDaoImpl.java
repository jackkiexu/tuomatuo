package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.MobileAccountDaoInterface;
import com.lami.tuomatuo.dao.SmsDaoInterface;
import com.lami.tuomatuo.model.MobileAccount;
import com.lami.tuomatuo.model.Sms;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/25.
 */
@Repository("mobileAccountDaoInterface")
public class MobileAccountDaoImpl extends BaseDaoMysqlImpl<MobileAccount, Long> implements MobileAccountDaoInterface {
    public MobileAccountDaoImpl(){
        super(MobileAccount.class);
    }
}
