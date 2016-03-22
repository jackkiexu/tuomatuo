package com.lami.tuomatuo.service.crawler;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.crawler.ChanYouJiAccountDaoInterface;
import com.lami.tuomatuo.dao.crawler.HuPuAccountDaoInterface;
import com.lami.tuomatuo.model.crawler.ChanYouJiAccount;
import com.lami.tuomatuo.model.crawler.UIAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/3/22.
 */
@Service("chanYouJiAccountService")
public class ChanYouJiAccountService  extends BaseService<ChanYouJiAccount, Long> {

    @Autowired
    private ChanYouJiAccountDaoInterface chanYouJiAccountDaoInterface;


}
