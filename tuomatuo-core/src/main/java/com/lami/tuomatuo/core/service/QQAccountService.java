package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.core.model.QQAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xjk on 2016/1/18.
 */
@Service("qqAccountService")
public class QQAccountService extends BaseService<QQAccount, Long> {

    @Autowired
    private QQAccountDaoInterface qqAccountDaoInterface;

}