package com.lami.tuomatuo.service.crawler;

import com.lami.tuomatuo.base.BaseService;
import com.lami.tuomatuo.dao.crawler.ChanYouJiAccountDaoInterface;
import com.lami.tuomatuo.dao.crawler.ChanYoujiDynamicDaoInterface;
import com.lami.tuomatuo.model.crawler.ChanYouJiAccount;
import com.lami.tuomatuo.model.crawler.ChanYoujiDynamic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xujiankang on 2016/3/24.
 */
@Service("chanYouJiDynamicService")
public class ChanYouJiDynamicService extends BaseService<ChanYoujiDynamic, Long> {

    @Autowired
    private ChanYoujiDynamicDaoInterface chanYoujiDynamicDaoInterface;


}
