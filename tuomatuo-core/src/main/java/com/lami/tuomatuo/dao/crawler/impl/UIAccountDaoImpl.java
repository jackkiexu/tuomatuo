package com.lami.tuomatuo.dao.crawler.impl;

import com.lami.tuomatuo.base.BaseDaoMysqlImpl;
import com.lami.tuomatuo.dao.crawler.UIAccountDaoInterface;
import com.lami.tuomatuo.model.crawler.UIAccount;
import org.springframework.stereotype.Repository;

/**
 * Created by xujiankang on 2016/3/21.
 */
@Repository("uiAccountDaoInterface")
public class UIAccountDaoImpl extends BaseDaoMysqlImpl<UIAccount, Long> implements UIAccountDaoInterface {
    public UIAccountDaoImpl(){
        super(UIAccount.class);
    }
}


