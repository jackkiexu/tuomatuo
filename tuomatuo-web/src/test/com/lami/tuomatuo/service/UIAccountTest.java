package com.lami.tuomatuo.service;

import com.lami.tuomatuo.base.AbstractBaseTest;
import com.lami.tuomatuo.model.UserDynamic;
import com.lami.tuomatuo.service.crawler.ChanYouJiAccountService;
import com.lami.tuomatuo.service.crawler.HuPuAccountService;
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
    @Autowired
    private HuPuAccountService huPuAccountService;
    @Autowired
    private ChanYouJiAccountService chanYouJiAccountService;

    @Test
    public void getUserDynamic(){uiAccountService.crawlerUIAccount(1l, 10l);}

    @Test
    public void getHuPuAccount(){
        huPuAccountService.crawlerHuPuAccount(50l, 60l);
    }

    @Test
    public void getChanYouJiAccount(){
        chanYouJiAccountService.crawlerChanYouJi(10l, 15l);
    }
}
