package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.SmsDaoInterface;
import com.lami.tuomatuo.core.model.Sms;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/25.
 */
@Repository("smsDaoInterface")
public class SmsDaoImpl extends MySqlBaseDao<Sms, Long> implements SmsDaoInterface {
    public SmsDaoImpl(){
        super(Sms.class);
    }
}

