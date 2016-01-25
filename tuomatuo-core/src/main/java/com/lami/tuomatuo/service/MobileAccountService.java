package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.MobileAccountDaoInterface;
import com.lami.tuomatuo.dao.SmsDaoInterface;
import com.lami.tuomatuo.model.MobileAccount;
import com.lami.tuomatuo.model.Sms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/25.
 */
@Service("mobileAccountService")
public class MobileAccountService extends BaseService<MobileAccount, Long> {

    @Autowired
    private MobileAccountDaoInterface mobileAccountDaoInterface;



}