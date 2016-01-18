package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.UserDaoInterface;
import com.lami.tuomatuo.dao.WeiXinAccountDaoInterface;
import com.lami.tuomatuo.model.User;
import com.lami.tuomatuo.model.WeiXinAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("weiXinAccountService")
public class WeiXinAccountService extends BaseService<WeiXinAccount, Integer> {

    @Autowired
    private WeiXinAccountDaoInterface weiXinAccountDaoInterface;

}
