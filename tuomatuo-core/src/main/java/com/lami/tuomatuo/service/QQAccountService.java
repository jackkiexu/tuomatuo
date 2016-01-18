package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.DynamicImgDaoInterface;
import com.lami.tuomatuo.dao.QQAccountDaoInterface;
import com.lami.tuomatuo.model.DynamicImg;
import com.lami.tuomatuo.model.QQAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/1/18.
 */
@Service("qqAccountService")
public class QQAccountService extends BaseService<QQAccount, Integer> {

    @Autowired
    private QQAccountDaoInterface qqAccountDaoInterface;

}