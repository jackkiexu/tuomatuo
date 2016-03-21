package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.AbstractBaseTest;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.service.crawler.UIAccountService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by xujiankang on 2016/3/21.
 */
public class UIAccountTest extends AbstractBaseTest {

    @Autowired
    private UIAccountService uiAccountService;

    @Test
    public void getUserDynamic(){
        uiAccountService.crawlerUIAccount(1l, 10l);
    }
}
