package com.lami.tuomatuo.core.dao.crawler.impl;

import com.lami.tuomatuo.core.base.MySqlBaseDao;
import com.lami.tuomatuo.core.dao.crawler.UIAccountDaoInterface;
import com.lami.tuomatuo.core.model.crawler.UIAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xjk on 2016/3/21.
 */
@Repository("uiAccountDaoInterface")
public class UIAccountDaoImpl extends MySqlBaseDao<UIAccount, Long> implements UIAccountDaoInterface {
    public UIAccountDaoImpl(){
        super(UIAccount.class);
    }
}


