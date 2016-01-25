package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.SmsDaoInterface;
import com.lami.tuomatuo.dao.UserDaoInterface;
import com.lami.tuomatuo.model.Sms;
import com.lami.tuomatuo.model.User;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/1/25.
 */
@Repository("smsDaoInterface")
public class SmsDaoImpl extends BaseDaoMysqlImpl<Sms, Long> implements SmsDaoInterface {
    public SmsDaoImpl(){
        super(Sms.class);
    }
}

