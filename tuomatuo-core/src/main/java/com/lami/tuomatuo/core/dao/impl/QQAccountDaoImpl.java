package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.core.model.QQAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/18.
 */
@Repository("qqAccountDaoInterface")
public class QQAccountDaoImpl extends MySqlBaseDao<QQAccount, Long> implements QQAccountDaoInterface {
    public QQAccountDaoImpl(){
        super(QQAccount.class);
    }
}