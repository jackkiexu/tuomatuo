package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.model.DynamicImg;
import com.lami.tuomatuo.model.QQAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Repository("qqAccountDaoInterface")
public class QQAccountDaoImpl extends BaseDaoMysqlImpl<QQAccount, Long> implements QQAccountDaoInterface {
    public QQAccountDaoImpl(){
        super(QQAccount.class);
    }
}