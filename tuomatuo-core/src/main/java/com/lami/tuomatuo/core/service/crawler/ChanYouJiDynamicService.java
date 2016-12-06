package com.lami.tuomatuo.core.service.crawler;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.crawler.ChanYoujiDynamicDaoInterface;
import com.lami.tuomatuo.core.model.crawler.ChanYoujiDynamic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by xjk on 2016/3/24.
 */
@Service("chanYouJiDynamicService")
public class ChanYouJiDynamicService extends BaseService<ChanYoujiDynamic, Long> {

    @Autowired
    private ChanYoujiDynamicDaoInterface chanYoujiDynamicDaoInterface;

    @Resource(name = "chanYoujiDynamicDaoInterface")
    public void setChanYoujiDynamicDaoInterface(ChanYoujiDynamicDaoInterface chanYoujiDynamicDaoInterface) {
        this.chanYoujiDynamicDaoInterface = chanYoujiDynamicDaoInterface;
        this.baseDao = chanYoujiDynamicDaoInterface;
    }
}
