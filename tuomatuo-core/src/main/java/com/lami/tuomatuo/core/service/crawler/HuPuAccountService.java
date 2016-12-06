package com.lami.tuomatuo.core.service.crawler;

import com.lami.tuomatuo.core.base.BaseService;
import com.lami.tuomatuo.core.dao.crawler.HuPuAccountDaoInterface;
import com.lami.tuomatuo.core.model.crawler.HuPuAccount;
import com.lami.tuomatuo.core.model.crawler.UIAccount;
import com.lami.tuomatuo.core.model.crawler.vo.HuPuAccountVO;
import com.lami.tuomatuo.core.utils.help.crawler.CrawlerHuPuAccountHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 2016/3/22.
 */
@Service("huPuAccountService")
public class HuPuAccountService extends BaseService<UIAccount, Long> {

    @Autowired
    private HuPuAccountDaoInterface huPuAccountDaoInterface;

    /**
     *  crawler ui account and storage them
     * @param minId
     * @param maxId
     */
    public void crawlerHuPuAccount(Long minId, Long maxId){

        Long iInit = 1l;
        Long iMax = 10l;
        if(minId != null) iInit = minId;
        if(maxId != null) iMax = maxId;

        HuPuAccountVO huPuAccountVO = new HuPuAccountVO();

        Long nullCount = 0l;
        String URL = null;
        String referer = null;
        for(;nullCount <= 10000 && iInit <= iMax;iInit++){
            try {
                URL = "http://my.hupu.com/"+iInit;
                referer = "http://my.hupu.com/"+iInit;
                huPuAccountVO =  CrawlerHuPuAccountHelper.getInstance().crawlerUIAccount(URL, referer, iInit);
                logger.info("index = " + iInit +", huPuAccountVO:"+huPuAccountVO);
                if(huPuAccountVO == null){
                    nullCount += 1l;
                    continue;
                }else {
                    nullCount = 0l;
                }

                HuPuAccount huPuAccount = new HuPuAccount();
                try {
                    BeanUtils.copyProperties(huPuAccount, huPuAccountVO);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                List<HuPuAccount> huPuAccountList = new ArrayList<HuPuAccount>();
                huPuAccountList.add(huPuAccount);
                huPuAccountDaoInterface.batchSave(huPuAccountList);

                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
