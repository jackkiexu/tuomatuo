package com.lami.tuomatuo.service.crawler;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.crawler.UIAccountDaoInterface;
import com.lami.tuomatuo.model.WeiXinAccount;
import com.lami.tuomatuo.model.crawler.UIAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/3/21.
 */
@Service("uiAccountService")
public class UIAccountService extends BaseService<UIAccount, Long> {

    @Autowired
    private UIAccountDaoInterface uiAccountDaoInterface;
}
