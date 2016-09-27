package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.model.QQAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("qqAccountDaoInterface")
public class QQAccountDaoImpl extends MySqlBaseDao<QQAccount, Long> implements QQAccountDaoInterface {
    public QQAccountDaoImpl(){
        super(QQAccount.class);
    }
}