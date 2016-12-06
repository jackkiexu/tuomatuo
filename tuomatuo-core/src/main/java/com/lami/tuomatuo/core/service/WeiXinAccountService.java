package com.lami.tuomatuo.core.service;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.WeiXinAccountDaoInterface;
import com.lami.tuomatuo.core.model.WeiXinAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xjk on 2016/1/18.
 */
@Service("weiXinAccountService")
public class WeiXinAccountService extends BaseService<WeiXinAccount, Long> {

    @Autowired
    private WeiXinAccountDaoInterface weiXinAccountDaoInterface;

}
