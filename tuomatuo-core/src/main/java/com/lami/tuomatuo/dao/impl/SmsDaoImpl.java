package com.lami.tuomatuo.dao.impl;

import com.lami.tuomatuo.base.MySqlBaseDao;
import com.lami.tuomatuo.dao.SmsDaoInterface;
import com.lami.tuomatuo.model.Sms;
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

