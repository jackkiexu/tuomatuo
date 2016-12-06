package com.lami.tuomatuo.core.dao.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.WeiXinAccountDaoInterface;
import com.lami.tuomatuo.core.model.WeiXinAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/1/18.
 */
@Repository("weiXinAccountDaoInterface")
public class WeXinAccountDaoImpl extends MySqlBaseDao<WeiXinAccount, Long> implements WeiXinAccountDaoInterface {
    public WeXinAccountDaoImpl(){
        super(WeiXinAccount.class);
    }
}
