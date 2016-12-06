package com.lami.tuomatuo.core.service.crawler;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.model.crawler.ChanYouJiAccount;
import com.lami.tuomatuo.core.model.crawler.ChanYoujiDynamic;
import com.lami.tuomatuo.core.model.crawler.vo.ChanYouJiAccountVO;
import com.lami.tuomatuo.core.model.crawler.vo.ChanYouJiVO;
import com.lami.tuomatuo.core.model.crawler.vo.ChanYoujiDynamicVO;
import com.lami.tuomatuo.core.utils.help.crawler.CrawlerChanYouJiAccountHelper;
import com.lami.tuomatuo.core.dao.crawler.ChanYouJiAccountDaoInterface;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/3/22.
 */
@Service("chanYouJiAccountService")
public class ChanYouJiAccountService  extends BaseService<ChanYouJiAccount, Long> {

    @Autowired
    private ChanYouJiAccountDaoInterface chanYouJiAccountDaoInterface;

    @Autowired
    private ChanYouJiDynamicService chanYouJiDynamicService;


    public List<ChanYouJiVO> crawlerChanYouJi(Long minId, Long maxId){

        Long iInit = 1l;
        Long iMax = 10l;
        if(minId != null) iInit = minId;
        if(maxId != null) iMax = maxId;

        ChanYouJiVO chanYouJiVO = new ChanYouJiVO();
        ChanYouJiAccountVO chanYouJiAccountVO = new ChanYouJiAccountVO();
        List<ChanYouJiVO> chanYouJiVOList = new ArrayList<ChanYouJiVO>();

        Long nullCount = 0l;
        String URL = null;
        String referer = null;
        for(;nullCount <= 1000 && iInit <= iMax;iInit++){
            URL = "http://chanyouji.com/users/"+iInit;
            referer = "http://chanyouji.com/users/"+iInit+1;
            chanYouJiVO = CrawlerChanYouJiAccountHelper.getInstance().crawlerChanYouJi(URL, referer, iInit);
            if(chanYouJiVO == null || chanYouJiVO.getChanYouJiAccountVO() == null){
                logger.info("chanYouJiVO == null, and i = " + iInit);
                nullCount += 1l;
                continue;
            }else {
                nullCount = 0l;
            }


            try {
                ChanYouJiAccount chanYouJiAccount = new ChanYouJiAccount();

                try {
                    BeanUtils.copyProperties(chanYouJiAccount, chanYouJiVO.getChanYouJiAccountVO());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


                List<ChanYouJiAccount> chanYouJiAccountList = new ArrayList<ChanYouJiAccount>();
                chanYouJiAccountList.add(chanYouJiAccount);
                chanYouJiAccountDaoInterface.batchSave(chanYouJiAccountList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(ChanYoujiDynamicVO chanYoujiDynamicVO : chanYouJiVO.getChanYoujiDynamicVOList()){
                try {
                    ChanYoujiDynamic chanYoujiDynamic = new ChanYoujiDynamic();
                    try {
                        BeanUtils.copyProperties(chanYoujiDynamic,chanYoujiDynamicVO);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    chanYouJiDynamicService.save(chanYoujiDynamic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return chanYouJiVOList;
    }

}
